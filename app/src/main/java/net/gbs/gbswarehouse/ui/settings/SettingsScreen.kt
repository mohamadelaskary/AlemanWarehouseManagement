package net.gbs.gbswarehouse.ui.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import net.gbs.gbswarehouse.DANGER
import net.gbs.gbswarehouse.FullBtn
import net.gbs.gbswarehouse.GREEN
import net.gbs.gbswarehouse.R
import net.gbs.gbswarehouse.SUCC
import net.gbs.gbswarehouse.Section
import net.gbs.gbswarehouse.TopBar
import net.gbs.gbswarehouse.ui.theme.AppTheme
import kotlin.time.Duration.Companion.milliseconds


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController : NavController,
    vm: SettingsViewModel = hiltViewModel()
) {
    val uiState = vm.uiState
    // auto-reset Idle after showing success/error
    LaunchedEffect(uiState) {
        if (uiState is SettingsUiState.Success || uiState is SettingsUiState.Error) {
            kotlinx.coroutines.delay(3000.milliseconds)
            vm.resetState()
        }
    }
    val colors = AppTheme.colors
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bg)
    ) {
        // ── TopBar ────────────────────────────────────────────────────────
        TopBar(
            title    = stringResource(R.string.connection_settings),
            subtitle = stringResource(R.string.server_information),
            accent   = GREEN,
            onBack   = {navController.popBackStack()}
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 14.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            // ── Protocol toggle ───────────────────────────────────────────
            Section(label = stringResource(R.string.protocol), accent = GREEN) {
                val options = listOf("http", "https")
                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    options.forEachIndexed { index, option ->
                        SegmentedButton(
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = options.size
                            ),
                            selected = vm.protocol == option,
                            onClick  = { vm.protocol = option },
                            colors   = SegmentedButtonDefaults.colors(
                                activeContainerColor   = GREEN.copy(alpha = 0.15f),
                                activeContentColor     = GREEN,
                                activeBorderColor      = GREEN.copy(alpha = 0.60f),
                                inactiveContainerColor = colors.card,
                                inactiveContentColor   = colors.muted,
                                inactiveBorderColor    = colors.border2,
                            ),
                            icon = {},
                            label = {
                                Text(
                                    text = option,
                                    fontSize = 13.sp,
                                    fontWeight = if (vm.protocol == option) FontWeight.Bold
                                    else FontWeight.Normal
                                )
                            }
                        )
                    }
                }
            }

            // ── IP Address ────────────────────────────────────────────────
            Section(label = stringResource(R.string.ip_address), accent = GREEN) {
                SettingsInputField(
                    value         = vm.ip,
                    onValueChange = { vm.ip = it; vm.ipError = null },
                    hint          = stringResource(R.string.example) +"192.168.1.100",
                    error         = vm.ipError,
                    keyboardType  = KeyboardType.Ascii,
                    ltr           = true
                )
            }

            // ── Port ──────────────────────────────────────────────────────
            Section(label = stringResource(R.string.port), accent = GREEN) {
                SettingsInputField(
                    value         = vm.port,
                    onValueChange = { vm.port = it; vm.portError = null },
                    hint          = stringResource(R.string.example)+" 8080",
                    error         = vm.portError,
                    keyboardType  = KeyboardType.Number,
                    ltr           = true
                )
            }

            // ── Preview URL ───────────────────────────────────────────────
            if (vm.ip.isNotBlank() && vm.port.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(colors.dark)
                        .border(1.dp, colors.border, RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            stringResource(R.string.check_url), color = colors.muted, fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "${vm.protocol}://${vm.ip}:${vm.port}",
                            color = GREEN,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // ── Feedback banner ───────────────────────────────────────────
            AnimatedVisibility(
                visible = uiState is SettingsUiState.Success ||
                        uiState is SettingsUiState.Error,
                enter   = fadeIn() + expandVertically(),
                exit    = fadeOut() + shrinkVertically()
            ) {
                val isSuccess = uiState is SettingsUiState.Success
                val msg = when (uiState) {
                    is SettingsUiState.Success -> uiState.message
                    is SettingsUiState.Error   -> uiState.message
                    else -> ""
                }
                val col = if (isSuccess) SUCC else DANGER

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(col.copy(alpha = 0.10f))
                        .border(1.dp, col.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = if (isSuccess) Icons.Default.CheckCircle
                        else Icons.Default.Warning,
                        contentDescription = null,
                        tint = col,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(msg, color = col, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            // ── Save button ───────────────────────────────────────────────
            Spacer(Modifier.height(4.dp))
            FullBtn(
                label   = if (uiState is SettingsUiState.Loading) "" else "↓  حفظ الإعدادات",
                color   = GREEN,
                onClick = {
                    vm.onSaveButtonClicked()
                          },
                saving  = uiState is SettingsUiState.Loading
            )
        }
    }
}

// ── Settings Input Field ──────────────────────────────────────────────────────
@Composable
fun SettingsInputField(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    error: String?,
    keyboardType: KeyboardType = KeyboardType.Text,
    ltr: Boolean = false
) {
    val colors = AppTheme.colors

    val borderColor = when {
        error != null        -> DANGER.copy(alpha = 0.70f)
        value.isNotBlank()   -> GREEN.copy(alpha = 0.50f)
        else                 -> colors.border2
    }

    BasicTextField(
        value         = value,
        onValueChange = onValueChange,
        singleLine    = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        textStyle = TextStyle(
            color = colors.text,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            textDirection = if (ltr) TextDirection.Ltr else TextDirection.Rtl
        ),
        cursorBrush = SolidColor(GREEN),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(colors.dark)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 11.dp),
        decorationBox = { inner ->
            if (value.isEmpty()) {
                Text(hint, color = colors.muted, fontSize = 14.sp)
            }
            inner()
        }
    )

    // inline error message
    AnimatedVisibility(visible = error != null) {
        Row(
            modifier = Modifier.padding(top = 4.dp, start = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(Icons.Default.Warning, contentDescription = null,
                tint = DANGER, modifier = Modifier.size(12.dp))
            Text(error ?: "", color = DANGER, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}