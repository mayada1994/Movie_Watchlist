package com.mayada1994.moviewatchlist.di

import android.app.Application
import com.mayada1994.moviewatchlist.BuildConfig
import com.mayada1994.moviewatchlist.services.MoviesService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object WatchlistComponent {

    private lateinit var application: Application

    private const val BASE_URL = "http://api.themoviedb.org/3/"

    private val okHttpClient = OkHttpClient().newBuilder().apply {
        if (BuildConfig.BUILD_TYPE != "release") {
            val interceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }
            addInterceptor(interceptor)
        }
    }.build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()

    val moviesService = retrofit.create(MoviesService::class.java)

    fun init(application: Application) {
        this.application = application
    }

}