package net.gbs.honeywellscanner

sealed interface ScanResult {

    data class Success(
        val barcode: String
    ) : ScanResult

    data class Failure(
        val message: String
    ) : ScanResult

}