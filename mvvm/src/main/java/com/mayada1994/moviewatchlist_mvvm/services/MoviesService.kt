package com.mayada1994.moviewatchlist_mvvm.services

import com.mayada1994.moviewatchlist_mvvm.entities.TmbdResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface MoviesService {

    @GET("search/movie")
    fun searchMovie(@Query("api_key") apiKey: String, @Query("query") query: String, @Query("page") page: Int?): Single<TmbdResponse>

    @GET("movie/popular")
    fun getPopularMovies(@Query("api_key") apiKey: String, @Query("page") page: Int?): Single<TmbdResponse>

    @GET("movie/upcoming")
    fun getUpcomingMovies(@Query("api_key") apiKey: String, @Query("page") page: Int?): Single<TmbdResponse>

}