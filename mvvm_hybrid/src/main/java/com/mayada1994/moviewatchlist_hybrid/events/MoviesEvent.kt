package com.mayada1994.moviewatchlist_hybrid.events

import com.mayada1994.moviewatchlist_hybrid.entities.Movie

sealed class MoviesEvent {
    data class SetMoviesList(val movies: List<Movie>) : ViewEvent
}