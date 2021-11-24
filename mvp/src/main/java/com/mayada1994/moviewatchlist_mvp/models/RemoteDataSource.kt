package com.mayada1994.moviewatchlist_mvp.models

import com.mayada1994.moviewatchlist_mvp.entities.TmbdResponse
import com.mayada1994.moviewatchlist_mvp.services.MoviesService
import io.reactivex.Single

class RemoteDataSource(private val  moviesService: MoviesService, private val apiKey: String) {

    fun searchMovie(query: String): Single<TmbdResponse> = moviesService.searchMovie(apiKey, query)

    fun getPopularMovies(): Single<TmbdResponse> = moviesService.getPopularMovies(apiKey)

    fun getUpcomingMovies(): Single<TmbdResponse> = moviesService.getUpcomingMovies(apiKey)

}