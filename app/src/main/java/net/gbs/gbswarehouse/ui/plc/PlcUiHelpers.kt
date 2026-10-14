package net.gbs.gbswarehouse.ui.plc

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import net.gbs.gbswarehouse.BLUE
import net.gbs.gbswarehouse.DANGER
import net.gbs.gbswarehouse.GRAY
import net.gbs.gbswarehouse.GREEN
import net.gbs.gbswarehouse.ORANGE
import net.gbs.gbswarehouse.PURPLE
import net.gbs.gbswarehouse.R
import net.gbs.gbswarehouse.SUCC
import net.gbs.gbswarehouse.domain.model.ConveyorStatus

val PLC_ACCENT = PURPLE

@Composable
fun ConveyorStatus.label(): String = when (this) {
    ConveyorStatus.IDLE -> stringResource(R.string.conveyor_idle)
    ConveyorStatus.RUNNING -> stringResource(R.string.conveyor_running)
    ConveyorStatus.PAUSED -> stringResource(R.string.conveyor_paused)
    ConveyorStatus.ERROR -> stringResource(R.string.conveyor_error)
    ConveyorStatus.COMPLETED -> stringResource(R.string.conveyor_completed)
    ConveyorStatus.MANUAL -> stringResource(R.string.conveyor_manual)
}

fun ConveyorStatus.color(): Color = when (this) {
    ConveyorStatus.IDLE -> GRAY
    ConveyorStatus.RUNNING -> GREEN
    ConveyorStatus.PAUSED -> ORANGE
    ConveyorStatus.ERROR -> DANGER
    ConveyorStatus.COMPLETED -> SUCC
    ConveyorStatus.MANUAL -> BLUE
}

/** Placeholder for optional backend fields so InfoCell never gets a blank value. */
fun String?.orDash(): String = this?.takeIf { it.isNotBlank() } ?: "-"

/** Trims trailing zeros so bag weights read "50" instead of "50.0". */
fun Double.orTrimmed(): String =
    if (this % 1.0 == 0.0) toLong().toString() else "%.1f".format(this)