package com.mayada1994.moviewatchlist_mvi.states

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.mayada1994.moviewatchlist_mvi.entities.Movie

sealed class WatchlistState {
    data class DataState(val movies: List<Movie>) : WatchlistState()

    data class UpdateDataState(val movies: List<Movie>, @StringRes val resId: Int) : WatchlistState()

    object LoadingState : WatchlistState()

    object EmptyState : WatchlistState()

    data class ErrorState(@StringRes val resId: Int) : WatchlistState()

    object NavigateToSearchScreenState : WatchlistState()

    object ShowDeleteMoviesDialogState : WatchlistState()

    data class FloatingActionButtonImageState(@DrawableRes val resId: Int) : WatchlistState()
}
