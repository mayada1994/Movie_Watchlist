package com.mayada1994.moviewatchlist_hybrid.events

import com.mayada1994.moviewatchlist_hybrid.entities.Movie

sealed class SearchEvent {
    data class SetMoviesList(val movies: List<Movie>) : ViewEvent

    data class ShowEmptySearchResult(val isVisible: Boolean) : ViewEvent

    object ClearMovieList : ViewEvent
}