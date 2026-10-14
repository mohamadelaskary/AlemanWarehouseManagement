package net.gbs.gbswarehouse.ui.login

import net.gbs.gbswarehouse.domain.model.Message

sealed class LoginUiEvents {
    data class UsernameChanged(val username: String) : LoginUiEvents()
    data class PasswordChanged(val password: String) : LoginUiEvents()
    object TogglePasswordVisibility : LoginUiEvents()
    data class UserNamerError(val userNamerError: Message) : LoginUiEvents()
    data class PasswordError(val passwordError: Message) : LoginUiEvents()
    data class LoginApiStats(val state: LoginApiState = LoginApiState.Idle) : LoginUiEvents()
    object LoginButtonClicked : LoginUiEvents()
}