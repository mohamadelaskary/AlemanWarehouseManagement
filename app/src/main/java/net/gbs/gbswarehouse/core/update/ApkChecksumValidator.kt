package net.gbs.gbswarehouse.core.update

import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApkChecksumValidator @Inject constructor() {
    private val tag = "ApkChecksumValidator"

    fun validate(apkFile: File, expectedSha256: String?, expectedFileSize: Long?): Boolean {
        if (!apkFile.exists()) {
            Log.e(tag, "APK file does not exist")
            return false
        }

        if (expectedFileSize != null && expectedFileSize > 0) {
            val actualSize = apkFile.length()
            if (actualSize != expectedFileSize) {
                Log.e(tag, "File size mismatch: expected=$expectedFileSize, actual=$actualSize")
                return false
            }
        }

        if (expectedSha256.isNullOrBlank()) {
            Log.w(tag, "Expected SHA-256 hash is null or blank, skipping checksum validation")
            return true
        }

        val calculatedHash = calculateSha256(apkFile) ?: return false
        val trimmedExpected = expectedSha256.trim()

        val isValid = calculatedHash.equals(trimmedExpected, ignoreCase = true)
        if (!isValid) {
            Log.e(tag, "SHA-256 mismatch: expected=$trimmedExpected, calculated=$calculatedHash")
        } else {
            Log.d(tag, "SHA-256 validation passed successfully")
        }
        return isValid
    }

    private fun calculateSha256(file: File): String? {
        return try {
            val digest = MessageDigest.getInstance("SHA-256")
            FileInputStream(file).use { fis ->
                val buffer = ByteArray(8192)
                var bytesRead: Int
                while (fis.read(buffer).also { bytesRead = it } != -1) {
                    digest.update(buffer, 0, bytesRead)
                }
            }
            digest.digest().joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            Log.e(tag, "Error calculating SHA-256", e)
            null
        }
    }
}
