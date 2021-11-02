package com.mayada1994.moviewatchlist_mvi.states

import androidx.annotation.StringRes
import com.mayada1994.moviewatchlist_mvi.entities.Movie

sealed class MoviesState {

    data class DataState(val movies: List<Movie>) : MoviesState()

    object LoadingState : MoviesState()

    object EmptyState : MoviesState()

    data class CompletedState(@StringRes val resId: Int) : MoviesState()

    data class ErrorState(@StringRes val resId: Int) : MoviesState()

}
