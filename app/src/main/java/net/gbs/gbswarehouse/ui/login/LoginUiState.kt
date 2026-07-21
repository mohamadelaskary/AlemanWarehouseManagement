package net.gbs.gbswarehouse.ui.login

import net.gbs.gbswarehouse.domain.model.User

data class LoginUiState (
    // ── Form fields ────────────────────────────────────────────────────────
    val username        :String = "",
    val password       :String = "",
    val passwordVisible : Boolean = false,
    // ── Validation errors ──────────────────────────────────────────────────
    val usernameError :String? = null,
    val passwordError :String?=null,
    // ── Login Api state ───────────────────────────────────────────────────────────
    val loginApiState : LoginApiState = LoginApiState.Idle,
)

sealed class LoginApiState {
    object Idle : LoginApiState()
    object Loading : LoginApiState()
    data class Success(val userData: User,val message: String) : LoginApiState()
    data class Error(val message: String) : LoginApiState()
}