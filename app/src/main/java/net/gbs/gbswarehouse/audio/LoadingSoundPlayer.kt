package net.gbs.gbswarehouse.audio

import android.media.AudioManager
import android.media.ToneGenerator
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Distinct, short system tones. No bundled audio assets and no extra dependency.
 * Each tone keeps its own [ToneGenerator] so repeated events don't allocate,
 * and everything is released in [release].
 */
@Singleton
class LoadingSoundPlayer @Inject constructor() {

    private class Slot(private val tone: Int, private val durationMs: Int) {
        private var generator: ToneGenerator? = null

        fun play() {
            try {
                val active = generator
                    ?: ToneGenerator(AudioManager.STREAM_NOTIFICATION, VOLUME).also { generator = it }
                active.startTone(tone, durationMs)
            } catch (_: RuntimeException) {
                // ToneGenerator can throw when audio output is unavailable;
                // a sound must never crash the app.
            }
        }

        fun release() {
            generator?.release()
            generator = null
        }
    }

    private val bagSlot = Slot(ToneGenerator.TONE_PROP_BEEP, BAG_DURATION_MS)
    private val successSlot = Slot(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, SUCCESS_DURATION_MS)
    private val failureSlot = Slot(ToneGenerator.TONE_CDMA_ABBR_REORDER, FAILURE_DURATION_MS)

    fun playBagCounted() = bagSlot.play()

    fun playSuccess() = successSlot.play()

    fun playFailure() = failureSlot.play()

    fun release() {
        bagSlot.release()
        successSlot.release()
        failureSlot.release()
    }

    companion object {
        private const val VOLUME = 80
        private const val BAG_DURATION_MS = 120
        private const val SUCCESS_DURATION_MS = 500
        private const val FAILURE_DURATION_MS = 700
    }
}