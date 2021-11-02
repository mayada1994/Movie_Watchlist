package com.mayada1994.moviewatchlist_mvi.repositories

import com.mayada1994.moviewatchlist_mvi.db.MovieDao
import com.mayada1994.moviewatchlist_mvi.entities.Movie
import com.mayada1994.moviewatchlist_mvi.entities.TmbdResponse
import com.mayada1994.moviewatchlist_mvi.services.MoviesService
import io.reactivex.Completable
import io.reactivex.Single

class MoviesRepository(
    private val movieDao: MovieDao,
    private val moviesService: MoviesService,
    private val apiKey: String
) {

    //region Local

    fun getMovies(): Single<List<Movie>> = movieDao.getMovies()

    fun insertMovie(movie: Movie): Single<Long> = movieDao.insertMovie(movie)

    fun deleteMovies(movies: List<Movie>): Completable = movieDao.deleteMovies(movies)

    //endregion

    //region Remote

    fun searchMovie(query: String, page: Int): Single<TmbdResponse> = moviesService.searchMovie(apiKey, query, page)

    fun getPopularMovies(page: Int): Single<TmbdResponse> = moviesService.getPopularMovies(apiKey, page)

    fun getUpcomingMovies(page: Int): Single<TmbdResponse> = moviesService.getUpcomingMovies(apiKey, page)

    //endregion

}