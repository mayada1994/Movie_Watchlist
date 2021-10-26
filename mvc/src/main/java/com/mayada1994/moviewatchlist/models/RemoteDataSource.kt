package com.mayada1994.moviewatchlist.models

import com.mayada1994.moviewatchlist.entities.TmbdResponse
import com.mayada1994.moviewatchlist.services.MoviesService
import io.reactivex.Single

class RemoteDataSource(private val  moviesService: MoviesService, private val apiKey: String) {

    fun searchMovie(query: String, page: Int): Single<TmbdResponse> = moviesService.searchMovie(apiKey, query, page)

    fun getPopularMovies(page: Int): Single<TmbdResponse> = moviesService.getPopularMovies(apiKey, page)

    fun getUpcomingMovies(page: Int): Single<TmbdResponse> = moviesService.getUpcomingMovies(apiKey, page)

}