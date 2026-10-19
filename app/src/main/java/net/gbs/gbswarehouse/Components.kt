package net.gbs.gbswarehouse


import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import net.gbs.gbswarehouse.ui.MaterialImageButton
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import net.gbs.gbswarehouse.core.session.SessionManager
import net.gbs.gbswarehouse.domain.model.MaterialOnHandStock
import net.gbs.gbswarehouse.domain.model.ValuationType
import net.gbs.gbswarehouse.domain.model.Warehouse
import net.gbs.gbswarehouse.ui.plc.PLC_ACCENT
import net.gbs.gbswarehouse.ui.theme.AppTheme
import net.gbs.gbswarehouse.ui.theme.CairoFont
import net.gbs.gbswarehouse.ui.theme.LocalDarkTheme
import net.gbs.gbswarehouse.ui.theme.LocalThemeToggle
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LanguageToggleButton(modifier: Modifier = Modifier) {
    val currentLang = LocalLanguage.current
    val toggleLang = LocalLanguageToggle.current
    val colors = AppTheme.colors

    IconButton(
        onClick = {
            val newLang = if (currentLang == "en") "ar" else "en"
            toggleLang(newLang)
        },
        modifier = modifier
            .clip(CircleShape)
            .background(colors.card)
            .border(1.dp, colors.border, CircleShape)
    ) {
        Text(
            text = if (currentLang == "en") "AR" else "EN",
            color = colors.muted,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun LogoutButton( modifier: Modifier = Modifier, onClick: () -> Unit) {
    val colors = AppTheme.colors
    IconButton(
        onClick = onClick,
        modifier = modifier
            .clip(CircleShape)
            .background(colors.card)
            .border(1.dp, colors.border, CircleShape)
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.Logout,
            contentDescription = stringResource(R.string.logout),
            tint = DANGER
        )
    }
}

@Composable
fun ThemeToggleButton(modifier: Modifier = Modifier) {
    val colors   = AppTheme.colors
    val isDark   = LocalDarkTheme.current
    val onToggle = LocalThemeToggle.current

    IconButton(
        onClick = onToggle,
        modifier = modifier
            .clip(CircleShape)
            .background(colors.card)
            .border(1.dp, colors.border, CircleShape)
    ) {
        Icon(
            imageVector = if (isDark) Icons.Default.WbSunny else Icons.Default.NightsStay,
            contentDescription = if (isDark) stringResource(R.string.theme_light) else stringResource(R.string.theme_dark),
            tint = if (isDark) YELLOW else colors.muted
        )
    }
}
@Composable
fun SettingsButton(modifier: Modifier = Modifier,onClick: () -> Unit) {
    val colors   = AppTheme.colors
    IconButton(
        onClick = onClick,
        modifier = modifier
            .clip(CircleShape)
            .background(colors.card)
            .border(1.dp, colors.border, CircleShape)
    ) {
        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = stringResource(R.string.settings),
            tint = colors.muted
        )
    }
}
@Composable
fun CarCodeInput(value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier,withLabel: Boolean = true) {
    Field(
        label = stringResource(R.string.car_number),
        hint = stringResource(R.string.car_number_hint),
        value = value,
        onValueChange = onValueChange,
        isNumber = false,
        center = true,
        ltr = true,
        withLabel = withLabel,
        modifier = modifier
    )
}

// ── Bottom Sheet Dialog ───────────────────────────────────────────────────────
@Composable
fun BottomSheetDialog(title: String, onClose: () -> Unit, content: @Composable ColumnScope.() -> Unit) {
    val colors = AppTheme.colors
    val dims = AppTheme.dims
    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
                .background(Color.Black.copy(alpha = 0.65f))
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { onClose() },
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(colors.card)
                    .border(1.dp, colors.border,
                        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .heightIn(max = 560.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { /* consume */ }
            ) {
                // Dialog Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp, color = colors.border,
                            shape = RoundedCornerShape(0.dp)
                        )
                        .padding(horizontal = dims.padding, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("✕", color = colors.muted, fontSize = 18.sp,
                        modifier = Modifier.clickable { onClose() }.padding(horizontal = 2.dp))
                    Text(title, color = colors.text, fontSize = dims.fontSizeButton, fontWeight = FontWeight.Bold)
                }
                content()
            }
        }
    }
}
// ── TopBar (Hdr) ─────────────────────────────────────────────────────────────
@Composable
fun TopBar(
    title: String,
    subtitle: String? = null,
    accent: Color = GREEN,
    onBack: () -> Unit,
    navController: NavController? = null) {
    val colors = AppTheme.colors
    val dims = AppTheme.dims
    val actualNavController = navController ?: LocalNavController.current
    val sessionManager = SessionManager()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.card)
            .border(width = 1.dp, color = colors.border, shape = RoundedCornerShape(0.dp))
            .padding(horizontal = dims.padding, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(colors.dark)
                .border(1.dp, colors.border2, RoundedCornerShape(8.dp))
                .clickable { onBack() },
            contentAlignment = Alignment.Center
        ) {
            Text(if (LocalLanguage.current == "ar") ">" else "<", color = colors.text, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = colors.text,
                fontSize = dims.fontSizeTitle,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (subtitle != null) {
                Text(text = subtitle, color = accent, fontSize = dims.fontSizeSubtitle, fontWeight = FontWeight.SemiBold)
            }
        }
        ThemeToggleButton()
        LogoutButton( onClick = {
            actualNavController.navigate(Screens.LoginScreen.route) {
                popUpTo(0) { inclusive = true }
            }
            sessionManager.logout()
        })
    }
}

// ── Section (Sec) ─────────────────────────────────────────────────────────────
@Composable
fun Section(label: String, accent: Color, content: @Composable ColumnScope.() -> Unit) {
    val colors = AppTheme.colors
    val dims = AppTheme.dims

    Column(modifier = Modifier.padding(bottom = dims.padding)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(bottom = dims.paddingSmall)
        ) {
            Box(modifier = Modifier
                .width(3.dp).height(12.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(accent))
            Text(label, color = accent, fontSize = dims.fontSizeHeader, fontWeight = FontWeight.Bold,
                letterSpacing = 0.6.sp)
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(colors.card)
                .border(1.dp, colors.border, RoundedCornerShape(12.dp))
                .padding(dims.paddingMedium)
        ) {
            content()
        }
    }
}

