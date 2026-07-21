package net.gbs.honeywellscanner

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember



@Composable
fun ScannerEffect(
    onScan: (String) -> Unit
) {

    val scanner = remember {
        HoneywellScanner.create()
    }

    DisposableEffect(scanner) {

        scanner.setOnScanListener(onScan)

        scanner.start()

        onDispose {

            scanner.stop()

        }
    }
}