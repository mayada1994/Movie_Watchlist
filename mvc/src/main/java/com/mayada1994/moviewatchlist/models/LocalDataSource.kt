package com.mayada1994.moviewatchlist.models

import com.mayada1994.moviewatchlist.db.MovieDao
import com.mayada1994.moviewatchlist.di.WatchlistComponent
import com.mayada1994.moviewatchlist.entities.Movie
import io.reactivex.Completable
import io.reactivex.Single

class LocalDataSource {

    private val movieDao: MovieDao = WatchlistComponent.movieDao

    fun getMovies(): Single<List<Movie>> = movieDao.getMovies()

    fun insertMovie(movie: Movie): Completable = movieDao.insertMovie(movie)

    fun deleteMovie(id: Int): Completable = movieDao.deleteMovie(id)

}