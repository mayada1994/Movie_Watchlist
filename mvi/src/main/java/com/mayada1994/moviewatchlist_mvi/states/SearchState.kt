package com.mayada1994.moviewatchlist_mvi.states

import androidx.annotation.StringRes
import com.mayada1994.moviewatchlist_mvi.entities.Movie

sealed class SearchState {
    data class DataState(val movies: List<Movie>) : SearchState()

    object LoadingState : SearchState()

    object EmptyState : SearchState()

    data class CompletedState(@StringRes val resId: Int) : SearchState()

    data class ErrorState(@StringRes val resId: Int) : SearchState()
}
