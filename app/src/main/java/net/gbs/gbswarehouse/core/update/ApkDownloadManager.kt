package net.gbs.gbswarehouse.core.update

import android.content.Context
import android.os.Environment
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import net.gbs.gbswarehouse.core.preferences.SettingsPrefs
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApkDownloadManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val okHttpClient: OkHttpClient,
    private val appUpdateManager: AppUpdateManager,
    private val settingsPrefs: SettingsPrefs
) {
    private val tag = "ApkDownloadManager"

    private fun resolveDownloadUrl(rawUrl: String): String {
        val baseUrl = settingsPrefs.getBaseUrl()
        return try {
            val parsedUrl = rawUrl.toHttpUrl()
            val pathSegments = parsedUrl.encodedPath
            val query = parsedUrl.encodedQuery

            val baseHttpUrl = baseUrl.toHttpUrl()
            val newUrlBuilder = baseHttpUrl.newBuilder()
                .encodedPath(pathSegments)
            if (!query.isNullOrBlank()) {
                newUrlBuilder.encodedQuery(query)
            }
            val resolved = newUrlBuilder.build().toString()
            Log.d(tag, "Resolved download URL from '$rawUrl' to '$resolved' using base URL '$baseUrl'")
            resolved
        } catch (_: Exception) {
            if (rawUrl.startsWith("/")) {
                val base = baseUrl.removeSuffix("/")
                "$base$rawUrl"
            } else {
                rawUrl
            }
        }
    }

    suspend fun downloadApk(downloadUrl: String, expectedFileSize: Long?): File? = withContext(Dispatchers.IO) {
        val resolvedUrl = resolveDownloadUrl(downloadUrl)
        Log.d(tag, "Starting APK download from URL: $resolvedUrl (original: $downloadUrl)")

        val downloadsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) ?: context.filesDir
        if (!downloadsDir.exists()) {
            downloadsDir.mkdirs()
        }
        val apkFile = File(downloadsDir, "update.apk")

        var downloadedBytes = 0L
        var isResume = false

        if (apkFile.exists()) {
            downloadedBytes = apkFile.length()
            if (downloadedBytes > 0 && expectedFileSize != null && downloadedBytes < expectedFileSize) {
                isResume = true
            } else if (downloadedBytes > 0) {
                if (expectedFileSize != null && downloadedBytes == expectedFileSize) {
                    return@withContext apkFile
                }
            }
        }

        val requestBuilder = Request.Builder().url(resolvedUrl)
        if (isResume && downloadedBytes > 0) {
            requestBuilder.header("Range", "bytes=$downloadedBytes-")
            Log.d(tag, "Resuming APK download from byte $downloadedBytes")
        }

        try {
            val response = okHttpClient.newCall(requestBuilder.build()).execute()
            val responseCode = response.code
            val body = response.body

            Log.d(tag, "Download response received: code=$responseCode, message=${response.message}")

            if (responseCode == 206) {
                Log.d(tag, "Server accepted range request (206 Partial Content)")
            } else if (responseCode == 200) {
                Log.d(tag, "Server returned 200 OK, restarting download from 0")
                downloadedBytes = 0L
                isResume = false
            } else if (!response.isSuccessful) {
                val errorBody = body?.string() ?: response.message
                Log.e(tag, "Download failed with HTTP error: code=$responseCode, body=$errorBody")
                appUpdateManager.updateState(UpdateState.Error("Download failed with HTTP error: $responseCode"))
                return@withContext null
            }

            if (body == null) {
                Log.e(tag, "Download failed: Empty response body")
                appUpdateManager.updateState(UpdateState.Error("Empty response body"))
                return@withContext null
            }

            val contentLength = body.contentLength()
            val totalBytes = if (responseCode == 206) {
                downloadedBytes + contentLength
            } else {
                if (contentLength > 0) contentLength else (expectedFileSize ?: 0L)
            }

            val usableSpace = downloadsDir.usableSpace
            val requiredSpace = if (totalBytes > downloadedBytes) (totalBytes - downloadedBytes) else (expectedFileSize ?: 20000000L)
            if (usableSpace < requiredSpace) {
                Log.e(tag, "Insufficient storage space: available=$usableSpace, required=$requiredSpace")
                appUpdateManager.updateState(UpdateState.Error("Insufficient storage space"))
                return@withContext null
            }

            val inputStream: InputStream = body.byteStream()
            val outputStream = FileOutputStream(apkFile, isResume && responseCode == 206)

            val buffer = ByteArray(8192)
            var bytesRead: Int
            var lastEmittedProgress = -1f

            outputStream.use { output ->
                inputStream.use { input ->
                    while (true) {
                        bytesRead = input.read(buffer)
                        if (bytesRead == -1) break
                        output.write(buffer, 0, bytesRead)
                        downloadedBytes += bytesRead

                        val progress = if (totalBytes > 0) {
                            (downloadedBytes.toFloat() / totalBytes.toFloat()).coerceIn(0f, 1f)
                        } else 0f

                        if (progress - lastEmittedProgress >= 0.01f || progress >= 1f) {
                            lastEmittedProgress = progress
                            appUpdateManager.updateState(
                                UpdateState.Downloading(
                                    progress = progress,
                                    downloadedBytes = downloadedBytes,
                                    totalBytes = totalBytes
                                )
                            )
                        }
                    }
                }
            }

            Log.d(tag, "APK download completed successfully: file=${apkFile.absolutePath}, size=${apkFile.length()}")
            return@withContext apkFile
        } catch (e: Exception) {
            Log.e(tag, "Error downloading APK", e)
            appUpdateManager.updateState(UpdateState.Error(e.message ?: "Download error"))
            return@withContext null
        }
    }
}
