package com.mayada1994.moviewatchlist.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mayada1994.moviewatchlist.entities.Movie

@Database(entities = [Movie::class], version = 1)
abstract class WatchlistDatabase : RoomDatabase() {

    abstract fun movieDao(): MovieDao

}