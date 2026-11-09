package net.gbs.gbswarehouse.data.remote.dto.response

import com.google.gson.annotations.SerializedName

data class AppVersionDto(
    @SerializedName("LatestVersionCode", alternate = ["latestVersionCode", "Latestversioncode", "latestversioncode"]) val latestVersionCode: Int,
    @SerializedName("LatestVersionName", alternate = ["latestVersionName", "Latestversionname", "latestversionname"]) val latestVersionName: String?,
    @SerializedName("PackageName", alternate = ["packageName", "Packagename", "packagename"]) val packageName: String?,
    @SerializedName("DownloadUrl", alternate = ["downloadUrl", "Downloadurl", "downloadurl"]) val downloadUrl: String?,
    @SerializedName("FileSize", alternate = ["fileSize", "Filesize", "filesize"]) val fileSize: Long?,
    @SerializedName("Sha256", alternate = ["sha256", "SHA256"]) val sha256: String?,
    @SerializedName("ReleaseNotes", alternate = ["releaseNotes", "Releasenotes", "releasenotes"]) val releaseNotes: String?,
    @SerializedName("UploadedAt", alternate = ["uploadedAt", "Uploadedat", "uploadedat"]) val uploadedAt: String?
)
