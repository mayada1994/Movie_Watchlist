package com.mayada1994.moviewatchlist_hybrid.events

import androidx.annotation.DrawableRes
import com.mayada1994.moviewatchlist_hybrid.entities.Movie

sealed class WatchlistEvent {
    data class SetMoviesList(val movies: List<Movie>) : ViewEvent

    data class SetFloatingActionButtonImage(@DrawableRes val resId: Int) : ViewEvent

    object GoToSearchScreen : ViewEvent

    object ShowDeleteMoviesDialog : ViewEvent

    data class UpdateMovies(val movies: List<Movie>) : ViewEvent
}