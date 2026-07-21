package net.gbs.gbswarehouse

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import net.gbs.honeywellscanner.HoneywellScanner


@HiltAndroidApp
class MyApplication: Application(){
    override fun onCreate() {
        super.onCreate()

        HoneywellScanner.initialize(this)
    }

    override fun onTerminate() {
        HoneywellScanner.destroy()
        super.onTerminate()
    }
}