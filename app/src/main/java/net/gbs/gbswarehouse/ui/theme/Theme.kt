package net.gbs.gbswarehouse.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.platform.LocalContext

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ── Material3 schemes (you can customize these further) ────
private val M3Dark  = darkColorScheme()
private val M3Light = lightColorScheme()

// ── Global accessor — use AppTheme.colors.bg anywhere ──────
object AppTheme {
    val colors: AppColors
        @Composable
        get() = LocalAppColors.current
}

// ── Your main theme — light is DEFAULT ────────────────────
@Composable
fun GBSWarehouseJetPackComposeTheme(
    isDarkTheme: Boolean = false,           // ← light by default
    content: @Composable () -> Unit
) {
    val appColors = if (isDarkTheme) DarkAppColors else LightAppColors

    // Sync status bar with current theme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view)
                .isAppearanceLightStatusBars = !isDarkTheme
        }
    }

    CompositionLocalProvider(LocalAppColors provides appColors) {
        MaterialTheme(
            colorScheme = if (isDarkTheme) M3Dark else M3Light,
            typography = Typography,
            content = content
        )
    }
}