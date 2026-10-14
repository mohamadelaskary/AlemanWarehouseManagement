package net.gbs.gbswarehouse.ui.login

import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import net.gbs.gbswarehouse.ui.theme.AppTheme


import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import net.gbs.gbswarehouse.LanguageToggleButton
import net.gbs.gbswarehouse.R
import net.gbs.gbswarehouse.Screens
import net.gbs.gbswarehouse.SettingsButton
import net.gbs.gbswarehouse.ThemeToggleButton

@Composable
fun LoginScreen(
    navController: NavController,
) {
    val viewModel : LoginViewModel =  hiltViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val colors = AppTheme.colors
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.bg)
    ) {
        // ── Background gradient accent ─────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(colors.green.copy(alpha = 0.06f), Color.Transparent)
                    )
                )
        )

        // ── Settings icon top-left ─────────────────────────────────────────
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd).padding(top = 10.dp, end = 10.dp).zIndex(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ){
            SettingsButton(
                onClick = {
                    navController.navigate(Screens.SettingsScreen.route)
                }
            )
            LanguageToggleButton()
            ThemeToggleButton()
        }


        // ── Main content ───────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(Modifier.height(60.dp))

            // ── Logo area ──────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(22.dp)),
//                    .background(colors.card)
//                    .border(
//                        width = 1.5.dp,
//                        brush = Brush.linearGradient(
//                            listOf(colors.green.copy(.55f), colors.green.copy(.15f))
//                        ),
//                        shape = RoundedCornerShape(22.dp)
//                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.gbs_logo),
                    contentDescription = "GBS Logo"
                    , modifier = Modifier.size(76.dp)
                )
            }

            Spacer(Modifier.height(18.dp))

            Text(
                text       = stringResource(R.string.main_warehouse),
                color      = colors.text,
                fontSize   = 22.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text     = stringResource(R.string.warehouse_ops_system),
                color    = colors.muted,
                fontSize = 12.sp
            )

            Spacer(Modifier.height(36.dp))

            // ── colors.card ───────────────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(colors.card)
                    .border(1.dp, colors.border, RoundedCornerShape(18.dp))
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text       = stringResource(R.string.login),
                    color      = colors.text,
                    fontSize   = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier   = Modifier.fillMaxWidth(),
                    textAlign  = TextAlign.Start
                )

                HorizontalDivider(color = colors.border, thickness = 1.dp)

                // ── Username ───────────────────────────────────────────────
                LoginField(
                    label         = stringResource(R.string.username),
                    value         = state.username,
                    onValueChange = { 
                        viewModel.onEvent(
                            LoginUiEvents.UsernameChanged(it)
                        )
                    },
                    hint          = stringResource(R.string.enter_username),
                    icon          = "👤",
                    error         = state.usernameError,
                    keyboardType  = KeyboardType.Ascii
                )

                // ── Password ───────────────────────────────────────────────
                LoginField(
                    label           = stringResource(R.string.password),
                    value           = state.password,
                    onValueChange   = { 
                        viewModel.onEvent(
                            LoginUiEvents.PasswordChanged(it)
                        )
                                       },
                    hint            = stringResource(R.string.enter_password),
                    icon            = "🔒",
                    error           = state.passwordError,
                    keyboardType    = KeyboardType.Password,
                    isPassword      = true,
                    passwordVisible = state.passwordVisible,
                    onTogglePassword = { 
                        viewModel.onEvent(
                            LoginUiEvents.TogglePasswordVisibility
                        )
                    }
                )
                when(state.loginApiState){
                    is LoginApiState.Error -> {
                        AnimatedVisibility(
                            visible = true,
                            enter   = fadeIn() + expandVertically(),
                            exit    = fadeOut() + shrinkVertically()
                        ) {
                            val msg = (state.loginApiState as LoginApiState.Error).message
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(colors.danger.copy(alpha = 0.10f))
                                    .border(1.dp, colors.danger.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                                    .padding(12.dp),
                                verticalAlignment     = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector   = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint          = colors.danger,
                                    modifier      = Modifier.size(16.dp)
                                )
                                Text(msg.text, color = colors.danger, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                    LoginApiState.Idle -> {
                    }
                    LoginApiState.Loading -> {
                    }
                    is LoginApiState.Success -> {
                        LaunchedEffect(Unit) {
                            navController.navigate(Screens.Home.route){
                                popUpTo(Screens.LoginScreen.route){
                                    inclusive = true
                                }
                            }
                        }
                    }
                }
                // ── Error banner ───────────────────────────────────────────


                // ── Login button ───────────────────────────────────────────
                Spacer(Modifier.height(2.dp))
                LoginButton(
                    loading  = state.loginApiState == LoginApiState.Loading,
                    onClick  = {
                        viewModel.onEvent(
                            LoginUiEvents.LoginButtonClicked
                        )
                    }
                )
            }

            Spacer(Modifier.height(24.dp))

            // ── Footer ─────────────────────────────────────────────────────
            Text(
                text     = "GBS — Global Barcode Solutions",
                color    = colors.dim,
                fontSize = 10.sp,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(40.dp))
        }
    }

}

