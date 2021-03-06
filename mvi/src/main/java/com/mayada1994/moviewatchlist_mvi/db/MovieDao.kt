package com.mayada1994.moviewatchlist_mvi.db

import androidx.room.*
import com.mayada1994.moviewatchlist_mvi.entities.Movie
import io.reactivex.Single

@Dao
interface MovieDao {

    @Query("SELECT * FROM movies")
    fun getMovies(): Single<List<Movie>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMovie(movie: Movie): Single<Long>

    @Delete
    fun deleteMovies(movies: List<Movie>): Single<Unit>

}