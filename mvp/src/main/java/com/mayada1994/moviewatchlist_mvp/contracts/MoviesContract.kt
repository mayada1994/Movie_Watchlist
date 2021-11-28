package com.mayada1994.moviewatchlist_mvp.contracts

import androidx.annotation.StringRes
import com.mayada1994.moviewatchlist_mvp.entities.Movie
import com.mayada1994.moviewatchlist_mvp.fragments.MoviesFragment.MovieType

class MoviesContract {

    interface PresenterInterface {
        fun init(movieType: MovieType)
        fun addMovieToWatchlist(movie: Movie)
        fun onDestroy()
    }

    interface ViewInterface {
        fun setMoviesList(movies: List<Movie>)
        fun showPlaceholder(isVisible: Boolean)
        fun showProgress(isProgressVisible: Boolean)
        fun showToast(@StringRes resId: Int)
    }

}