// ── Login Field ───────────────────────────────────────────────────────────────
@Composable
fun LoginField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    icon: String,
    error: net.gbs.gbswarehouse.domain.model.Message?,
    keyboardType: KeyboardType    = KeyboardType.Text,
    isPassword: Boolean           = false,
    passwordVisible: Boolean      = false,
    onTogglePassword: (() -> Unit)? = null
) {
    val colors = AppTheme.colors
    val borderColor = when {
        error != null      -> colors.danger.copy(alpha = 0.70f)
        value.isNotBlank() -> colors.green.copy(alpha = 0.50f)
        else               -> colors.border2
    }

    Column {
        // label
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier.padding(bottom = 6.dp)
        ) {
            Text(icon, fontSize = 12.sp)
            Text(
                text       = label,
                color      = if (error != null) colors.danger else colors.muted,
                fontSize   = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        // input row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(colors.dark)
                .border(1.5.dp, borderColor, RoundedCornerShape(10.dp))
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            BasicTextField(
                value           = value,
                onValueChange   = onValueChange,
                singleLine      = true,
                visualTransformation = if (isPassword && !passwordVisible)
                    PasswordVisualTransformation() else VisualTransformation.None,
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                textStyle = TextStyle(
                    color     = colors.text,
                    fontSize  = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    textDirection = TextDirection.Ltr
                ),
                cursorBrush = SolidColor(colors.green),
                modifier = Modifier.weight(1f),
                decorationBox = { inner ->
                    if (value.isEmpty()) {
                        Text(hint, color = colors.muted, fontSize = 13.sp)
                    }
                    inner()
                }
            )

            // eye toggle for password
            if (isPassword && onTogglePassword != null) {
                Text(
                    text     = if (passwordVisible) "🙈" else "👁",
                    fontSize = 16.sp,
                    modifier = Modifier.clickable { onTogglePassword() }
                )
            }
        }

        // inline error
        AnimatedVisibility(visible = error != null) {
            Row(
                modifier = Modifier.padding(top = 4.dp, start = 4.dp),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(Icons.Default.Warning, contentDescription = null, tint = colors.danger, modifier = Modifier.size(12.dp))
                Text(error!!.text, color = colors.danger, fontSize = 10.sp)
            }
        }
    }
}

// ── Login Button ──────────────────────────────────────────────────────────────
@Composable
fun LoginButton(loading: Boolean, onClick: () -> Unit) {
    val colors = AppTheme.colors
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(colors.green.copy(alpha = 0.22f), colors.green.copy(alpha = 0.10f))
                )
            )
            .border(
                width = 1.5.dp,
                brush = Brush.horizontalGradient(
                    listOf(colors.green.copy(alpha = 0.70f), colors.green.copy(alpha = 0.30f))
                ),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(enabled = !loading) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (loading) {
            CircularProgressIndicator(
                color       = colors.green,
                modifier    = Modifier.size(22.dp),
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text       = stringResource(R.string.login_btn),
                color      = colors.green,
                fontSize   = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.5.sp
            )
        }
    }
}
