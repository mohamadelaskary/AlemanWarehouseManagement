package net.gbs.honeywellscanner

import android.content.Context
import com.honeywell.aidc.AidcManager
import com.honeywell.aidc.BarcodeReader

object HoneywellScanner {

    private var manager: AidcManager? = null

    private var barcodeReader: BarcodeReader? = null

    private var isInitializing = false

    private var scannerConfig = ScannerConfig()

    fun create(): ScannerManager {

        val reader = barcodeReader
            ?: throw ScannerNotInitializedException()

        return ScannerManager(reader)
    }
    /**
     * Initializes Honeywell AIDC SDK.
     *
     * Call once from Application.onCreate().
     */
    fun initialize(
        context: Context,
        config: ScannerConfig = ScannerConfig(),
        onReady: (() -> Unit)? = null
    ) {

        if (barcodeReader != null) {
            onReady?.invoke()
            return
        }

        if (isInitializing) {
            return
        }

        isInitializing = true
        scannerConfig = config

        AidcManager.create(context.applicationContext) { aidcManager ->

            manager = aidcManager
            barcodeReader = aidcManager.createBarcodeReader()

            isInitializing = false

            onReady?.invoke()
        }
    }

    fun isInitialized(): Boolean {
        return barcodeReader != null
    }

    internal fun getBarcodeReader(): BarcodeReader? {
        return barcodeReader
    }

    internal fun getConfig(): ScannerConfig {
        return scannerConfig
    }

    fun destroy() {

        try {
            barcodeReader?.release()
        } catch (_: Exception) {
        }

        try {
            barcodeReader?.close()
        } catch (_: Exception) {
        }

        try {
            manager?.close()
        } catch (_: Exception) {
        }

        barcodeReader = null
        manager = null
        isInitializing = false
    }
}