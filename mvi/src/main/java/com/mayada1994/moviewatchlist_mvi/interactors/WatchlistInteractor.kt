package com.mayada1994.moviewatchlist_mvi.interactors

import com.mayada1994.moviewatchlist_mvi.R
import com.mayada1994.moviewatchlist_mvi.entities.Movie
import com.mayada1994.moviewatchlist_mvi.repositories.MoviesRepository
import com.mayada1994.moviewatchlist_mvi.states.WatchlistState
import io.reactivex.Observable

class WatchlistInteractor(private val moviesRepository: MoviesRepository) {

    fun getMovies(): Observable<WatchlistState> {
        return moviesRepository.getMovies().toObservable()
            .map {
                if (it.isNotEmpty()) {
                    WatchlistState.DataState(it)
                } else {
                    WatchlistState.EmptyState
                }
            }
            .onErrorReturn {
                WatchlistState.ErrorState(R.string.general_error_message)
            }
    }

    fun deleteMoviesFromWatchlist(movies: List<Movie>): Observable<WatchlistState> {
        return moviesRepository.deleteMovies(movies)
            .map<WatchlistState> {
                WatchlistState.UpdateDataState(
                    movies,
                    if (movies.size > 1) {
                        R.string.movies_deleted_message
                    } else {
                        R.string.movie_deleted_message
                    }
                )
            }
            .onErrorReturn { WatchlistState.ErrorState(R.string.general_error_message) }
            .toObservable()
    }

    fun onFloatingActionButtonClick(selectedMovies: List<Movie>): Observable<WatchlistState> {
        return Observable.just(
            if (selectedMovies.isEmpty()) {
                WatchlistState.NavigateToSearchScreenState
            } else {
                WatchlistState.ShowDeleteMoviesDialogState
            }
        )
    }

    fun onMovieSelected(selectedMovies: List<Movie>): Observable<WatchlistState> {
        return Observable.just(
            WatchlistState.FloatingActionButtonImageState(
                if (selectedMovies.isEmpty()) {
                    android.R.drawable.ic_input_add
                } else {
                    android.R.drawable.ic_delete
                }
            )
        )
    }

    fun onCheckMoviesList(selectedMovies: List<Movie>): Observable<WatchlistState> {
        return Observable.just(
            if (selectedMovies.isEmpty()) {
                WatchlistState.EmptyState
            } else {
                WatchlistState.FloatingActionButtonImageState(android.R.drawable.ic_input_add)
            }
        )
    }

}