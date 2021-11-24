package com.mayada1994.moviewatchlist.models

import com.mayada1994.moviewatchlist.entities.TmbdResponse
import com.mayada1994.moviewatchlist.services.MoviesService
import io.reactivex.Single

class RemoteDataSource(private val  moviesService: MoviesService, private val apiKey: String) {

    fun searchMovie(query: String): Single<TmbdResponse> = moviesService.searchMovie(apiKey, query)

    fun getPopularMovies(): Single<TmbdResponse> = moviesService.getPopularMovies(apiKey)

    fun getUpcomingMovies(): Single<TmbdResponse> = moviesService.getUpcomingMovies(apiKey)

}