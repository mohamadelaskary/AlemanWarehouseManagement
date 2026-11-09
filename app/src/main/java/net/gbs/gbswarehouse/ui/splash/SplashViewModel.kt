package net.gbs.gbswarehouse.ui.splash

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.gbs.gbswarehouse.core.network.NetworkResult
import net.gbs.gbswarehouse.core.update.AppUpdateManager
import net.gbs.gbswarehouse.core.update.UpdateState
import net.gbs.gbswarehouse.data.remote.repository.AppVersionRepository
import javax.inject.Inject

sealed interface SplashNavigationEvent {
    object NavigateToLogin : SplashNavigationEvent
    object NavigateToUpdate : SplashNavigationEvent
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val appVersionRepository: AppVersionRepository,
    val appUpdateManager: AppUpdateManager,
    @ApplicationContext val context : Context
) : ViewModel() {
    private val tag = "SplashViewModel"

    private val _navigationEvent = MutableSharedFlow<SplashNavigationEvent>()
    val navigationEvent: SharedFlow<SplashNavigationEvent> = _navigationEvent.asSharedFlow()

    private val _checkFinished = MutableStateFlow(false)
    val checkFinished: StateFlow<Boolean> = _checkFinished.asStateFlow()

    private val _entranceCompleted = MutableStateFlow(false)
    val entranceCompleted: StateFlow<Boolean> = _entranceCompleted.asStateFlow()

    private var hasCheckedVersion = false
    private var updatePending = false

    init {
        checkVersion()
    }

    fun checkVersion() {
        if (hasCheckedVersion) return
        hasCheckedVersion = true
        viewModelScope.launch {
            Log.d(tag, "checkVersion() started: calling AppVersion endpoint...")
            appUpdateManager.updateState(UpdateState.Checking)

            val result = appVersionRepository.checkForUpdate()
            Log.d(tag, "checkVersion(): checkForUpdate() result = $result")

            if (result is NetworkResult.Success) {
//                 Update check on splash bypassed to allow debugging and testing update flow from login screen
                 val appVersion = result.data
                 val installedCode = appVersionRepository.getInstalledVersionCode()
                 val isUpdate = appUpdateManager.processVersion(appVersion, installedCode, context.packageName)
                 if (isUpdate) {
                     updatePending = true
                 }
            } else {
                appUpdateManager.updateState(UpdateState.NoUpdate)
            }

            _checkFinished.value = true
            tryNavigateIfNeeded()
        }
    }

    fun onEntranceAnimationCompleted() {
        Log.d(tag, "onEntranceAnimationCompleted()")
        _entranceCompleted.value = true
        viewModelScope.launch {
            tryNavigateIfNeeded()
        }
    }

    private suspend fun tryNavigateIfNeeded() {
        if (_entranceCompleted.value && _checkFinished.value) {
            Log.d(tag, "tryNavigateIfNeeded(): updatePending=$updatePending, waiting 600ms before navigating...")
            kotlinx.coroutines.delay(600L)
            if (updatePending) {
                _navigationEvent.emit(SplashNavigationEvent.NavigateToUpdate)
            } else {
                _navigationEvent.emit(SplashNavigationEvent.NavigateToLogin)
            }
        }
    }
}
