package net.gbs.gbswarehouse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import net.gbs.gbswarehouse.ui.theme.GBSWarehouseJetPackComposeTheme
import net.gbs.gbswarehouse.ui.theme.LocalDarkTheme
import net.gbs.gbswarehouse.ui.theme.LocalThemeToggle
import net.gbs.gbswarehouse.core.preferences.SettingsPrefs
import net.gbs.gbswarehouse.core.session.SessionManager
import java.util.Locale
import javax.inject.Inject

val LocalLanguage = staticCompositionLocalOf { "en" }
val LocalLanguageToggle = staticCompositionLocalOf { { _: String -> } }
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val settingsPrefs = SettingsPrefs(this)
        val initialLang = settingsPrefs.getLanguage()
        val initialIsDark = settingsPrefs.getIsDark()
        updateLocale(initialLang)

        setContent {
            var isDark by remember { mutableStateOf(initialIsDark) }
            var lang by remember { mutableStateOf(initialLang) }

            val toggleLang = { newLang: String ->
                settingsPrefs.saveLanguage(newLang)
                lang = newLang
                updateLocale(newLang)
                recreate() 
            }

            GBSWarehouseJetPackComposeTheme(isDark) {
                val layoutDirection = if (lang == "ar") LayoutDirection.Rtl else LayoutDirection.Ltr
                CompositionLocalProvider(
                    LocalDarkTheme provides isDark,
                    LocalThemeToggle provides { 
                        isDark = !isDark
                        settingsPrefs.saveIsDark(isDark)
                    },
                    LocalLanguage provides lang,
                    LocalLanguageToggle provides toggleLang,
                    LocalLayoutDirection provides layoutDirection
                ) {
                    WarehouseApp()
                }
            }
        }
    }

    private fun updateLocale(lang: String) {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val resources = resources
        val configuration = resources.configuration
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }
}
