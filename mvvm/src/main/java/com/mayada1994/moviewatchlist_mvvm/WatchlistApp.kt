package com.mayada1994.moviewatchlist_mvvm

import android.app.Application
import com.mayada1994.moviewatchlist_mvvm.di.WatchlistComponent
import timber.log.Timber

class WatchlistApp : Application() {

    override fun onCreate() {
        super.onCreate()

        WatchlistComponent.init(this)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

}