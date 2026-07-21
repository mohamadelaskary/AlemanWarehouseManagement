package net.gbs.honeywellscanner

import com.honeywell.aidc.BarcodeFailureEvent
import com.honeywell.aidc.BarcodeReadEvent
import com.honeywell.aidc.BarcodeReader
import com.honeywell.aidc.ScannerNotClaimedException
import com.honeywell.aidc.ScannerUnavailableException
import com.honeywell.aidc.TriggerStateChangeEvent
import com.honeywell.aidc.UnsupportedPropertyException

class ScannerManager(
    private val reader: BarcodeReader
) {

    private var onScanListener: ((String) -> Unit)? = null

    fun setOnScanListener(listener: (String) -> Unit) {
        onScanListener = listener
    }

    private val barcodeListener = object : BarcodeReader.BarcodeListener {

        override fun onBarcodeEvent(event: BarcodeReadEvent) {
            onScanListener?.invoke(event.barcodeData.trim())
        }

        override fun onFailureEvent(event: BarcodeFailureEvent) {
            // Optional
        }
    }

    private val triggerListener = BarcodeReader.TriggerListener { event ->
        onTrigger(event)
    }

    init {
        installBarcodeReader()
    }

    private fun installBarcodeReader() {

        val config = HoneywellScanner.getConfig()

        try {
            reader.setProperty(
                BarcodeReader.PROPERTY_TRIGGER_CONTROL_MODE,
                BarcodeReader.TRIGGER_CONTROL_MODE_CLIENT_CONTROL
            )
        } catch (_: UnsupportedPropertyException) {
        }

        val properties = HashMap<String, Any>()

        properties["DATA_PROCESSOR_ENABLE_URI"] = false
        properties[BarcodeReader.PROPERTY_DATA_PROCESSOR_LAUNCH_BROWSER] = false
        properties["KEYBOARD_WEDGE_ENABLED"] = false
        properties["SCANWEDGE_ENABLE"] = false
        properties["data_processing_enabled"] = false
        properties["wedge.intent.enable"] = true
        properties["wedge.keyboard.enable"] = false
        properties["wedge.display.enable"] = false

        properties[BarcodeReader.PROPERTY_CODE_128_ENABLED] = config.code128
        properties[BarcodeReader.PROPERTY_GS1_128_ENABLED] = config.gs1_128
        properties[BarcodeReader.PROPERTY_QR_CODE_ENABLED] = config.qrCode
        properties[BarcodeReader.PROPERTY_CODE_39_ENABLED] = config.code39
        properties[BarcodeReader.PROPERTY_DATAMATRIX_ENABLED] = config.dataMatrix
        properties[BarcodeReader.PROPERTY_UPC_A_ENABLE] = config.upcA
        properties[BarcodeReader.PROPERTY_EAN_13_ENABLED] = config.ean13
        properties[BarcodeReader.PROPERTY_AZTEC_ENABLED] = config.aztec
        properties[BarcodeReader.PROPERTY_CODABAR_ENABLED] = config.codabar
        properties[BarcodeReader.PROPERTY_INTERLEAVED_25_ENABLED] = config.interleaved25
        properties[BarcodeReader.PROPERTY_PDF_417_ENABLED] = config.pdf417

        properties[BarcodeReader.PROPERTY_CODE_39_MAXIMUM_LENGTH] =
            config.code39MaxLength

        properties[BarcodeReader.PROPERTY_CENTER_DECODE] =
            config.centerDecode

        properties[BarcodeReader.PROPERTY_NOTIFICATION_BAD_READ_ENABLED] =
            config.notificationBadRead

        properties[BarcodeReader.PROPERTY_EAN_13_CHECK_DIGIT_TRANSMIT_ENABLED] = true

        reader.setProperties(properties)
    }

    private fun onTrigger(event: TriggerStateChangeEvent) {

        try {
            reader.aim(event.state)
            reader.light(event.state)
            reader.decode(event.state)
        } catch (e: ScannerNotClaimedException) {
            e.printStackTrace()
        } catch (e: ScannerUnavailableException) {
            e.printStackTrace()
        }
    }

    fun start() {

        try {
            reader.claim()
            reader.addBarcodeListener(barcodeListener)
            reader.addTriggerListener(triggerListener)
        } catch (e: ScannerUnavailableException) {
            e.printStackTrace()
        }
    }

    fun stop() {

        try {
            reader.release()
        } catch (_: Exception) {
        }

        reader.removeBarcodeListener(barcodeListener)
        reader.removeTriggerListener(triggerListener)

        onScanListener = null
    }
}