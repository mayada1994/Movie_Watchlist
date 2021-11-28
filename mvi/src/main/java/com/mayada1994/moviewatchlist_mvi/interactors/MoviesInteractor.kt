package com.mayada1994.moviewatchlist_mvi.interactors

import com.mayada1994.moviewatchlist_mvi.R
import com.mayada1994.moviewatchlist_mvi.entities.Movie
import com.mayada1994.moviewatchlist_mvi.fragments.MoviesFragment.MovieType
import com.mayada1994.moviewatchlist_mvi.repositories.MoviesRepository
import com.mayada1994.moviewatchlist_mvi.states.MoviesState
import io.reactivex.Observable

class MoviesInteractor(private val moviesRepository: MoviesRepository) {

    fun getMovies(movieType: MovieType): Observable<MoviesState> {
        return when (movieType) {
            MovieType.POPULAR -> {
                moviesRepository.getPopularMovies().toObservable()
                    .map {
                        if (it.results.isNullOrEmpty()) {
                            MoviesState.EmptyState
                        } else {
                            MoviesState.DataState(it.results)
                        }
                    }
                    .onErrorReturn {
                        MoviesState.ErrorState(R.string.general_error_message)
                    }
            }

            MovieType.UPCOMING -> {
                moviesRepository.getUpcomingMovies().toObservable()
                    .map {
                        if (it.results.isNullOrEmpty()) {
                            MoviesState.EmptyState
                        } else {
                            MoviesState.DataState(it.results)
                        }
                    }
                    .onErrorReturn {
                        MoviesState.ErrorState(R.string.general_error_message)
                    }
            }
        }
    }

    fun addMovieToWatchlist(movie: Movie): Observable<MoviesState> {
        return moviesRepository.insertMovie(movie)
            .map<MoviesState> { MoviesState.CompletedState(R.string.movie_added_to_watchlist_message) }
            .onErrorReturn { MoviesState.ErrorState(R.string.general_error_message) }
            .toObservable()
    }

}