// ── Pill ──────────────────────────────────────────────────────────────────────
@Composable
fun Pill(label: String, color: Color, modifier: Modifier?=Modifier) {
    Box(
        modifier = modifier?: Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(label, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

class TwoTapKeyboardState(private val keyboardController: SoftwareKeyboardController?) {
    var isFocused by mutableStateOf(false)
    var isKeyboardEnabled by mutableStateOf(false)

    val isReadOnly: Boolean
        get() = isFocused && !isKeyboardEnabled

    fun onTap() {
        if (!isFocused) {
            isFocused = true
            isKeyboardEnabled = false
            keyboardController?.hide()
        } else if (!isKeyboardEnabled) {
            isKeyboardEnabled = true
            keyboardController?.show()
        } else {
            keyboardController?.show()
        }
    }

    fun onFocusChanged(focused: Boolean) {
        isFocused = focused
        if (!focused) {
            isKeyboardEnabled = false
            keyboardController?.hide()
        }
    }
}

@Composable
fun rememberTwoTapKeyboardState(): TwoTapKeyboardState {
    val keyboardController = LocalSoftwareKeyboardController.current
    return remember { TwoTapKeyboardState(keyboardController) }
}

@Composable
fun Modifier.twoTap(
    state: TwoTapKeyboardState,
    enabled: Boolean = true,
    focusRequester: FocusRequester = remember { FocusRequester() }
): Modifier {
    return this
        .focusRequester(focusRequester)
        .onFocusChanged { state.onFocusChanged(it.isFocused) }
        .then(
            if (enabled) {
                Modifier.clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    if (!state.isFocused) {
                        focusRequester.requestFocus()
                    }
                    state.onTap()
                }
            } else {
                Modifier
            }
        )
}

// ── Field ─────────────────────────────────────────────────────────────────────
@Composable
fun Field(
    label: String,
    hint: String,
    value: String,
    onValueChange: (String) -> Unit,
    isNumber: Boolean = false,
    center: Boolean = false,
    ltr: Boolean = false,
    withLabel: Boolean = true,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    val twoTapState = rememberTwoTapKeyboardState()

    Column(modifier = modifier) {
        if (withLabel)
            Text(label, color = colors.muted, fontSize = 10.sp, fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 5.dp))
        BasicTextField(
            value = value,
            onValueChange = { if (enabled && !twoTapState.isReadOnly) onValueChange(it) },
            enabled = enabled,
            singleLine = true,
            readOnly = twoTapState.isReadOnly,
            keyboardOptions = if (isNumber) KeyboardOptions(keyboardType = KeyboardType.Number)
            else KeyboardOptions.Default,
            textStyle = TextStyle(
                color = if (enabled) colors.text else colors.dim,
                fontSize = if (isNumber) 16.sp else 13.sp,
                fontWeight = if (isNumber) FontWeight.Bold else FontWeight.Normal,
                textAlign = if (center) TextAlign.Center else (if (LocalLanguage.current == "ar") TextAlign.Right else TextAlign.Left),
                textDirection = if (ltr) TextDirection.Ltr else (if (LocalLanguage.current == "ar") TextDirection.Rtl else TextDirection.Ltr)
            ),
            cursorBrush = SolidColor(GREEN),
            modifier = Modifier
                .fillMaxWidth()
                .twoTap(twoTapState, enabled)
                .clip(RoundedCornerShape(8.dp))
                .background(if (enabled) colors.dark else colors.dark.copy(alpha = 0.5f))
                .border(1.dp, if (enabled) colors.border2 else colors.dim, RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 10.dp),
            decorationBox = { inner ->
                if (value.isEmpty()) {
                    Text(hint, color = colors.muted, fontSize = if (isNumber) 16.sp else 13.sp,
                        textAlign = if (center) TextAlign.Center else (if (LocalLanguage.current == "ar") TextAlign.Right else TextAlign.Left),
                        modifier = Modifier.fillMaxWidth())
                }
                inner()
            }
        )
    }
}

// ── SearchBox ─────────────────────────────────────────────────────────────────
@Composable
fun SearchBox(value: String, onValueChange: (String) -> Unit, placeholder: String,
              modifier: Modifier = Modifier) {
    val colors = AppTheme.colors
    val twoTapState = rememberTwoTapKeyboardState()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(colors.dark)
            .border(1.dp, colors.border2, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("🔍", fontSize = 13.sp, color = colors.muted)
        Spacer(Modifier.width(8.dp))
        BasicTextField(
            value = value,
            onValueChange = { if (!twoTapState.isReadOnly) onValueChange(it) },
            singleLine = true,
            readOnly = twoTapState.isReadOnly,
            textStyle = TextStyle(color = colors.text, fontSize = 12.sp, textDirection = if (LocalLanguage.current == "ar") TextDirection.Rtl else TextDirection.Ltr),
            cursorBrush = SolidColor(GREEN),
            modifier = Modifier
                .weight(1f)
                .twoTap(twoTapState)
                .padding(vertical = 9.dp),
            decorationBox = { inner ->
                if (value.isEmpty()) Text(placeholder, color = colors.muted, fontSize = 12.sp)
                inner()
            }
        )
        if (value.isNotEmpty()) {
            Text("✕", color = colors.muted, fontSize = 13.sp,
                modifier = Modifier.clickable { onValueChange("") }.padding(4.dp))
        }

    }
}
@Composable
fun OrderNumberSearchBox(value: String, onValueChange: (String) -> Unit,onSearchButtonClicked: (String) -> Unit, placeholder: String,
              modifier: Modifier = Modifier) {
    val colors = AppTheme.colors
    val twoTapState = rememberTwoTapKeyboardState()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(colors.dark)
            .border(1.dp, colors.border2, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicTextField(
            value = value,
            onValueChange = { if (!twoTapState.isReadOnly) onValueChange(it) },
            singleLine = true,
            readOnly = twoTapState.isReadOnly,
            textStyle = TextStyle(color = colors.text, fontSize = 12.sp, textDirection = if (LocalLanguage.current == "ar") TextDirection.Rtl else TextDirection.Ltr),
            cursorBrush = SolidColor(GREEN),
            modifier = Modifier
                .weight(1f)
                .twoTap(twoTapState)
                .padding(vertical = 9.dp),
            decorationBox = { inner ->
                if (value.isEmpty()) Text(placeholder, color = colors.muted, fontSize = 12.sp)
                inner()
            }
        )
        if (value.isNotEmpty()) {
            Text("✕", color = colors.muted, fontSize = 13.sp,
                modifier = Modifier.clickable { onValueChange("") }.padding(4.dp))
            Text("🔍", color = colors.muted, fontSize = 13.sp,
                modifier = Modifier.clickable { onSearchButtonClicked(value) }.padding(4.dp))
        }

    }
}

// ── OutlineButton (Btn1) ──────────────────────────────────────────────────────
@Composable
fun OutlineBtn(label: String, color: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    var hovered by remember { mutableStateOf(false) }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (hovered) color.copy(0.22f) else color.copy(0.12f))
            .border(1.dp, color.copy(0.28f), RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Text(label, color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold,
            maxLines = 1)
    }
}

