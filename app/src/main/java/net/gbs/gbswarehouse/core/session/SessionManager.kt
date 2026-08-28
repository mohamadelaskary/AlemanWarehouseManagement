package net.gbs.gbswarehouse.core.session

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor() {
    private val _sessionState =
        MutableStateFlow<SessionState>(
            SessionState.Authenticated
        )

    val sessionState: StateFlow<SessionState> =
        _sessionState.asStateFlow()

    fun sessionExpired() {
        _sessionState.value = SessionState.Expired
    }

    fun logout() {
        _sessionState.value = SessionState.LoggedOut
    }

    fun authenticated() {
        _sessionState.value = SessionState.Authenticated
    }
}