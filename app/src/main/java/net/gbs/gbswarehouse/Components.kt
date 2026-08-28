package net.gbs.gbswarehouse


import android.provider.CalendarContract
import androidx.compose.foundation.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import net.gbs.gbswarehouse.core.session.SessionManager
import net.gbs.gbswarehouse.domain.model.ValuationType
import net.gbs.gbswarehouse.domain.model.Warehouse
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
// ── TopBar (Hdr) ─────────────────────────────────────────────────────────────
@Composable
fun TopBar(
    title: String,
    subtitle: String? = null,
    accent: Color = GREEN,
    onBack: () -> Unit,
    navController: NavController? = null) {
    val colors = AppTheme.colors
    val actualNavController = navController ?: LocalNavController.current
    val sessionManager = SessionManager()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.card)
            .border(width = 1.dp, color = colors.border, shape = RoundedCornerShape(0.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
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
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (subtitle != null) {
                Text(text = subtitle, color = accent, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
            }
        }
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

    Column(modifier = Modifier.padding(bottom = 14.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Box(modifier = Modifier
                .width(3.dp).height(12.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(accent))
            Text(label, color = accent, fontSize = 11.sp, fontWeight = FontWeight.Bold,
                letterSpacing = 0.6.sp)
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(colors.card)
                .border(1.dp, colors.border, RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            content()
        }
    }
}

// ── Pill ──────────────────────────────────────────────────────────────────────
@Composable
fun Pill(label: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(label, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
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
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors

    Column(modifier = modifier) {
        if (withLabel)
            Text(label, color = colors.muted, fontSize = 10.sp, fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 5.dp))
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            keyboardOptions = if (isNumber) KeyboardOptions(keyboardType = KeyboardType.Number)
            else KeyboardOptions.Default,
            textStyle = TextStyle(
                color = colors.text,
                fontSize = if (isNumber) 16.sp else 13.sp,
                fontWeight = if (isNumber) FontWeight.Bold else FontWeight.Normal,
                textAlign = if (center) TextAlign.Center else (if (LocalLanguage.current == "ar") TextAlign.Right else TextAlign.Left),
                textDirection = if (ltr) TextDirection.Ltr else (if (LocalLanguage.current == "ar") TextDirection.Rtl else TextDirection.Ltr)
            ),
            cursorBrush = SolidColor(GREEN),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(colors.dark)
                .border(1.dp, colors.border2, RoundedCornerShape(8.dp))
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
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = TextStyle(color = colors.text, fontSize = 12.sp, textDirection = if (LocalLanguage.current == "ar") TextDirection.Rtl else TextDirection.Ltr),
            cursorBrush = SolidColor(GREEN),
            modifier = Modifier.weight(1f).padding(vertical = 9.dp),
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
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = TextStyle(color = colors.text, fontSize = 12.sp, textDirection = if (LocalLanguage.current == "ar") TextDirection.Rtl else TextDirection.Ltr),
            cursorBrush = SolidColor(GREEN),
            modifier = Modifier.weight(1f).padding(vertical = 9.dp),
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
fun OutlineBtn(label: String, color: Color, onClick: () -> Unit) {
    var hovered by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
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
    enabled: Boolean = true
) {
    val colors = AppTheme.colors
    val bg     = if (!enabled) colors.dim.copy(0.22f) else color.copy(0.12f)
    val border = if (!enabled) colors.dim else color.copy(0.35f)
    val txtCol = if (!enabled) colors.dim else color

    Box(
        modifier = Modifier
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
            Text(label, color = txtCol, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
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
fun SuccessDialogCard(label: String,message: String, accent: Color) {
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
fun SuccessDialog(accent: Color,message: String,onClose: () -> Unit){
    val colors = AppTheme.colors
    Dialog(onDismissRequest = onClose) {
        Box(modifier = Modifier.wrapContentSize()) {

            SuccessDialogCard(
                label = stringResource(R.string.success),
                message = message,
                accent = accent,
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
    modifier: Modifier = Modifier,
    color: Color = AppTheme.colors.text
) {
    val colors = AppTheme.colors
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(colors.dark)
            .padding(8.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            label, color = colors.muted, fontSize = 9.sp, fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 2.dp)
        )
        Text(value, color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
            Text(mat.code, color = accent, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
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
//    var searchText by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }

    val filteredItems = remember(searchText, items) {
        if (searchText.isBlank()) items
        else items.filter { itemLabel(it).contains(searchText, ignoreCase = true) }
    }

    val focusManager = androidx.compose.ui.platform.LocalFocusManager.current

    Column(modifier = modifier.fillMaxWidth()) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { 
                if (!it) expanded = false
            }
        ) {
            OutlinedTextField(
                value = searchText,
                onValueChange = {
                    onSearchTextChanged(it)
                    if (!expanded) expanded = true
                },
                placeholder = { Text(text = hint, color = colors.text.copy(alpha = 0.5f)) },
                trailingIcon = {
                    IconButton(onClick = { expanded = !expanded }) {
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
                    .focusRequester(focusRequester)
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
            itemLabel = { "${it.warehouseDescription} (${it.warehouseCode})" },
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
            itemLabel = { "${it.workerGroupDescription} (${it.workerGroupCode})" },
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
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    var expanded by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf(selectedItem) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(colors.dark)
                .border(2.dp, colors.border2, RoundedCornerShape(12.dp))
                .clickable { expanded = true }
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selected?.let { itemLabel(it) } ?: hint,
                    color = if (selected == null) colors.muted else colors.text,
                    fontSize = 13.sp,
                    fontWeight = if (selected == null) FontWeight.Normal else FontWeight.SemiBold
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp
                    else Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = if (selected == null) colors.muted else GREEN
                )
            }
        }

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
                text = if(value.isNullOrEmpty())label else value  ,
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

                            val formatted = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                                .format(Date(millis))
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
            itemLabel = { it.type },
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
fun TransportingVendorSpinner(hint: String, items: List<TransportingVendor>, onItemSelected: (TransportingVendor) -> Unit) {
    DropdownSpinner(
        hint = hint,
        items = items,
        onItemSelected = onItemSelected,
        itemLabel = { it.name }
    )
}
