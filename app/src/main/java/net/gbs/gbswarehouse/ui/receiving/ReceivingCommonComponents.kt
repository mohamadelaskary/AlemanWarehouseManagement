package net.gbs.gbswarehouse.ui.receiving

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import net.gbs.gbswarehouse.R
import net.gbs.gbswarehouse.ui.theme.AppTheme

@Composable
fun ReceivingInfoCell(
    label: String,
    value: String,
    valueColor: Color = Color.Unspecified,
    modifier: Modifier = Modifier
) {
    val colors = AppTheme.colors
    val textColor = if (valueColor == Color.Unspecified) colors.text else valueColor
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(colors.dark)
            .padding(all = 8.dp)
    ) {
        Text(
            text = label,
            color = colors.muted,
            fontSize = 9.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 2.dp)
        )
        Text(
            text = value,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ReceivingMatCodeInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    hint: String = stringResource(R.string.item_code_hint),
    onKeyboardAction: (() -> Unit)? = null
) {
    val colors = AppTheme.colors
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (enabled) colors.dark else colors.card2)
            .border(1.dp, colors.border2, RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            enabled = enabled,
            textStyle = TextStyle(
                color = if (enabled) colors.text else colors.muted,
                fontSize = 13.sp,
                textDirection = TextDirection.Ltr
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                onKeyboardAction?.invoke()
            }),
            cursorBrush = SolidColor(colors.green),
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 10.dp),
            decorationBox = { inner ->
                if (value.isEmpty()) {
                    Text(text = hint, color = colors.muted, fontSize = 13.sp)
                }
                inner()
            }
        )
    }
}

@Composable
fun ReceivingBottomSheetDialog(
    title: String,
    onClose: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    val colors = AppTheme.colors
    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
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
                    .border(
                        1.dp, colors.border2,
                        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    )
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
                        .padding(horizontal = 14.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "✕",
                        color = colors.muted,
                        fontSize = 18.sp,
                        modifier = Modifier
                            .clickable { onClose() }
                            .padding(horizontal = 2.dp)
                    )
                    Text(
                        text = title,
                        color = colors.text,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                content()
            }
        }
    }
}

@Composable
fun ReceivingChip(
    text: String,
    modifier: Modifier = Modifier,
    accent: Color = AppTheme.colors.green
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = accent.copy(.10f)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            fontSize = 11.sp,
            color = accent,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun ReceivingMaterialDialogItem(
    materialCode: String,
    materialDescription: String,
    uom: String,
    orderedQty: String,
    receivedQty: String,
    doneLabel: String,
    accent: Color,
    onClick: () -> Unit
) {
    val colors = AppTheme.colors
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(colors.dark)
            .border(1.dp, colors.border, RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(all = 11.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = materialCode,
                color = accent,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
            Text(text = uom, color = colors.muted, fontSize = 10.sp)
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = materialDescription,
            color = colors.text,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 18.sp
        )
        Spacer(Modifier.height(6.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(
                text = stringResource(R.string.required) + ": $orderedQty",
                color = colors.muted,
                fontSize = 10.sp
            )
            Text(
                text = "$doneLabel: $receivedQty",
                color = if (receivedQty == orderedQty) colors.succ else colors.muted,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun ReceivingAddLineButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    accent: Color = AppTheme.colors.addMaterialButtonBackground
) {
    val colors = AppTheme.colors
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(if (enabled) accent else colors.dim.copy(alpha = 0.22f))
            .border(1.dp, colors.border, RoundedCornerShape(10.dp))
            .clickable(enabled = enabled) { onClick() }
            .padding(vertical = 13.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.add),
            color = if (enabled) colors.addMaterialButtonTextColor else colors.dim,
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}
