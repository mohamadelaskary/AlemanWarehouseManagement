package net.gbs.gbswarehouse.ui.update

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.HttpUrl.Companion.toHttpUrl
import net.gbs.gbswarehouse.core.preferences.SettingsPrefs
import net.gbs.gbswarehouse.core.update.ApkChecksumValidator
import net.gbs.gbswarehouse.core.update.ApkDownloadManager
import net.gbs.gbswarehouse.core.update.ApkInstaller
import net.gbs.gbswarehouse.core.update.AppUpdateManager
import net.gbs.gbswarehouse.core.update.UpdateState
import javax.inject.Inject

@HiltViewModel
class UpdateViewModel @Inject constructor(
    val appUpdateManager: AppUpdateManager,
    private val apkDownloadManager: ApkDownloadManager,
    private val apkChecksumValidator: ApkChecksumValidator,
    private val apkInstaller: ApkInstaller,
    private val settingsPrefs: SettingsPrefs,
    @ApplicationContext private val context: Context
) : ViewModel() {

    val updateState: StateFlow<UpdateState> = appUpdateManager.updateState

    fun getResolvedDownloadUrl(): String {
        val appVersion = appUpdateManager.getLatestAppVersion() ?: return "N/A"
        val rawUrl = appVersion.downloadUrl ?: return "N/A"
        val baseUrl = settingsPrefs.getBaseUrl()
        return try {
            val parsedUrl = rawUrl.toHttpUrl()
            val pathSegments = parsedUrl.encodedPath
            val query = parsedUrl.encodedQuery
            val baseHttpUrl = baseUrl.toHttpUrl()
            baseHttpUrl.newBuilder().encodedPath(pathSegments).apply {
                if (!query.isNullOrBlank()) encodedQuery(query)
            }.build().toString()
        } catch (_: Exception) {
            if (rawUrl.startsWith("/")) {
                val base = baseUrl.removeSuffix("/")
                "$base$rawUrl"
            } else {
                rawUrl
            }
        }
    }

    fun startDownload() {
        val appVersion = appUpdateManager.getLatestAppVersion() ?: return
        val url = appVersion.downloadUrl ?: return

        viewModelScope.launch {
            appUpdateManager.updateState(UpdateState.Downloading(0f, 0L, appVersion.fileSize ?: 0L))

            val downloadedFile = apkDownloadManager.downloadApk(url, appVersion.fileSize)
            if (downloadedFile == null || !downloadedFile.exists()) {
                return@launch
            }

            appUpdateManager.updateState(UpdateState.Validating)

            val isValid = apkChecksumValidator.validate(downloadedFile, appVersion.sha256, appVersion.fileSize)
            if (!isValid) {
                downloadedFile.delete()
                appUpdateManager.updateState(UpdateState.Error("APK validation failed (SHA-256 or size mismatch)"))
                return@launch
            }

            appUpdateManager.updateState(UpdateState.DownloadCompleted(downloadedFile, appVersion))
        }
    }

    fun installApk(activity: Activity) {
        val state = updateState.value
        val file = when (state) {
            is UpdateState.DownloadCompleted -> state.file
            else -> return
        }

        if (!apkInstaller.canInstallPackages(context)) {
            apkInstaller.openInstallPermissionSettings(activity)
            return
        }

        appUpdateManager.updateState(UpdateState.Installing)
        apkInstaller.installApk(context, file)
    }
}
