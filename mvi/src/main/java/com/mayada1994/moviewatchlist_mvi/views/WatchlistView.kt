package com.mayada1994.moviewatchlist_mvi.views

import com.mayada1994.moviewatchlist_mvi.entities.Movie
import com.mayada1994.moviewatchlist_mvi.states.WatchlistState
import io.reactivex.Observable

interface WatchlistView {
    fun render(state: WatchlistState)
    fun displayMoviesIntent(): Observable<Unit>
    fun deleteMoviesFromWatchlistIntent(): Observable<Unit>
    fun floatingActionButtonClickIntent(): Observable<Unit>
    fun selectMovieIntent(): Observable<Pair<Movie, Boolean>>
    fun checkMoviesListIntent(): Observable<List<Movie>>
}