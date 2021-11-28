package com.mayada1994.moviewatchlist_hybrid.repositories

import com.mayada1994.moviewatchlist_hybrid.db.MovieDao
import com.mayada1994.moviewatchlist_hybrid.entities.Movie
import com.mayada1994.moviewatchlist_hybrid.entities.TmbdResponse
import com.mayada1994.moviewatchlist_hybrid.services.MoviesService
import io.reactivex.Completable
import io.reactivex.Single

class MoviesRepository(
    private val movieDao: MovieDao,
    private val moviesService: MoviesService,
    private val apiKey: String
) {

    //region Local

    fun getMovies(): Single<List<Movie>> = movieDao.getMovies()

    fun insertMovie(movie: Movie): Completable = movieDao.insertMovie(movie)

    fun deleteMovies(movies: List<Movie>): Completable = movieDao.deleteMovies(movies)

    //endregion

    //region Remote

    fun searchMovie(query: String): Single<TmbdResponse> = moviesService.searchMovie(apiKey, query)

    fun getPopularMovies(): Single<TmbdResponse> = moviesService.getPopularMovies(apiKey)

    fun getUpcomingMovies(): Single<TmbdResponse> = moviesService.getUpcomingMovies(apiKey)

    //endregion

}