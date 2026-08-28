package net.gbs.gbswarehouse.ui.settings

import android.app.Application
import android.util.Log
import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.gbs.gbswarehouse.core.preferences.SettingsPrefs
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import javax.inject.Inject


// ── UI State ──────────────────────────────────────────────────────────────────
sealed class SettingsUiState {
    object Idle    : SettingsUiState()
    object Loading : SettingsUiState()
    data class Success(val message: String) : SettingsUiState()
    data class Error(val message: String)   : SettingsUiState()
}

// ── ViewModel ─────────────────────────────────────────────────────────────────
@HiltViewModel
class SettingsViewModel @Inject constructor(
    app: Application,
    private val settingsPrefs: SettingsPrefs
) : AndroidViewModel(app) {

    // ── Form state ─────────────────────────────────────────────────────────
    var protocol  by mutableStateOf("http")
    var ip        by mutableStateOf("")
    var port      by mutableStateOf("")

    // ── Validation errors ──────────────────────────────────────────────────
    var ipError   by mutableStateOf<String?>(null)
    var portError by mutableStateOf<String?>(null)

    // ── UI state ───────────────────────────────────────────────────────────
    var uiState   by mutableStateOf<SettingsUiState>(SettingsUiState.Idle)

    init {
        port = settingsPrefs.getPort()
        ip   = settingsPrefs.getIp()
        protocol = settingsPrefs.getProtocol()
    }
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()


    // ── Validation ─────────────────────────────────────────────────────────
    private fun validate(): Boolean {
        var valid = true

        ipError = when {
            ip.isBlank() -> "هذا الحقل مطلوب"
            !Patterns.IP_ADDRESS.matcher(ip.trim()).matches() -> "عنوان IP غير صحيح"
            else -> null
        }
        if (ipError != null) valid = false

        portError = when {
            port.isBlank() -> "هذا الحقل مطلوب"
            port.toIntOrNull() == null -> "يجب أن يكون رقماً"
            port.toInt() !in 1..65535  -> "البورت بين 1 و 65535"
            else -> null
        }
        if (portError != null) valid = false

        return valid
    }

    // ── Save button clicked ────────────────────────────────────────────────
    fun onSaveButtonClicked() {
        if (!validate()) return

        viewModelScope.launch {
            uiState = SettingsUiState.Loading
            val result = testConnection()
            uiState = if (result == null) {
                settingsPrefs.save( protocol, ip.trim(), port.trim())
                SettingsUiState.Success("تم الاتصال بنجاح وحفظ الإعدادات ✓")
            } else {
                SettingsUiState.Error(result)
            }
        }
    }

    // ── API connectivity test ──────────────────────────────────────────────
    private suspend fun testConnection(): String? = withContext(Dispatchers.IO) {
        try {
            val url = "$protocol://${ip.trim()}:${port.trim()}/api/AccountHH/TestConnection"
            val request = Request.Builder().url(url).get().build()
            httpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) return@withContext "خطأ في الخادم: ${response.code}"
                
                val body = response.body?.string() ?: return@withContext "استجابة فارغة"
                Log.d("SettingsViewModel", "testConnection: $body")
                val json = JSONObject(body)
                val responseStatus = json.getJSONObject("responseStatus")
                val isSuccess = responseStatus.getBoolean("isSuccess")
                
                if (isSuccess) null 
                else responseStatus.optString("StatusMessage", "فشل الاتصال")
            }
        } catch (e: java.net.SocketTimeoutException) {
            Log.e("SettingsViewModel", "testConnection: timeout", e)
            "انتهت مهلة الاتصال (30 ثانية) - تحقق من عنوان IP والمنفذ وتأكد أن السيرفر يعمل"
        } catch (e: java.net.ConnectException) {
            Log.e("SettingsViewModel", "testConnection: connect error", e)
            "فشل الاتصال: السيرفر غير متاح أو العنوان خطأ"
        } catch (e: Exception) {
            Log.e("SettingsViewModel", "testConnection: ", e)
            "تعذّر الاتصال: ${e.localizedMessage ?: "خطأ غير معروف"}"
        }
    }

    fun resetState() { uiState = SettingsUiState.Idle }
}