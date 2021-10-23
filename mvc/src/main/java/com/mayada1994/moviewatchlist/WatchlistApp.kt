package com.mayada1994.moviewatchlist

import android.app.Application
import timber.log.Timber

class WatchlistApp : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

}