// ── FullButton (Btn2) ─────────────────────────────────────────────────────────
@Composable
fun FullBtn(
    label: String,
    color: Color,
    onClick: () -> Unit,
    saving: Boolean = false,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    val dims = AppTheme.dims
    val bg     = if (!enabled) colors.dim.copy(0.22f) else color.copy(0.12f)
    val border = if (!enabled) colors.dim else color.copy(0.35f)
    val txtCol = if (!enabled) colors.dim else color

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(10.dp))
            .clickable(enabled = enabled && !saving) { onClick() }
            .padding(vertical = 13.dp),
        contentAlignment = Alignment.Center
    ) {
        if (saving) {
            CircularProgressIndicator(
                color = color,
                modifier = Modifier.size(18.dp),
                strokeWidth = 2.dp
            )
        } else {
            Text(label, color = txtCol, fontSize = dims.fontSizeButton, fontWeight = FontWeight.ExtraBold)
        }
    }
}

// ── SuccessCard ───────────────────────────────────────────────────────────────
@Composable
fun SuccessCard(kind: String?, accent: Color) {
    val isAccept = kind == "accept"
    val isReject = kind == "reject"
    val col   = if (isAccept) SUCC else if (isReject) DANGER else accent
    val label = if (isAccept) stringResource(R.string.success_accept) else if (isReject) stringResource(R.string.success_reject) else stringResource(R.string.success_save)
    val icon  = if (isReject) "✕" else "✓"
    val colors = AppTheme.colors
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(col.copy(0.07f))
            .border(1.dp, col.copy(0.28f), RoundedCornerShape(12.dp))
            .padding(24.dp)
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(col.copy(0.14f))
                .border(2.dp, col, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(icon, color = col, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(12.dp))
        Text(label, color = col, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(4.dp))
        Text(stringResource(R.string.transaction_recorded), color = colors.muted, fontSize = 11.sp)
    }
}
@Composable
fun SuccessDialogCard(
    label: String,
    message: String,
    accent: Color,
    loadingButtonText: String? = null,
    onLoadingClick: (() -> Unit)? = null
) {
    val colors = AppTheme.colors

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(colors.bg)
            .border(1.dp, accent.copy(0.28f), RoundedCornerShape(12.dp))
            .padding(24.dp)
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(colors.bg)
                .border(2.dp, accent, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("✓", color = accent, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(12.dp))
        Text(label, color = accent, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(4.dp))
        Text(message, color = colors.muted, fontSize = 11.sp)

        if (loadingButtonText != null && onLoadingClick != null) {
            Spacer(Modifier.height(16.dp))
            FullBtn(
                label = loadingButtonText,
                color = PLC_ACCENT,
                onClick = onLoadingClick
            )
        }
    }
}
@Composable
fun ErrorCard(label: String,message: String, accent: Color) {
    val colors = AppTheme.colors

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(accent.copy(0.07f))
            .border(1.dp, accent.copy(0.28f), RoundedCornerShape(12.dp))
            .padding(24.dp)
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(accent.copy(0.14f))
                .border(2.dp, accent, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("✕", color = accent, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(12.dp))
        Text(label, color = accent, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(4.dp))
        Text(message, color = colors.muted, fontSize = 11.sp)
    }
}
@Composable
fun ErrorDialogCard(label: String,message: String, accent: Color) {
    val colors = AppTheme.colors

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(colors.bg)
            .border(1.dp, accent.copy(0.28f), RoundedCornerShape(12.dp))
            .padding(24.dp)
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(colors.bg)
                .border(2.dp, accent, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("✕", color = accent, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(12.dp))
        Text(label, color = accent, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(4.dp))
        Text(message, color = colors.muted, fontSize = 11.sp)
    }
}
@Composable
fun SuccessDialog(
    accent: Color,
    message: String,
    loadingButtonText: String? = null,
    onLoadingClick: (() -> Unit)? = null,
    onClose: () -> Unit
){
    val colors = AppTheme.colors
    Dialog(onDismissRequest = onClose) {
        Box(modifier = Modifier.wrapContentSize()) {

            SuccessDialogCard(
                label = stringResource(R.string.success),
                message = message,
                accent = accent,
                loadingButtonText = loadingButtonText,
                onLoadingClick = onLoadingClick
            )
            IconButton(onClick = {
                onClose()
            }, modifier = Modifier.align(Alignment.TopEnd).padding(top = 10.dp, end = 10.dp),){
                Icon(imageVector = Icons.Default.Close, contentDescription = null, tint = colors.muted)
            }
        }
    }
}
@Composable
fun ErrorDialog(accent: Color,message: String,onClose: () -> Unit){
    val colors = AppTheme.colors
    Dialog(onDismissRequest = onClose) {
        Box(modifier = Modifier.wrapContentSize()) {

            ErrorDialogCard(
                label = stringResource(R.string.error),
                message = message,
                accent = accent,
            )
            IconButton(
                onClick = {
                    onClose()
                },
                modifier = Modifier.align(Alignment.TopEnd).padding(top = 10.dp, end = 10.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                    tint = colors.muted
                )
            }
        }
    }
}
// ── InfoCell ──────────────────────────────────────────────────────────────────
@Composable
fun InfoCell(
    label: String,
    value: String,
    color: Color = AppTheme.colors.text,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    val dims = AppTheme.dims
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(colors.dark)
            .padding(dims.paddingSmall),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            label, color = colors.muted, fontSize = dims.fontSizeCaption, fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 2.dp)
        )
        Text(value, color = color, fontSize = dims.fontSizeBody, fontWeight = FontWeight.Bold)
    }
}
@Composable
fun InspectionSelectedMaterialCard(
    mat: Material, accent: Color, isQuality: Boolean, isIssue: Boolean,
    doneLabel: String, smpQty: String, onSmpQtyChange: (String) -> Unit,
    qty: String, onQtyChange: (String) -> Unit,
    saved: Boolean, saving: Boolean,
    onAccept: () -> Unit, onReject: () -> Unit, onClear: () -> Unit,
    onQualityDecisionChange: (QualityDecision) -> Unit
) {
    val progress = if (mat.qty > 0) mat.done.toFloat() / mat.qty else 0f
    val colors = AppTheme.colors
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(accent.copy(0.06f))
            .border(1.dp, accent.copy(0.22f), RoundedCornerShape(10.dp))
            .padding(12.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(mat.code, color = accent, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(Modifier.width(8.dp))
                if (mat.code.isNotEmpty()) {
                    MaterialImageButton(materialCode = mat.code, accent = accent)
                }
            }
            Text("✕", color = colors.muted, fontSize = 12.sp,
                modifier = Modifier.clickable { onClear() }.padding(4.dp))
        }
        Spacer(Modifier.height(4.dp))
        Text(mat.desc, color = colors.text, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
            lineHeight = 18.sp)
        Spacer(Modifier.height(8.dp))
        // Stats row
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            listOf( doneLabel to mat.done.toString(),stringResource(R.string.required) to mat.qty.toString(),
                stringResource(R.string.unit) to mat.unit,).forEach { (l, v) ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(l, color = colors.muted, fontSize = 9.sp,
                        modifier = Modifier.padding(bottom = 2.dp))
                    Text(v, color = colors.text, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        Spacer(Modifier.height(10.dp))
        // Progress bar
        Column {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(stringResource(R.string.execution), color = colors.muted, fontSize = 9.sp)
                Text("${(progress * 100).toInt()}%", color = accent, fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(3.dp))
            Box(
                modifier = Modifier.fillMaxWidth().height(4.dp)
                    .clip(RoundedCornerShape(2.dp)).background(colors.dark)
            ) {
                Box(
                    modifier = Modifier.fillMaxHeight()
                        .fillMaxWidth(fraction = progress.coerceIn(0f, 1f))
                        .clip(RoundedCornerShape(2.dp)).background(accent)
                )
            }
        }
        Spacer(Modifier.height(10.dp))
        if (isQuality) {
            Field(stringResource(R.string.sample_qty), "0", smpQty, onSmpQtyChange, isNumber = true, center = true)
            if (!saved) {
                Spacer(Modifier.height(10.dp))
                QualityDecisionSpinner(stringResource(R.string.quality_decision),qualityDecisions, onItemSelected = {
                    onQualityDecisionChange(it)
                })




                Spacer(Modifier.height(10.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FullBtn(
                        "✓  ${stringResource(R.string.accept)}", SUCC, onAccept, saving, smpQty.isNotEmpty()
                    )
                    FullBtn("✕  ${stringResource(R.string.reject)}", DANGER, onReject, saving, smpQty.isNotEmpty()
                    )
                }
            }
        } else {
            val qLabel = "${if (isIssue) stringResource(R.string.issued_qty) else stringResource(R.string.receiving_qty)} (${mat.unit})"
            Field(qLabel, "0", qty, onQtyChange, isNumber = true, center = true)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorksGroupSpinner(hint:String, items : List<WorkersGroup>,onItemSelected: (WorkersGroup) -> Unit) {
    val colors = AppTheme.colors
    var expanded by remember {
        mutableStateOf(false)
    }

    var selectedCategory by remember {
        mutableStateOf<WorkersGroup?>(null)
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
        ) {

            Box(
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.dark)
                    .border(
                        width = 2.dp,
                        color = colors.border2,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable {
                        expanded = true
                    }
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = selectedCategory?.name ?: hint,
                        color = colors.text,
                        fontSize = 16.sp
                    )

                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = colors.text
                    )
                }
            }

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                },
                modifier = Modifier
                    .background(colors.card)
            ) {

                items.forEach { category ->

                    DropdownMenuItem(
                        text = {
                            Text(
                                text = category.name,
                                color = colors.text
                            )
                        },
                        onClick = {
                            onItemSelected(category)
                            selectedCategory = category
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarTypeSpinner(hint:String, items : List<CarType>, onItemSelected: (CarType) -> Unit) {
    val colors = AppTheme.colors
    var expanded by remember {
        mutableStateOf(false)
    }

    var selectedCategory by remember {
        mutableStateOf<CarType?>(null)
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
        ) {

            Box(
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.dark)
                    .border(
                        width = 2.dp,
                        color = colors.border2,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable {
                        expanded = true
                    }
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = selectedCategory?.name ?: hint,
                        color = colors.text,
                        fontSize = 16.sp
                    )

                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = colors.text
                    )
                }
            }

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                },
                modifier = Modifier
                    .background(colors.card)
            ) {

                items.forEach { category ->

                    DropdownMenuItem(
                        text = {
                            Text(
                                text = category.name,
                                color = colors.text
                            )
                        },
                        onClick = {
                            onItemSelected(category)
                            selectedCategory = category
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarSourceSpinner(hint:String, items : List<CarSource>, onItemSelected: (CarSource) -> Unit) {
    val colors = AppTheme.colors
    var expanded by remember {
        mutableStateOf(false)
    }

    var selectedCategory by remember {
        mutableStateOf<CarSource?>(null)
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
        ) {

            Box(
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.dark)
                    .border(
                        width = 2.dp,
                        color = colors.border2,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable {
                        expanded = true
                    }
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = selectedCategory?.name ?: hint,
                        color = colors.text,
                        fontSize = 16.sp
                    )

                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = colors.text
                    )
                }
            }


            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                },
                modifier = Modifier
                    .background(colors.card)
            ) {

                items.forEach { category ->

                    DropdownMenuItem(
                        text = {
                            Text(
                                text = category.name,
                                color = colors.text
                            )
                        },
                        onClick = {
                            onItemSelected(category)
                            selectedCategory = category
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QualityDecisionSpinner(hint:String, items : List<QualityDecision>,onItemSelected: (QualityDecision) -> Unit) {
    val colors = AppTheme.colors
    var expanded by remember {
        mutableStateOf(false)
    }

    var selectedCategory by remember {
        mutableStateOf<QualityDecision?>(null)
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
        ) {

            Box(
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.dark)
                    .border(
                        width = 2.dp,
                        color = colors.border2,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable {
                        expanded = true
                    }
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = selectedCategory?.name ?: hint,
                        color = colors.text,
                        fontSize = 16.sp
                    )

                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = colors.text
                    )
                }
            }

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                },
                modifier = Modifier
                    .background(colors.card)
            ) {

                items.forEach { category ->

                    DropdownMenuItem(
                        text = {
                            Text(
                                text = category.name,
                                color = colors.text
                            )
                        },
                        onClick = {
                            onItemSelected(category)
                            selectedCategory = category
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransportationTypeSelector(options:List<TransportationType>, onItemSelected: (TransportationType) -> Unit) {
    val colors = AppTheme.colors
    var selectedIndex by remember {
        mutableIntStateOf(0)
    }
    SingleChoiceSegmentedButtonRow(
        modifier = Modifier.fillMaxWidth()
    ) {
        options.forEachIndexed { index, label ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = options.size
                ),
                selected = selectedIndex == index,
                onClick = {
                    selectedIndex = index
                    onItemSelected(options[selectedIndex])
                },
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor    = GREEN.copy(alpha = 0.15f),
                    activeContentColor      = GREEN,
                    activeBorderColor       = GREEN.copy(alpha = 0.60f),
                    inactiveContainerColor  = colors.card,
                    inactiveContentColor    = colors.muted,
                    inactiveBorderColor     = colors.border2,
                    disabledActiveContainerColor   = colors.dim.copy(alpha = 0.20f),
                    disabledActiveContentColor     = colors.dim,
                    disabledActiveBorderColor      = colors.dim,
                    disabledInactiveContainerColor = colors.card,
                    disabledInactiveContentColor   = colors.dim,
                    disabledInactiveBorderColor    = colors.border,
                ),
                icon = {},   
                label = {
                    Text(
                        text = options[index].arName,
                        fontSize = 12.sp,
                        fontFamily = CairoFont,
                        fontWeight = if (selectedIndex == index) FontWeight.Bold
                        else FontWeight.Normal,
                    )
                }
            )
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WarehouseToTypeSelector(options:List<DirectTransferWarehouseToType>, onItemSelected: (DirectTransferWarehouseToType) -> Unit) {
    val colors = AppTheme.colors
    var selectedIndex by remember {
        mutableIntStateOf(0)
    }
    SingleChoiceSegmentedButtonRow(
        modifier = Modifier.fillMaxWidth()
    ) {
        options.forEachIndexed { index, label ->
            SegmentedButton(
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = options.size
                ),
                selected = selectedIndex == index,
                onClick = {
                    selectedIndex = index
                    onItemSelected(options[selectedIndex])
                },
                colors = SegmentedButtonDefaults.colors(
                    activeContainerColor    = GREEN.copy(alpha = 0.15f),
                    activeContentColor      = GREEN,
                    activeBorderColor       = GREEN.copy(alpha = 0.60f),
                    inactiveContainerColor  = colors.card,
                    inactiveContentColor    = colors.muted,
                    inactiveBorderColor     = colors.border2,
                    disabledActiveContainerColor   = colors.dim.copy(alpha = 0.20f),
                    disabledActiveContentColor     = colors.dim,
                    disabledActiveBorderColor      = colors.dim,
                    disabledInactiveContainerColor = colors.card,
                    disabledInactiveContentColor   = colors.dim,
                    disabledInactiveBorderColor    = colors.border,
                ),
                icon = {},   
                label = {
                    Text(
                        text = options[index].name,
                        fontSize = 12.sp,
                        fontFamily = CairoFont,
                        fontWeight = if (selectedIndex == index) FontWeight.Bold
                        else FontWeight.Normal,
                    )
                }
            )
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadTypeSpinner(hint:String, items : List<LoadType>, onItemSelected: (LoadType) -> Unit) {
    val colors = AppTheme.colors
    var expanded by remember {
        mutableStateOf(false)
    }

    var selectedCategory by remember {
        mutableStateOf<LoadType?>(null)
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
        ) {

            Box(
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.dark)
                    .border(
                        width = 2.dp,
                        color = colors.border2,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable {
                        expanded = true
                    }
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = selectedCategory?.name ?: hint,
                        color = colors.text,
                        fontSize = 16.sp
                    )

                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = colors.text
                    )
                }
            }

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                },
                modifier = Modifier
                    .background(colors.card)
            ) {

                items.forEach { category ->

                    DropdownMenuItem(
                        text = {
                            Text(
                                text = category.name,
                                color = colors.text
                            )
                        },
                        onClick = {
                            onItemSelected(category)
                            selectedCategory = category
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> SearchableSpinner(
    hint: String,
    items: List<T>,
    itemLabel: (T) -> String,
    onItemSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    searchText: String = "",
    onSearchTextChanged: (String) -> Unit = {}
) {
    val colors = AppTheme.colors
    var expanded by remember { mutableStateOf(false) }
    val twoTapState = rememberTwoTapKeyboardState()
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    val filteredItems = remember(searchText, items) {
        when {
            searchText.isBlank() -> items
            // The field doubles as the selection display, so once the text is exactly one
            // item's label it is a selection rather than a query - keep the whole list
            // reachable so the user can switch to a different item without clearing it.
            items.any { itemLabel(it).equals(searchText, ignoreCase = true) } -> items
            else -> items.filter { itemLabel(it).contains(searchText, ignoreCase = true) }
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { 
                expanded = it
                if (!it) {
                    keyboardController?.hide()
                }
            }
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = {
                    if (!twoTapState.isReadOnly) {
                        onSearchTextChanged(it)
                        if (!expanded) expanded = true
                    }
                },
                readOnly = twoTapState.isReadOnly,
                placeholder = { Text(text = hint, color = colors.text.copy(alpha = 0.5f)) },
                trailingIcon = {
                    IconButton(onClick = { 
                        expanded = !expanded
                        keyboardController?.hide()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = colors.text
                        )
                    }
                },
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(fontSize = 16.sp, color = colors.text),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = colors.dark,
                    unfocusedContainerColor = colors.dark,
                    focusedBorderColor = colors.border2,
                    unfocusedBorderColor = colors.border2,
                    cursorColor = colors.text
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .twoTap(twoTapState, focusRequester = focusRequester)
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(colors.card)
            ) {
                if (filteredItems.isEmpty()) {
                    DropdownMenuItem(
                        text = { Text("No results", color = colors.text) },
                        onClick = {}
                    )
                } else {
                    filteredItems.forEach { item ->
                        DropdownMenuItem(
                            text = { Text(text = itemLabel(item), color = colors.text) },
                            onClick = {
                                onSearchTextChanged(itemLabel(item))
                                onItemSelected(item)
                                expanded = false
                                keyboardController?.hide()
                            }
                        )
                    }
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WarehousesSpinner( hint:String=stringResource(R.string.select_a_warehouse),items : List<Warehouse>, onItemSelected: (Warehouse) -> Unit,accent: Color= GREEN,label: String = stringResource(R.string.warehouse),searchText: String="",onSearchTextChanged: (String) -> Unit) {
     //   val colors = AppTheme.colors
//    var expanded by remember {
//        mutableStateOf(false)
//    }
    Column(modifier = Modifier.padding(bottom = 14.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(3.dp).height(12.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(accent)
            )
            Text(
                label, color = accent, fontSize = 11.sp, fontWeight = FontWeight.Bold,
                letterSpacing = 0.6.sp
            )
        }
        SearchableSpinner(
            hint = hint,
            items = items,
            itemLabel = { it.spinnerLabel() },
            onItemSelected = onItemSelected,
            searchText = searchText,
            onSearchTextChanged = onSearchTextChanged
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun WorkersGroupsSpinner( hint:String=stringResource(R.string.select_workers_group),items : List<net.gbs.gbswarehouse.domain.model.WorkersGroup>, onItemSelected: (net.gbs.gbswarehouse.domain.model.WorkersGroup) -> Unit,accent: Color= GREEN,label: String = stringResource(R.string.workers_group),searchText: String="",onSearchTextChanged: (String) -> Unit) {
    Column(modifier = Modifier.padding(bottom = 14.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(3.dp).height(12.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(accent)
            )
            Text(
                label, color = accent, fontSize = 11.sp, fontWeight = FontWeight.Bold,
                letterSpacing = 0.6.sp
            )
        }
        SearchableSpinner(
            hint = hint,
            items = items,
            itemLabel = { it.spinnerLabel() },
            onItemSelected = onItemSelected,
            searchText = searchText,
            onSearchTextChanged = onSearchTextChanged
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun WorkersGroupsNumsSpinner(hint:String=stringResource(R.string.select_workers_group), items : List<Int>, onItemSelected: (Int) -> Unit, accent: Color= GREEN, label: String = stringResource(R.string.workers_group), searchText: String="", onSearchTextChanged: (String) -> Unit) {
    Column(modifier = Modifier.padding(bottom = 14.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(3.dp).height(12.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(accent)
            )
            Text(
                label, color = accent, fontSize = 11.sp, fontWeight = FontWeight.Bold,
                letterSpacing = 0.6.sp
            )
        }
        SearchableSpinner(
            hint = hint,
            items = items,
            itemLabel = { it.toString() },
            onItemSelected = onItemSelected,
            searchText = searchText,
            onSearchTextChanged = onSearchTextChanged
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun StockOnHandSpinner(hint:String, items : List<MaterialOnHandStock>, onItemSelected: (MaterialOnHandStock) -> Unit, accent: Color, searchText: String="", onSearchTextChanged: (String) -> Unit) {
    var hint = ""
    var label = ""
    when (items.first().isBatchManaged) {
        true -> {
            label = stringResource(R.string.batch)
            hint  = stringResource(R.string.select_a_batch)
        }
        false if items.first().valuationType != null -> {
            label = stringResource(R.string.valuation_type)
            hint  = stringResource(R.string.please_select_a_valuation_type)
        }

        else -> {}
    }

    Column(modifier = Modifier.padding(bottom = 14.dp)) {
        Text(
            label, color = accent, fontSize = 11.sp, fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 5.dp)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(3.dp).height(12.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(accent)
            )
            Text(
                label, color = accent, fontSize = 11.sp, fontWeight = FontWeight.Bold,
                letterSpacing = 0.6.sp
            )
        }
        SearchableSpinner(
            hint = hint,
            items = items,
            itemLabel = { it.toString() },
            onItemSelected = onItemSelected,
            searchText = searchText,
            onSearchTextChanged = onSearchTextChanged
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductionLinesSpinner(hint:String, items : List<ProductionLine>, onItemSelected: (ProductionLine) -> Unit) {
    val colors = AppTheme.colors
    var expanded by remember {
        mutableStateOf(false)
    }

    var selectedCategory by remember {
        mutableStateOf<ProductionLine?>(null)
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = {
                expanded = !expanded
            }
        ) {

            Box(
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.dark)
                    .border(
                        width = 2.dp,
                        color = colors.border2,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable {
                        expanded = true
                    }
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = selectedCategory?.name ?: hint,
                        color = colors.text,
                        fontSize = 16.sp
                    )

                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = colors.text
                    )
                }
            }

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = {
                    expanded = false
                },
                modifier = Modifier
                    .background(colors.card)
            ) {

                items.forEach { category ->

                    DropdownMenuItem(
                        text = {
                            Text(
                                text = category.name,
                                color = colors.text
                            )
                        },
                        onClick = {
                            onItemSelected(category)
                            selectedCategory = category
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
@Composable
fun ReturnReasonsSpinner(hint: String, items: List<ReturnReason>, onItemSelected: (ReturnReason) -> Unit) {
    DropdownSpinner(
        hint = hint,
        items = items,
        onItemSelected = onItemSelected,
        itemLabel = { it.name }
    )
}

@Composable
fun MovementTypeSpinner(hint: String, items: List<MovementType>, onItemSelected: (MovementType) -> Unit) {
    DropdownSpinner(
        hint = hint,
        items = items,
        onItemSelected = onItemSelected,
        itemLabel = { it.name }
    )
}

@Composable
fun ReturnTypeSpinner(hint: String, items: List<ReturnType>, onItemSelected: (ReturnType) -> Unit) {
    DropdownSpinner(
        hint = hint,
        items = items,
        onItemSelected = onItemSelected,
        itemLabel = { it.name }
    )
}

@Composable
fun MaterialsSpinner(
    label: String,
    hint: String,
    items: List<Material>,
    onItemSelected: (Material) -> Unit
) {
    val colors = AppTheme.colors
    Column {
        Text(label, color = colors.muted, fontSize = 10.sp, fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 5.dp))
        DropdownSpinner(
            hint = hint,
            items = items,
            onItemSelected = onItemSelected,
            itemLabel = { "${it.code} - ${it.desc}" }
        )
    }
}

@Composable
fun QtyFieldWithUOMSpinner(
    label: String,
    hint: String,
    value: String,
    isNumber: Boolean,
    onValueChange: (String) -> Unit,
    onItemSelected: (UOM) -> Unit,
    uOMItems: List<UOM>
) {
    val colors = AppTheme.colors
    Column {
        Text(label, color = colors.muted, fontSize = 10.sp, fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 5.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Field(
                label = "",
                hint = hint,
                value = value,
                onValueChange = onValueChange,
                isNumber = isNumber,
                withLabel = false,
                modifier = Modifier.weight(1.5f)
            )
            DropdownSpinner(
                hint = "UOM",
                items = uOMItems,
                onItemSelected = onItemSelected,
                itemLabel = { it.name },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> DropdownSpinner(
    hint: String,
    items: List<T>,
    selectedItem: T? = null,
    onItemSelected: (T) -> Unit,
    itemLabel: (T) -> String,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    var expanded by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf(selectedItem) }
    LaunchedEffect(selectedItem) {
        selected = selectedItem
    }

    ExposedDropdownMenuBox(
        expanded = expanded && enabled,
        onExpandedChange = { if (enabled) expanded = !expanded },
        modifier = modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(if (enabled) colors.dark else colors.dark.copy(alpha = 0.5f))
                .border(2.dp, if (enabled) colors.border2 else colors.dim, RoundedCornerShape(12.dp))
                .then(
                    if (enabled) Modifier.clickable { expanded = true }
                    else Modifier
                )
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selected?.let { itemLabel(it) } ?: hint,
                    color = if (!enabled) colors.dim else (if (selected == null) colors.muted else colors.text),
                    fontSize = 13.sp,
                    fontWeight = if (selected == null) FontWeight.Normal else FontWeight.SemiBold
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp
                    else Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = if (!enabled) colors.dim else (if (selected == null) colors.muted else GREEN)
                )
            }
        }

        if (enabled) {
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .background(colors.card)
                    .border(1.dp, colors.border2, RoundedCornerShape(8.dp))
            ) {
                items.forEach { item ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = itemLabel(item),
                                color = if (item == selected) GREEN else colors.text,
                                fontSize = 13.sp,
                                fontWeight = if (item == selected) FontWeight.Bold
                                else FontWeight.Normal
                            )
                        },
                        onClick = {
                            selected = item
                            onItemSelected(item)
                            expanded = false
                        },
                        modifier = Modifier
                            .background(
                                if (item == selected) GREEN.copy(alpha = 0.08f)
                                else Color.Transparent
                            )
                    )
                }
            }
        }
    }
}
@Composable
fun MarginTenDp(){
    Spacer(Modifier.height(10.dp))
}

@Composable
fun MarginVertical(margin : Dp){
    Spacer(Modifier.height(margin))
}

@Composable
fun DatePickerField(
    value: String?,
    onDateSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String,
    hint: String,
    dateFormatter: (Long) -> String = { millis ->
        SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Date(millis))
    }
) {
    var showPicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val colors = AppTheme.colors
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(colors.dark)
            .border(1.dp, colors.border2, RoundedCornerShape(8.dp))
            .clickable { showPicker = true }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if(value.isNullOrEmpty())label else value.take(10)  ,
                color = if (value.isNullOrEmpty()) colors.muted else colors.text,
                fontSize = 13.sp,
                fontWeight = if (value.isNullOrEmpty()) FontWeight.Normal else FontWeight.SemiBold
            )
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = hint ,
                tint = if (value.isNullOrEmpty()) colors.muted else GREEN,
                modifier = Modifier.size(18.dp)
            )
        }
    }

    if (showPicker) {
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val formatted = dateFormatter(millis)
                            onDateSelected(formatted)
                        }
                        showPicker = false
                    }
                ) {
                    Text(stringResource(R.string.confirm), color = GREEN, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) {
                    Text(stringResource(R.string.cancel), color = colors.muted)
                }
            },
            colors = DatePickerDefaults.colors(
                containerColor          = colors.card,
                titleContentColor       = colors.muted,
                headlineContentColor    = colors.text,
                weekdayContentColor     = colors.muted,
                subheadContentColor     = colors.muted,
                navigationContentColor  = colors.text,
                yearContentColor        = colors.text,
                currentYearContentColor = GREEN,
                selectedYearContentColor         = colors.dark,
                selectedYearContainerColor       = GREEN,
                dayContentColor                  = colors.text,
                selectedDayContentColor          = colors.dark,
                selectedDayContainerColor        = GREEN,
                todayContentColor                = GREEN,
                todayDateBorderColor             = GREEN,
                dayInSelectionRangeContentColor  = colors.text,
                dayInSelectionRangeContainerColor= GREEN.copy(alpha = 0.20f),
            )
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    containerColor       = colors.card,
                    titleContentColor    = colors.muted,
                    headlineContentColor = colors.text,
                    weekdayContentColor  = colors.muted,
                    navigationContentColor = colors.text,
                    yearContentColor     = colors.text,
                    currentYearContentColor      = GREEN,
                    selectedYearContentColor     = colors.dark,
                    selectedYearContainerColor   = GREEN,
                    dayContentColor              = colors.text,
                    selectedDayContentColor      = colors.dark,
                    selectedDayContainerColor    = GREEN,
                    todayContentColor            = GREEN,
                    todayDateBorderColor         = GREEN,
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ValuationTypeSpinner(hint:String, items : List<ValuationType>, onItemSelected: (ValuationType) -> Unit,accent:Color,searchText:String,onSearchTextChanged: (String) -> Unit) {
//    val colors = AppTheme.colors
//    var expanded by remember {
//        mutableStateOf(false)
//    }
//
//    var selectedCategory by remember {
//        mutableStateOf<ValuationType?>(null)
//    }
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//    ) {
//
//        ExposedDropdownMenuBox(
//            expanded = expanded,
//            onExpandedChange = {
//                expanded = !expanded
//            }
//        ) {
//
//            Box(
//                modifier = Modifier
//                    .menuAnchor()
//                    .fillMaxWidth()
//                    .clip(RoundedCornerShape(12.dp))
//                    .background(colors.dark)
//                    .border(
//                        width = 2.dp,
//                        color = colors.border2,
//                        shape = RoundedCornerShape(12.dp)
//                    )
//                    .clickable {
//                        expanded = true
//                    }
//                    .padding(horizontal = 16.dp, vertical = 14.dp)
//            ) {
//
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceBetween,
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//
//                    Text(
//                        text = selectedCategory?.type ?: hint,
//                        color = colors.text,
//                        fontSize = 16.sp
//                    )
//
//                    Icon(
//                        imageVector = Icons.Default.ArrowDropDown,
//                        contentDescription = null,
//                        tint = colors.text
//                    )
//                }
//            }
//
//            ExposedDropdownMenu(
//                expanded = expanded,
//                onDismissRequest = {
//                    expanded = false
//                },
//                modifier = Modifier
//                    .background(colors.bg)
//            ) {
//
//                items.forEach { category ->
//
//                    DropdownMenuItem(
//                        text = {
//                            Text(
//                                text = category.type,
//                                color = colors.text
//                            )
//                        },
//                        onClick = {
//                            onItemSelected(category)
//                            selectedCategory = category
//                            expanded = false
//                        }
//                    )
//                }
//            }
//        }
//    }
    Column(modifier = Modifier.padding(bottom = 14.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(3.dp).height(12.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(accent)
            )
            Text(
                stringResource(R.string.valuation_type), color = accent, fontSize = 11.sp, fontWeight = FontWeight.Bold,
                letterSpacing = 0.6.sp
            )
        }
        SearchableSpinner(
            hint = hint,
            items = items,
            itemLabel = { it.spinnerLabel() },
            onItemSelected = onItemSelected,
            searchText = searchText,
            onSearchTextChanged = onSearchTextChanged
        )
    }
}

@Composable
fun BatchNoSpinner(hint: String, items: List<BatchNo>, onItemSelected: (BatchNo) -> Unit) {
    DropdownSpinner(
        hint = hint,
        items = items,
        onItemSelected = onItemSelected,
        itemLabel = { it.name }
    )
}

@Composable
fun CostCenterSpinner(hint: String, items: List<CostCenter>, onItemSelected: (CostCenter) -> Unit) {
    DropdownSpinner(
        hint = hint,
        items = items,
        onItemSelected = onItemSelected,
        itemLabel = { it.name }
    )
}

@Composable
fun TransportingVendorSpinner(
hint: String, items: List<TransportingVendor>, onItemSelected: (TransportingVendor) -> Unit) {
    DropdownSpinner(
        hint = hint,
        items = items,
        onItemSelected = onItemSelected,
        itemLabel = { it.name }
    )
}

@Composable
fun MatCodeInput(value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier) {
    val colors = AppTheme.colors
    val dims = AppTheme.dims
    val twoTapState = rememberTwoTapKeyboardState()
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(colors.dark)
            .border(1.dp, colors.border2, RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicTextField(
            value = value,
            onValueChange = { if (!twoTapState.isReadOnly) onValueChange(it) },
            singleLine = true,
            readOnly = twoTapState.isReadOnly,
            textStyle = TextStyle(
                color = colors.text, fontSize = dims.fontSizeButton,
                textDirection = TextDirection.Ltr
            ),
            cursorBrush = SolidColor(GREEN),
            modifier = Modifier
                .weight(1f)
                .twoTap(twoTapState)
                .padding(vertical = 10.dp),
            decorationBox = { inner ->
                if (value.isEmpty()) Text("كود المادة...", color = colors.muted, fontSize = dims.fontSizeButton)
                inner()
            }
        )
    }
}

@Composable
fun BinCodeInput(value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier) {
    val colors = AppTheme.colors
    val dims = AppTheme.dims
    val twoTapState = rememberTwoTapKeyboardState()
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(colors.dark)
            .border(1.dp, colors.border2, RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicTextField(
            value = value,
            onValueChange = { if (!twoTapState.isReadOnly) onValueChange(it) },
            singleLine = true,
            readOnly = twoTapState.isReadOnly,
            textStyle = TextStyle(
                color = colors.text, fontSize = dims.fontSizeButton,
                textDirection = TextDirection.Ltr
            ),
            cursorBrush = SolidColor(YELLOW),
            modifier = Modifier
                .weight(1f)
                .twoTap(twoTapState)
                .padding(vertical = 10.dp),
            decorationBox = { inner ->
                if (value.isEmpty()) Text("كود البن (Bin Code)...", color = colors.muted, fontSize = dims.fontSizeButton)
                inner()
            }
        )
    }
}

@Composable
fun MaterialDialogItem(mat: Material, accent: Color, doneLabel: String, onClick: () -> Unit) {
    val colors = AppTheme.colors
    val dims = AppTheme.dims
    var hovered by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (hovered) colors.card2 else colors.dark)
            .border(1.dp, if (hovered) accent.copy(0.35f) else colors.border, RoundedCornerShape(10.dp))
            .clickable { onClick(); hovered = false }
            .padding(all = 11.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(mat.code, color = accent, fontSize = dims.fontSizeSubtitle, fontWeight = FontWeight.Bold)
            Text(mat.unit, color = colors.muted, fontSize = dims.fontSizeSubtitle)
        }
        Spacer(Modifier.height(4.dp))
        Text(mat.desc, color = colors.text, fontSize = dims.fontSizeBody, fontWeight = FontWeight.SemiBold,
            lineHeight = 18.sp)
        Spacer(Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Text("المطلوب: ${mat.qty}", color = colors.muted, fontSize = dims.fontSizeSubtitle)
            Text("$doneLabel: ${mat.done}",
                color = if (mat.done == mat.qty) colors.succ else colors.muted, fontSize = dims.fontSizeSubtitle)
        }
    }
}

@Composable
fun BinDialogItem(bin: Bin, accent: Color, onClick: () -> Unit) {
    val colors = AppTheme.colors
    val dims = AppTheme.dims
    var hovered by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (hovered) colors.card2 else colors.dark)
            .border(1.dp, if (hovered) accent.copy(0.35f) else colors.border, RoundedCornerShape(10.dp))
            .clickable { onClick(); hovered = false }
            .padding(12.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("📍 ${bin.code}", color = accent, fontSize = dims.fontSizeSubtitle, fontWeight = FontWeight.Bold)
            Text("متاح", color = colors.succ, fontSize = dims.fontSizeSubtitle, fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.height(4.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            listOf("المنطقة" to bin.zone, "الممر" to bin.aisle, "المتاح" to bin.avail)
                .forEach { (l, v) ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(l, color = colors.muted, fontSize = dims.fontSizeCaption)
                        Text(v, color = colors.text, fontSize = dims.fontSizeHeader, fontWeight = FontWeight.Bold)
                    }
                }
        }
    }
}
