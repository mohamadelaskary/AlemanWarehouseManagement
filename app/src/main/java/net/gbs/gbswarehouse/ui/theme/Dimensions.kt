package net.gbs.gbswarehouse.ui.theme

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class AppDimensions(
    val padding: Dp = 14.dp,
    val paddingMedium: Dp = 10.dp,
    val paddingSmall: Dp = 8.dp,
    val paddingExtraSmall: Dp = 4.dp,
    
    val fontSizeTitle: TextUnit = 14.sp,
    val fontSizeSubtitle: TextUnit = 10.sp,
    val fontSizeBody: TextUnit = 12.sp,
    val fontSizeCaption: TextUnit = 9.sp,
    val fontSizeButton: TextUnit = 13.sp,
    val fontSizeHeader: TextUnit = 11.sp,
    
    val bottomBarHeight: Dp = 90.dp
)

val LocalAppDimensions = compositionLocalOf { AppDimensions() }