package com.mayada1994.moviewatchlist.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
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
    fun deleteMovie(id: Int): Completable

    @Query("DELETE FROM movies WHERE id IN (:ids)")
    fun deleteMovies(ids: List<Int>): Completable

}