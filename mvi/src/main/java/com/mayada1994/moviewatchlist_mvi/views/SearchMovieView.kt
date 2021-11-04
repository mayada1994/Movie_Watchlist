package com.mayada1994.moviewatchlist_mvi.views

import com.mayada1994.moviewatchlist_mvi.entities.Movie
import com.mayada1994.moviewatchlist_mvi.states.SearchState
import io.reactivex.Observable

interface SearchMovieView {
    fun render(state: SearchState)
    fun searchMovieIntent(): Observable<String>
    fun addMovieToWatchlistIntent(): Observable<Movie>
}