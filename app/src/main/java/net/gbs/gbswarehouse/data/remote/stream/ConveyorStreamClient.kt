package net.gbs.gbswarehouse.data.remote.stream

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import net.gbs.gbswarehouse.core.constants.Constants
import net.gbs.gbswarehouse.core.preferences.SettingsPrefs
import net.gbs.gbswarehouse.data.local.TokenManager
import net.gbs.gbswarehouse.data.remote.dto.response.ConveyorStatusDto
import okhttp3.Call
import okhttp3.Callback
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.io.IOException
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Live conveyor status stream.
 *
 * Auto-detects how `api/Conveyors/Stream` is served:
 *  1. SignalR hub — `POST .../negotiate` succeeds, then WebSocket transport
 *     (JSON hub protocol) or, as a fallback, the Server-Sent Events
 *     half-transport paired with the HTTP POST half-transport.
 *  2. Plain SSE / newline-delimited JSON — negotiate is not available, the
 *     endpoint simply streams frames such as
 *     `{"conveyor":{...},"type":"conveyorStatus"}`.
 *
 * The returned flow never completes: on any failure it backs off
 * exponentially and reconnects, re-detecting the transport each time.
 */
@Singleton
class ConveyorStreamClient @Inject constructor(
    @Named("StreamClient") private val client: OkHttpClient,
    private val settingsPrefs: SettingsPrefs,
    private val tokenManager: TokenManager
) {

    enum class Status { CONNECTING, LIVE, DISCONNECTED }

    private val _status = MutableStateFlow(Status.DISCONNECTED)
    val status: StateFlow<Status> = _status.asStateFlow()

    private val gson = Gson()
    private val sendMutex = Mutex()

    fun conveyorUpdates(): Flow<ConveyorStatusDto> = flow {
        var backoffMs = INITIAL_BACKOFF_MS
        while (true) {
            _status.value = Status.CONNECTING
            try {
                val negotiation = negotiate()
                val updates = if (negotiation != null) {
                    when {
                        negotiation.transports.isEmpty() || "WebSockets" in negotiation.transports ->
                            signalRWebSocket(negotiation)

                        "ServerSentEvents" in negotiation.transports ->
                            signalRSse(negotiation)

                        else -> rawStream()
                    }
                } else {
                    rawStream()
                }
                updates.collect { update ->
                    backoffMs = INITIAL_BACKOFF_MS
                    emit(update)
                }
                throw IOException("Conveyor stream ended")
            } catch (e: TimeoutCancellationException) {
                _status.value = Status.DISCONNECTED
                delay(backoffMs)
                backoffMs = (backoffMs * 2).coerceAtMost(MAX_BACKOFF_MS)
            } catch (e: CancellationException) {
                _status.value = Status.DISCONNECTED
                throw e
            } catch (e: Exception) {
                _status.value = Status.DISCONNECTED
                delay(backoffMs)
                backoffMs = (backoffMs * 2).coerceAtMost(MAX_BACKOFF_MS)
            }
        }
    }.flowOn(Dispatchers.IO)

    // ---------------------------------------------------------------------
    // URL / headers
    // ---------------------------------------------------------------------

    /** Same scheme/host/port rewriting the normal [BaseUrlInterceptor] applies. */
    private fun httpUrl(path: String, query: List<Pair<String, String>> = emptyList()): HttpUrl {
        val settings = settingsPrefs.getBaseUrl().toHttpUrl()
        val base = Constants.BASE_URL.toHttpUrl().newBuilder()
            .scheme(settings.scheme)
            .host(settings.host)
            .port(settings.port)
            .build()
        return base.newBuilder()
            .addPathSegments(path)
            .apply { query.forEach { (key, value) -> addQueryParameter(key, value) } }
            .build()
    }

    private fun wsUrl(path: String, query: List<Pair<String, String>>): HttpUrl {
        val http = httpUrl(path, query)
        return http.newBuilder()
            .scheme(if (http.scheme == "https") "wss" else "ws")
            .build()
    }

    private fun newRequest(url: HttpUrl): Request.Builder = Request.Builder().url(url).apply {
        tokenManager.getAccessToken()
            ?.takeIf { it.isNotBlank() }
            ?.let { header("Authorization", "Bearer $it") }
    }

    private suspend fun execute(request: Request): Response =
        withContext(Dispatchers.IO) {
            withTimeout(REQUEST_TIMEOUT_MS) {
                suspendCancellableCoroutine { continuation ->
                    val call = client.newCall(request)
                    continuation.invokeOnCancellation { call.cancel() }
                    call.enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            if (continuation.isActive) continuation.resumeWithException(e)
                        }

                        override fun onResponse(call: Call, response: Response) {
                            if (continuation.isActive) continuation.resume(response)
                            else response.close()
                        }
                    })
                }
            }
        }

    // ---------------------------------------------------------------------
    // SignalR negotiate
    // ---------------------------------------------------------------------

    private data class Negotiation(
        val connectionToken: String?,
        val connectionId: String?,
        val transports: List<String>
    ) {
        val id: String
            get() = connectionToken ?: connectionId ?: throw IOException("SignalR negotiate response has no connection token")
    }

    private suspend fun negotiate(): Negotiation? {
        val url = httpUrl(HUB_PATH + "/negotiate", listOf("negotiateVersion" to "1"))
        val attempts = listOf(
            newRequest(url).post(ByteArray(0).toRequestBody(null)).build(),
            newRequest(url).get().build()
        )
        for (request in attempts) {
            val response = execute(request)
            try {
                when {
                    response.code == 404 || response.code == 405 || response.code == 400 -> continue
                    !response.isSuccessful -> throw IOException("SignalR negotiate failed: HTTP ${response.code}")
                }
                val body = response.body.string()
                parseNegotiation(body)?.let { return it }
            } finally {
                response.close()
            }
        }
        return null
    }

    private fun parseNegotiation(body: String): Negotiation? {
        if (body.isBlank()) return null
        val root = runCatching { JsonParser.parseString(body) }.getOrNull() ?: return null
        if (!root.isJsonObject) return null
        val obj = root.asJsonObject
        if (!obj.has("connectionId") && !obj.has("connectionToken")) return null
        if (obj.has("error")) throw IOException("SignalR negotiate error: ${obj.string("error")}")
        val transports = obj.get("availableTransports")
            ?.takeIf { it.isJsonArray }
            ?.asJsonArray
            ?.mapNotNull { element ->
                element.takeIf { it.isJsonObject }?.asJsonObject?.string("transport")
            }
            ?: emptyList()
        return Negotiation(
            connectionToken = obj.string("connectionToken"),
            connectionId = obj.string("connectionId"),
            transports = transports
        )
    }

    // ---------------------------------------------------------------------
    // SignalR over WebSockets
    // ---------------------------------------------------------------------

    private fun signalRWebSocket(negotiation: Negotiation): Flow<ConveyorStatusDto> = flow {
        val request = newRequest(
            wsUrl(HUB_PATH, listOf("id" to negotiation.id))
        ).header("Sec-WebSocket-Protocol", "json.websockets.v1").build()

        val frames = Channel<String>(Channel.UNLIMITED)
        val webSocket = client.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                webSocket.send(HANDSHAKE_REQUEST)
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                text.split(RECORD_SEPARATOR)
                    .filter { it.isNotBlank() }
                    .forEach { frames.trySend(it) }
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                frames.close(IOException("SignalR websocket closed ($code): $reason"))
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                frames.close(t)
            }
        })

        try {
            val handshake = withTimeout(HANDSHAKE_TIMEOUT_MS) { frames.receive() }
            if (handshake.contains("\"error\"", ignoreCase = true)) {
                throw IOException("SignalR handshake rejected: $handshake")
            }
            _status.value = Status.LIVE
            for (frame in frames) {
                when (signalRMessageType(frame)) {
                    TYPE_PING -> webSocket.send(PING_FRAME)
                    TYPE_CLOSE -> throw IOException("SignalR server closed the connection: $frame")
                    else -> extractConveyors(frame).forEach { emit(it) }
                }
            }
            throw IOException("SignalR websocket ended")
        } finally {
            webSocket.cancel()
            frames.cancel()
        }
    }

    // ---------------------------------------------------------------------
    // SignalR over SSE (GET half-transport) + HTTP POST (send half-transport)
    // ---------------------------------------------------------------------

    private fun signalRSse(negotiation: Negotiation): Flow<ConveyorStatusDto> = flow {
        val url = httpUrl(HUB_PATH, listOf("id" to negotiation.id))
        val response = execute(
            newRequest(url)
                .header("Accept", "text/event-stream")
                .header("Cache-Control", "no-cache")
                .get()
                .build()
        )
        var handshakeDone = false
        try {
            if (!response.isSuccessful) throw IOException("SignalR SSE connect failed: HTTP ${response.code}")
            postSignalRMessages(url, HANDSHAKE_REQUEST)
            signalRSseFrames(response).collect { frame ->
                when (signalRMessageType(frame)) {
                    TYPE_PING -> postSignalRMessages(url, PING_FRAME)
                    TYPE_CLOSE -> throw IOException("SignalR server closed the connection: $frame")
                    else -> {
                        if (!handshakeDone) {
                            if (frame.contains("\"error\"", ignoreCase = true)) {
                                throw IOException("SignalR handshake rejected: $frame")
                            }
                            handshakeDone = true
                            _status.value = Status.LIVE
                        } else {
                            extractConveyors(frame).forEach { emit(it) }
                        }
                    }
                }
            }
            throw IOException("SignalR SSE stream ended")
        } finally {
            response.close()
        }
    }

    /** POST half-transport: sends handshake/pings to the hub. Serialized — a second concurrent POST gets HTTP 409. */
    private suspend fun postSignalRMessages(url: HttpUrl, message: String) {
        sendMutex.withLock {
            val response = execute(
                newRequest(url)
                    .post(message.toRequestBody("text/plain; charset=utf-8".toMediaType()))
                    .build()
            )
            try {
                if (response.code == 409) return
                if (!response.isSuccessful) throw IOException("SignalR send failed: HTTP ${response.code}")
            } finally {
                response.close()
            }
        }
    }

    private fun signalRSseFrames(response: Response): Flow<String> = flow {
        val data = mutableListOf<String>()
        val source = response.body.source()
        while (true) {
            val line = source.readUtf8Line() ?: break
            when {
                line.isEmpty() -> {
                    if (data.isNotEmpty()) {
                        val payload = data.joinToString("\n")
                        data.clear()
                        payload.split(RECORD_SEPARATOR)
                            .filter { it.isNotBlank() }
                            .forEach { emit(it) }
                    }
                }
                line.startsWith("data:") -> data.add(line.substring(5).removePrefix(" "))
                line.startsWith(":") -> Unit
                else -> Unit
            }
        }
    }

    // ---------------------------------------------------------------------
    // Plain SSE / newline-delimited JSON stream (non-SignalR endpoint)
    // ---------------------------------------------------------------------

    private fun rawStream(): Flow<ConveyorStatusDto> = flow {
        val request = newRequest(httpUrl(STREAM_PATH))
            .header("Accept", "text/event-stream")
            .header("Cache-Control", "no-cache")
            .get()
            .build()
        val response = execute(request)
        try {
            if (!response.isSuccessful) throw IOException("Conveyor stream connect failed: HTTP ${response.code}")
            _status.value = Status.LIVE
            rawFrames(response).collect { payload ->
                extractConveyors(payload).forEach { emit(it) }
            }
            throw IOException("Conveyor stream ended")
        } finally {
            response.close()
        }
    }

    private fun rawFrames(response: Response): Flow<String> = flow {
        val data = mutableListOf<String>()
        var jsonBuffer = ""
        val source = response.body.source()
        while (true) {
            val line = source.readUtf8Line() ?: break
            val trimmed = line.trimStart()
            when {
                line.isEmpty() -> {
                    if (jsonBuffer.isNotBlank()) {
                        emit(jsonBuffer)
                        jsonBuffer = ""
                    } else if (data.isNotEmpty()) {
                        emit(data.joinToString("\n"))
                        data.clear()
                    }
                }
                line.startsWith("data:") -> data.add(line.substring(5).removePrefix(" "))
                line.startsWith(":") -> Unit
                jsonBuffer.isNotBlank() || trimmed.startsWith("{") || trimmed.startsWith("[") -> {
                    jsonBuffer = if (jsonBuffer.isEmpty()) line else "$jsonBuffer\n$line"
                    if (runCatching { JsonParser.parseString(jsonBuffer) }.isSuccess) {
                        emit(jsonBuffer)
                        jsonBuffer = ""
                    }
                }
                else -> Unit
            }
        }
        if (jsonBuffer.isNotBlank()) emit(jsonBuffer)
    }

    // ---------------------------------------------------------------------
    // Payload extraction (tolerant to SignalR framing and raw envelopes)
    // ---------------------------------------------------------------------

    private fun extractConveyors(payload: String): List<ConveyorStatusDto> {
        if (payload.isBlank()) return emptyList()
        val root = runCatching { JsonParser.parseString(payload) }.getOrNull() ?: return emptyList()
        return collectConveyors(root)
    }

    private fun collectConveyors(element: JsonElement): List<ConveyorStatusDto> {
        if (element.isJsonArray) {
            return element.asJsonArray.flatMap { collectConveyors(it) }
        }
        if (!element.isJsonObject) return emptyList()
        val obj = element.asJsonObject
        val results = mutableListOf<ConveyorStatusDto>()

        obj.get("conveyor")
            ?.takeIf { it.isJsonObject }
            ?.let { runCatching { gson.fromJson(it, ConveyorStatusDto::class.java) }.getOrNull() }
            ?.let { results.add(it) }

        if (results.isEmpty() && obj.get("conveyorId")?.isJsonPrimitive == true) {
            runCatching { gson.fromJson(obj, ConveyorStatusDto::class.java) }
                .getOrNull()
                ?.let { results.add(it) }
        }

        if (results.isEmpty()) {
            obj.get("arguments")
                ?.takeIf { it.isJsonArray }
                ?.asJsonArray
                ?.forEach { argument -> collectConveyors(argument).forEach { results.add(it) } }
        }

        return results
    }

    private fun signalRMessageType(frame: String): Int? {
        val root = runCatching { JsonParser.parseString(frame) }.getOrNull() ?: return null
        if (!root.isJsonObject) return null
        val type = root.asJsonObject.get("type")?.takeIf { it.isJsonPrimitive } ?: return null
        val primitive = type.asJsonPrimitive
        return if (primitive.isNumber) primitive.asInt else null
    }

    private fun JsonObject.string(key: String): String? =
        get(key)?.takeIf { it.isJsonPrimitive }?.asString

    companion object {
        private const val STREAM_PATH = "api/Conveyors/Stream"
        private const val HUB_PATH = "api/Conveyors/Stream"

        /** ASCII Record Separator (0x1E) — SignalR frame delimiter. */
        private const val RECORD_SEPARATOR = "\u001e"
        private const val HANDSHAKE_REQUEST = """{"protocol":"json","version":1}""" + RECORD_SEPARATOR
        private const val PING_FRAME = """{"type":6}""" + RECORD_SEPARATOR

        private const val TYPE_PING = 6
        private const val TYPE_CLOSE = 7

        private const val REQUEST_TIMEOUT_MS = 15_000L
        private const val HANDSHAKE_TIMEOUT_MS = 15_000L
        private const val INITIAL_BACKOFF_MS = 1_000L
        private const val MAX_BACKOFF_MS = 15_000L
    }
}
