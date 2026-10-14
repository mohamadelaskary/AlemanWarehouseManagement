package net.gbs.gbswarehouse.ui.plc

import android.content.Context
import net.gbs.gbswarehouse.service.LoadingService

fun Context.startLoadingService(operationId: String) {
    LoadingService.start(this, operationId)
}

fun Context.stopLoadingService() {
    LoadingService.stop(this)
}