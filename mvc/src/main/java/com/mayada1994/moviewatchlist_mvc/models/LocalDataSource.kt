package com.mayada1994.moviewatchlist_mvc.models

import com.mayada1994.moviewatchlist_mvc.db.MovieDao
import com.mayada1994.moviewatchlist_mvc.entities.Movie
import io.reactivex.Completable
import io.reactivex.Single

class LocalDataSource(private val movieDao: MovieDao) {

    fun getMovies(): Single<List<Movie>> = movieDao.getMovies()

    fun insertMovie(movie: Movie): Completable = movieDao.insertMovie(movie)

    fun deleteMovies(movies: List<Movie>): Completable = movieDao.deleteMovies(movies)

}