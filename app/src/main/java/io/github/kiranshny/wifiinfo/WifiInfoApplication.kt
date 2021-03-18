package io.github.kiranshny.wifiinfo

import android.app.Application
import timber.log.Timber

class WifiInfoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(HyperlinkedDebugTree(showMethodName = true))
    }
}