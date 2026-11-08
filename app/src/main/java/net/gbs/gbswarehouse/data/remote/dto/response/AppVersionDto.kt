package net.gbs.gbswarehouse.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class AppVersionDto(
    @SerializedName("LatestVersionCode") val latestVersionCode: Int,
    @SerializedName("LatestVersionName") val latestVersionName: String?,
    @SerializedName("PackageName") val packageName: String?,
    @SerializedName("DownloadUrl") val downloadUrl: String?,
    @SerializedName("FileSize") val fileSize: Long?,
    @SerializedName("Sha256") val sha256: String?,
    @SerializedName("ReleaseNotes") val releaseNotes: String?,
    @SerializedName("UploadedAt") val uploadedAt: String?
)
