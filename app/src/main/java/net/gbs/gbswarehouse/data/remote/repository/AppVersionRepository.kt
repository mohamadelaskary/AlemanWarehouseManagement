package net.gbs.gbswarehouse.data.remote.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import net.gbs.gbswarehouse.core.network.NetworkResult
import net.gbs.gbswarehouse.core.preferences.SettingsPrefs
import net.gbs.gbswarehouse.data.remote.api.ApiService
import net.gbs.gbswarehouse.data.remote.dto.response.AppVersionDto
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppVersionRepository @Inject constructor(
    private val apiService: ApiService,
    private val settingsPrefs: SettingsPrefs,
    @ApplicationContext private val context: Context
) {
    private val tag = "AppVersionRepository"

    fun getPackageName(): String = context.packageName

    fun getTargetUrl(): String {
        return "${settingsPrefs.getBaseUrl()}api/AccountHH/AppVersion"
    }

    fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager ?: return false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } else {
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo
            @Suppress("DEPRECATION")
            return networkInfo != null && networkInfo.isConnected
        }
    }

    fun getInstalledVersionCode(): Int {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode.toInt()
            } else {
                @Suppress("DEPRECATION")
                packageInfo.versionCode
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting installed version code", e)
            1
        }
    }

    suspend fun checkForUpdate(): NetworkResult<AppVersionDto?> {
        try {
            val response = apiService.getAppVersion()
            if (!response.isSuccessful) {
                return NetworkResult.HttpError(response.code(), response.errorBody()?.string() ?: response.message())
            }

            val jsonObject = response.body() ?: return NetworkResult.Success(null)

            val versionObj = when {
                jsonObject.has("LatestVersionCode") || jsonObject.has("latestVersionCode") -> jsonObject
                jsonObject.has("data") && jsonObject.get("data").isJsonObject -> jsonObject.getAsJsonObject("data")
                jsonObject.has("result") && jsonObject.get("result").isJsonObject -> jsonObject.getAsJsonObject("result")
                else -> jsonObject
            }

            if (!versionObj.has("LatestVersionCode") && !versionObj.has("latestVersionCode") && !versionObj.has("LatestVersionName")) {
                return NetworkResult.Success(null)
            }

            val latestVersionCode = getInt(versionObj, "LatestVersionCode", "latestVersionCode")
            if (latestVersionCode <= 0) {
                return NetworkResult.Success(null)
            }

            val latestVersionName = getString(versionObj, "LatestVersionName", "latestVersionName")
            val packageName = getString(versionObj, "PackageName", "packageName")
            val downloadUrl = getString(versionObj, "DownloadUrl", "downloadUrl")
            val fileSize = getLong(versionObj, "FileSize", "fileSize")
            val sha256 = getString(versionObj, "Sha256", "sha256", "SHA256")
            val releaseNotes = getString(versionObj, "ReleaseNotes", "releaseNotes")
            val uploadedAt = getString(versionObj, "UploadedAt", "uploadedAt")

            val appVersion = AppVersionDto(
                latestVersionCode = latestVersionCode,
                latestVersionName = latestVersionName,
                packageName = packageName,
                downloadUrl = downloadUrl,
                fileSize = fileSize,
                sha256 = sha256,
                releaseNotes = releaseNotes,
                uploadedAt = uploadedAt
            )

            return NetworkResult.Success(appVersion)
        } catch (e: java.net.SocketTimeoutException) {
            Log.e(tag, "Socket timeout checking update", e)
            return NetworkResult.NetworkError("Connection timeout (${getTargetUrl()})")
        } catch (e: java.net.ConnectException) {
            Log.e(tag, "Connection refused checking update", e)
            return NetworkResult.NetworkError("Connection refused to ${getTargetUrl()} (Check IP/Port/Emulator localhost)")
        } catch (e: java.io.IOException) {
            Log.e(tag, "Network error checking update", e)
            return NetworkResult.NetworkError("${e.message ?: "Network error"} (${getTargetUrl()})")
        } catch (e: Exception) {
            Log.e(tag, "Unexpected error checking update", e)
            return NetworkResult.UnknownError("${e.message ?: "Unknown error"} (${getTargetUrl()})")
        }
    }

    private fun getInt(obj: com.google.gson.JsonObject, vararg keys: String): Int {
        for (key in keys) {
            if (obj.has(key) && !obj.get(key).isJsonNull) {
                return obj.get(key).asInt
            }
        }
        return 0
    }

    private fun getLong(obj: com.google.gson.JsonObject, vararg keys: String): Long {
        for (key in keys) {
            if (obj.has(key) && !obj.get(key).isJsonNull) {
                return obj.get(key).asLong
            }
        }
        return 0L
    }

    private fun getString(obj: com.google.gson.JsonObject, vararg keys: String): String? {
        for (key in keys) {
            if (obj.has(key) && !obj.get(key).isJsonNull) {
                return obj.get(key).asString
            }
        }
        return null
    }
}
