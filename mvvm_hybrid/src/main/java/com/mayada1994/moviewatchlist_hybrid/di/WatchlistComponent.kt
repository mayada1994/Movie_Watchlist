package com.mayada1994.moviewatchlist_hybrid.di

import android.app.Application
import androidx.room.Room
import com.mayada1994.moviewatchlist_hybrid.BuildConfig
import com.mayada1994.moviewatchlist_hybrid.R
import com.mayada1994.moviewatchlist_hybrid.db.MovieDao
import com.mayada1994.moviewatchlist_hybrid.db.WatchlistDatabase
import com.mayada1994.moviewatchlist_hybrid.repositories.MoviesRepository
import com.mayada1994.moviewatchlist_hybrid.services.MoviesService
import com.mayada1994.moviewatchlist_hybrid.viewmodels.ViewModelFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object WatchlistComponent {

    private lateinit var application: Application

    //region API
    private const val BASE_URL = "https://api.themoviedb.org/3/"

    private val okHttpClient by lazy {
        OkHttpClient().newBuilder().apply {
            if (BuildConfig.BUILD_TYPE != "release") {
                val interceptor = HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
                addInterceptor(interceptor)
            }
        }.build()
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    private val moviesService: MoviesService by lazy { retrofit.create(MoviesService::class.java) }
    //endregion

    //region DB
    private val database by lazy {
        Room.databaseBuilder(
            application.applicationContext,
            WatchlistDatabase::class.java, "watchlist.db"
        ).build()
    }

    private val movieDao: MovieDao by lazy { database.movieDao() }

    private val moviesRepository: MoviesRepository by lazy {
        MoviesRepository(
            movieDao,
            moviesService,
            application.getString(R.string.api_key)
        )
    }

    val viewModelFactory: ViewModelFactory by lazy { ViewModelFactory(moviesRepository) }

    //endregion

    fun init(application: Application) {
        WatchlistComponent.application = application
    }

}