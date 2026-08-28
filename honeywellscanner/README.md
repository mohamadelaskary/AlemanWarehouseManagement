// Initialize
HoneywellScanner.initialize(applicationContext)

// Compose
ScannerEffect { barcode ->
    Log.d("Scanner", barcode)
}