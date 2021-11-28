package com.mayada1994.moviewatchlist_mvp.models

import com.mayada1994.moviewatchlist_mvp.db.MovieDao
import com.mayada1994.moviewatchlist_mvp.entities.Movie
import io.reactivex.Completable
import io.reactivex.Single

class LocalDataSource(private val movieDao: MovieDao) {

    fun getMovies(): Single<List<Movie>> = movieDao.getMovies()

    fun insertMovie(movie: Movie): Completable = movieDao.insertMovie(movie)

    fun deleteMovies(movies: List<Movie>): Completable = movieDao.deleteMovies(movies)

}