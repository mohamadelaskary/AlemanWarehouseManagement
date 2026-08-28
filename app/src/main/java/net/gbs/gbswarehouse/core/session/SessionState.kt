package net.gbs.gbswarehouse.core.session

sealed class SessionState {

    data object Authenticated : SessionState()

    data object Expired : SessionState()

    data object LoggedOut : SessionState()

}