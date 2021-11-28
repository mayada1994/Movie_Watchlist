package com.mayada1994.moviewatchlist_mvp.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mayada1994.moviewatchlist_mvp.entities.Movie

@Database(entities = [Movie::class], version = 1, exportSchema = false)
abstract class WatchlistDatabase : RoomDatabase() {

    abstract fun movieDao(): MovieDao

}