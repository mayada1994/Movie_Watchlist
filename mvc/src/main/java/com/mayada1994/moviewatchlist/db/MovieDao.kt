package com.mayada1994.moviewatchlist.db

import androidx.room.*
import com.mayada1994.moviewatchlist.entities.Movie
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface MovieDao {

    @Query("SELECT * FROM movies")
    fun getMovies(): Single<List<Movie>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMovie(movie: Movie): Completable

    @Query("DELETE FROM movies WHERE id = :id")
    fun deleteMovie(id: Int?): Completable

}