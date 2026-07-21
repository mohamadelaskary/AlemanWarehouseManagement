package net.gbs.honeywellscanner


data class ScannerConfig(

    val code128: Boolean = true,

    val gs1_128: Boolean = true,

    val qrCode: Boolean = true,

    val code39: Boolean = true,

    val dataMatrix: Boolean = true,

    val upcA: Boolean = true,

    val ean13: Boolean = true,

    val aztec: Boolean = true,

    val codabar: Boolean = true,

    val interleaved25: Boolean = true,

    val pdf417: Boolean = true,

    val centerDecode: Boolean = true,

    val notificationBadRead: Boolean = true,

    val code39MaxLength: Int = 30

)