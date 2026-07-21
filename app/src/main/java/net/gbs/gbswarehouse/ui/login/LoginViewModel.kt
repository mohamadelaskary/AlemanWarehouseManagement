package net.gbs.gbswarehouse.ui.login

import android.app.Application
import android.content.Context
import android.service.autofill.UserData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.gbs.gbswarehouse.MyApplication
import net.gbs.gbswarehouse.R
import net.gbs.gbswarehouse.core.network.NetworkResult
import net.gbs.gbswarehouse.data.remote.dto.response.LoginResponse
import net.gbs.gbswarehouse.data.remote.mapper.toUser
import net.gbs.gbswarehouse.domain.usecase.LoginUseCase
import javax.inject.Inject

@HiltViewModel
// ── ViewModel ─────────────────────────────────────────────────────────────────
class LoginViewModel @Inject constructor(
    @ApplicationContext
    private val context: Context,
    private val loginUseCase: LoginUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()
    fun onEvent(events: LoginUiEvents){
        when(events){
            is LoginUiEvents.LoginApiStats -> {
                _uiState.value = _uiState.value.copy(loginApiState = events.state)
            }
            LoginUiEvents.LoginButtonClicked -> login()
            is LoginUiEvents.PasswordChanged -> {
                _uiState.update {
                    it.copy(
                        password = events.password,
                        passwordError = null
                    )
                }
            }
            is LoginUiEvents.PasswordError -> _uiState.value = _uiState.value.copy(passwordError = events.passwordError)
            LoginUiEvents.TogglePasswordVisibility -> _uiState.value = _uiState.value.copy(passwordVisible = !_uiState.value.passwordVisible)
            is LoginUiEvents.UserNamerError -> _uiState.value = _uiState.value.copy(usernameError = events.userNamerError)
            is LoginUiEvents.UsernameChanged -> {
                _uiState.update {
                    it.copy(
                        username = events.username,
                        usernameError = null
                    )
                }
            }
        }
    }

    private fun login(){
        val username = _uiState.value.username
        val password = _uiState.value.password
        if (username.isBlank()){
            _uiState.value = _uiState.value.copy(usernameError = context.getString(R.string.please_enter_username))
            return
        }
        if (password.isBlank()){
            _uiState.value = _uiState.value.copy(passwordError = context.getString(R.string.please_enter_password))
            return
        }
        viewModelScope.launch {
            val result = loginUseCase(
                userName = username,
                password = password
            )
            when(result){
                is NetworkResult.Error -> {
                    _uiState.value = _uiState.value.copy(loginApiState = LoginApiState.Error(result.message))
                }
                NetworkResult.Loading -> {
                    _uiState.value = _uiState.value.copy(loginApiState = LoginApiState.Loading)
                }
                is NetworkResult.Success<*> -> {
                    val response = result.data as LoginResponse
                    _uiState.value = _uiState.value.copy(loginApiState = LoginApiState.Success(response.data.toUser(),response.responseStatus.statusMessage))
                }
            }

        }
    }
}