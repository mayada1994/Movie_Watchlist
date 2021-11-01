package com.mayada1994.moviewatchlist_mvvm.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mayada1994.moviewatchlist_mvvm.repositories.MoviesRepository

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(private val moviesRepository: MoviesRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T = when {
        modelClass.isAssignableFrom(MainViewModel::class.java) -> MainViewModel() as T
        modelClass.isAssignableFrom(MoviesViewModel::class.java) -> MoviesViewModel(
            moviesRepository
        ) as T
        modelClass.isAssignableFrom(SearchViewModel::class.java) -> SearchViewModel(
            moviesRepository
        ) as T
        modelClass.isAssignableFrom(WatchlistViewModel::class.java) -> WatchlistViewModel(
            moviesRepository
        ) as T
        else -> throw RuntimeException("Unable to create $modelClass")
    }

}