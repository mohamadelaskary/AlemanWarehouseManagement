package net.gbs.gbswarehouse.core.preferences

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit

@Singleton
class SettingsPrefs @Inject constructor(
    @ApplicationContext
    private val context: Context
) {

    private val prefs by lazy {
        context.getSharedPreferences(
            PrefKeys.PREFS_NAME,
            Context.MODE_PRIVATE
        )
    }

    fun save(protocol: String, ip: String, port: String) {
        prefs.edit {
            putString(PrefKeys.KEY_PROTOCOL, protocol)
                .putString(PrefKeys.KEY_IP, ip)
                .putString(PrefKeys.KEY_PORT, port)
        }
    }

    fun getProtocol() =
        prefs.getString(PrefKeys.KEY_PROTOCOL, "http") ?: "http"

    fun getIp() =
        prefs.getString(PrefKeys.KEY_IP, "") ?: ""

    fun getPort() =
        prefs.getString(PrefKeys.KEY_PORT, "") ?: ""

    fun saveLanguage(lang: String) {
        prefs.edit {
            putString(PrefKeys.KEY_LANG, lang)
        }
    }

    fun getLanguage() =
        prefs.getString(PrefKeys.KEY_LANG, "en") ?: "en"

    fun saveIsDark(isDark: Boolean) {
        prefs.edit {
            putBoolean(PrefKeys.KEY_IS_DARK, isDark)
        }
    }

    fun getIsDark() =
        prefs.getBoolean(PrefKeys.KEY_IS_DARK, false)

    fun getBaseUrl(): String {

        val protocol = getProtocol()
        val ip = getIp()
        val port = getPort()

        return buildString {
            append(protocol)
            append("://")
            append(ip)

            if (port.isNotBlank()) {
                append(":")
                append(port)
            }

            append("/")
        }
    }
}