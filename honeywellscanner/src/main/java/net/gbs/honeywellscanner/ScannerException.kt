package net.gbs.honeywellscanner


class ScannerNotInitializedException :
    IllegalStateException(
        "HoneywellScanner.initialize() must be called first."
    )