package com.mayada1994.moviewatchlist_mvi.interactors

import com.mayada1994.moviewatchlist_mvi.R
import com.mayada1994.moviewatchlist_mvi.entities.Movie
import com.mayada1994.moviewatchlist_mvi.repositories.MoviesRepository
import com.mayada1994.moviewatchlist_mvi.states.SearchState
import io.reactivex.Observable

class SearchInteractor(private val moviesRepository: MoviesRepository) {

    fun searchMovie(query: String): Observable<SearchState> {
        return moviesRepository.searchMovie(query).toObservable()
            .map {
                if (it.results.isNotEmpty()) {
                    SearchState.DataState(it.results)
                } else {
                    SearchState.EmptyState
                }
            }
            .onErrorReturn {
                SearchState.ErrorState(R.string.general_error_message)
            }
    }

    fun addMovieToWatchlist(movie: Movie): Observable<SearchState> {
        return moviesRepository.insertMovie(movie)
            .map<SearchState> { SearchState.CompletedState(R.string.movie_added_to_watchlist_message) }
            .onErrorReturn { SearchState.CompletedState(R.string.general_error_message) }
            .toObservable()
    }

}