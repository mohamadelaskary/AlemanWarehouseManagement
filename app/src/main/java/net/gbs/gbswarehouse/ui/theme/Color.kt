package net.gbs.gbswarehouse.ui.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color



// ── Your existing dark colors ──────────────────────────────
val BG      = Color(0xFF08111E)
val CARD    = Color(0xFF0C1A2B)
val CARD2   = Color(0xFF0F2035)
val DARK    = Color(0xFF0A1828)
val BORDER  = Color(0xFF152A42)
val BORDER2 = Color(0xFF1E3E5E)
val TEXT    = Color(0xFFE2EDF8)
val MUTED   = Color(0xFF7096B8)
val DIM     = Color(0xFF2E4D6A)

val GREEN   = Color(0xFF00C896)
val ORANGE  = Color(0xFFFF6224)
val YELLOW  = Color(0xFFFFB800)
val PURPLE  = Color(0xFFA78BFA)
val GRAY    = Color(0xFF4A6080)
val SUCC    = Color(0xFF22C55E)
val DANGER  = Color(0xFFEF4444)
val addMaterialButtonBackgroundDark = Color(0xFFC5C7C7)
val addMaterialButtonTextColorDark = Color(0xFF000000)

// ── New light colors ───────────────────────────────────────
val LightBG      = Color(0xFFF0F6FF)
val LightCARD    = Color(0xFFFFFFFF)
val LightCARD2   = Color(0xFFFFE1E1)
val LightDARK    = Color(0xFFD8E6F5)
val LightBORDER  = Color(0xFFB8D0E8)
val LightBORDER2 = Color(0xFF8AAECE)
val LightTEXT    = Color(0xFF0A1828)
val LightMUTED   = Color(0xFF4A6E90)
val LightDIM     = Color(0xFFBDD4EC)
val addMaterialButtonBackgroundLight = Color(0xFF4B4B4B)
val addMaterialButtonTextColorLight = Color(0xFFFFFFFF)


// ── Palette wrapper ────────────────────────────────────────
data class AppColors(
    val bg: Color,
    val card: Color,
    val card2: Color,
    val dark: Color,
    val border: Color,
    val border2: Color,
    val text: Color,
    val muted: Color,
    val dim: Color,
    val green: Color,
    val orange: Color,
    val yellow: Color,
    val purple: Color,
    val gray: Color,
    val succ: Color,
    val danger: Color,
    val isDark: Boolean,
    val addMaterialButtonBackground: Color,
    val addMaterialButtonTextColor: Color,
) {

}

val DarkAppColors = AppColors(
    bg = BG, card = CARD, card2 = CARD2, dark = DARK,
    border = BORDER, border2 = BORDER2, text = TEXT,
    muted = MUTED, dim = DIM, green = GREEN, orange = ORANGE,
    yellow = YELLOW, purple = PURPLE, gray = GRAY,
    succ = SUCC, danger = DANGER, isDark = true,
    addMaterialButtonBackground = addMaterialButtonBackgroundDark,
    addMaterialButtonTextColor = addMaterialButtonTextColorDark
)

val LightAppColors = AppColors(
    bg = LightBG, card = LightCARD, card2 = LightCARD2, dark = LightDARK,
    border = LightBORDER, border2 = LightBORDER2, text = LightTEXT,
    muted = LightMUTED, dim = LightDIM, green = GREEN, orange = ORANGE,
    yellow = YELLOW, purple = PURPLE, gray = GRAY,
    succ = SUCC, danger = DANGER, isDark = false, addMaterialButtonBackground = addMaterialButtonBackgroundLight,
    addMaterialButtonTextColor = addMaterialButtonTextColorLight
)

// ── CompositionLocal ───────────────────────────────────────
val LocalAppColors = compositionLocalOf { LightAppColors }
// Add these two lines at the bottom of Color.kt
val LocalDarkTheme = compositionLocalOf { false }
val LocalThemeToggle = compositionLocalOf<() -> Unit> { {} }