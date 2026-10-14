package net.gbs.gbswarehouse

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import net.gbs.gbswarehouse.core.loading.LoadingNavigation
import net.gbs.gbswarehouse.service.LoadingService
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

    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleLoadingIntent(intent)
        askForNotificationPermission()
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

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleLoadingIntent(intent)
    }

    private fun askForNotificationPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
        val granted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
        if (!granted) {
            requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun handleLoadingIntent(intent: Intent?) {
        val operationId =
            intent?.getStringExtra(LoadingService.EXTRA_OPERATION_ID)?.takeIf { it.isNotBlank() }
                ?: return
        // Consume the extra so a later configuration change or recreate() does not
        // replay the pending navigation.
        intent.removeExtra(LoadingService.EXTRA_OPERATION_ID)
        LoadingNavigation.request(operationId)
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
