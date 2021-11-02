package com.mayada1994.moviewatchlist_mvi.views

import com.mayada1994.moviewatchlist_mvi.entities.Movie
import com.mayada1994.moviewatchlist_mvi.fragments.MoviesFragment.MovieType
import com.mayada1994.moviewatchlist_mvi.states.MoviesState
import io.reactivex.Observable

interface MoviesView {
    fun render(state: MoviesState)
    fun displayMoviesIntent(): Observable<MovieType>
    fun addMovieToWatchlistIntent(): Observable<Movie>
}