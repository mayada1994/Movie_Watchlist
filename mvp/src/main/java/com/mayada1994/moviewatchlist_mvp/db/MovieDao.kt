package com.mayada1994.moviewatchlist_mvp.db

import androidx.room.*
import com.mayada1994.moviewatchlist_mvp.entities.Movie
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface MovieDao {

    @Query("SELECT * FROM movies")
    fun getMovies(): Single<List<Movie>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMovie(movie: Movie): Completable

    @Delete
    fun deleteMovies(movies: List<Movie>): Completable

}