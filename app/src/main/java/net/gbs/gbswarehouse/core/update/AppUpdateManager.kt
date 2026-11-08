package net.gbs.gbswarehouse.core.update

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import net.gbs.gbswarehouse.data.remote.dto.response.AppVersionDto
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

sealed interface UpdateState {
    object Idle : UpdateState
    object Checking : UpdateState
    object NoUpdate : UpdateState
    data class UpdateAvailable(val appVersion: AppVersionDto) : UpdateState
    data class Downloading(val progress: Float, val downloadedBytes: Long, val totalBytes: Long) : UpdateState
    data class DownloadCompleted(val file: File, val appVersion: AppVersionDto) : UpdateState
    object Validating : UpdateState
    object Installing : UpdateState
    data class Error(val message: String) : UpdateState
}

@Singleton
class AppUpdateManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val _updateState = MutableStateFlow<UpdateState>(UpdateState.Idle)
    val updateState: StateFlow<UpdateState> = _updateState.asStateFlow()

    private var currentAppVersion: AppVersionDto? = null

    fun updateState(state: UpdateState) {
        _updateState.value = state
        if (state is UpdateState.UpdateAvailable) {
            currentAppVersion = state.appVersion
        } else if (state is UpdateState.DownloadCompleted) {
            currentAppVersion = state.appVersion
        }
    }

    fun getLatestAppVersion(): AppVersionDto? = currentAppVersion

    fun processVersionDto(appVersion: AppVersionDto?): Boolean {
        if (appVersion == null) return false
        val installedCode = try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                packageInfo.longVersionCode.toInt()
            } else {
                @Suppress("DEPRECATION")
                packageInfo.versionCode
            }
        } catch (_: Exception) {
            1
        }
        return processVersion(appVersion, installedCode, context.packageName)
    }

    fun processVersion(appVersion: AppVersionDto?, installedVersionCode: Int, packageName: String): Boolean {
        if (appVersion == null) {
            if (_updateState.value is UpdateState.Idle || _updateState.value is UpdateState.Checking) {
                _updateState.value = UpdateState.NoUpdate
            }
            return false
        }

        if (!appVersion.packageName.isNullOrBlank() && appVersion.packageName != packageName) {
            Log.w("AppUpdateManager", "Package name mismatch: remote=${appVersion.packageName}, local=$packageName")
            return false
        }

        if (installedVersionCode < appVersion.latestVersionCode) {
            Log.d("AppUpdateManager", "Update available: installed=$installedVersionCode, latest=${appVersion.latestVersionCode}")
            _updateState.value = UpdateState.UpdateAvailable(appVersion)
            currentAppVersion = appVersion
            return true
        } else {
            if (_updateState.value is UpdateState.Idle || _updateState.value is UpdateState.Checking) {
                _updateState.value = UpdateState.NoUpdate
            }
            return false
        }
    }
}
