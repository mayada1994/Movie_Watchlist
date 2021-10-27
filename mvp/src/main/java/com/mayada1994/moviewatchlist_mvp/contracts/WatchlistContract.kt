package com.mayada1994.moviewatchlist_mvp.contracts

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.mayada1994.moviewatchlist_mvp.entities.Movie

class WatchlistContract {

    interface PresenterInterface {
        fun init()
        fun onMovieItemChecked(movie: Movie, checked: Boolean)
        fun checkMoviesList(movies: List<Movie>)
        fun onFloatingActionButtonClick()
        fun deleteMovies()
        fun onDestroy()
    }

    interface ViewInterface {
        fun setMoviesList(movies: List<Movie>)
        fun updateMovies(selectedMovies: List<Movie>)
        fun showDeleteMoviesDialog()
        fun goToSearchScreen()
        fun showPlaceholder(isVisible: Boolean)
        fun showProgress(isProgressVisible: Boolean)
        fun showToast(@StringRes resId: Int)
        fun setFloatingActionButtonImage(@DrawableRes resId: Int)
    }

}