package com.mayada1994.moviewatchlist.di

import android.app.Application
import androidx.room.Room
import com.mayada1994.moviewatchlist.BuildConfig
import com.mayada1994.moviewatchlist.R
import com.mayada1994.moviewatchlist.db.MovieDao
import com.mayada1994.moviewatchlist.db.WatchlistDatabase
import com.mayada1994.moviewatchlist.models.LocalDataSource
import com.mayada1994.moviewatchlist.models.RemoteDataSource
import com.mayada1994.moviewatchlist.services.MoviesService
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

    val remoteDataSource: RemoteDataSource by lazy {
        RemoteDataSource(moviesService, application.getString(R.string.api_key))
    }
    //endregion

    //region DB
    private val database by lazy {
        Room.databaseBuilder(
            application.applicationContext,
            WatchlistDatabase::class.java, "watchlist.db"
        ).build()
    }

    private val movieDao: MovieDao by lazy { database.movieDao() }

    val localDataSource: LocalDataSource by lazy { LocalDataSource(movieDao) }
    //endregion

    fun init(application: Application) {
        this.application = application
    }

}