package net.gbs.gbswarehouse.ui.login

sealed class LoginUiEvents {
    data class UsernameChanged(val username: String) : LoginUiEvents()
    data class PasswordChanged(val password: String) : LoginUiEvents()
    object TogglePasswordVisibility : LoginUiEvents()
    data class UserNamerError(val userNamerError: String) : LoginUiEvents()
    data class PasswordError(val passwordError: String) : LoginUiEvents()
    data class LoginApiStats(val state: LoginApiState = LoginApiState.Idle) : LoginUiEvents()
    object LoginButtonClicked : LoginUiEvents()
}