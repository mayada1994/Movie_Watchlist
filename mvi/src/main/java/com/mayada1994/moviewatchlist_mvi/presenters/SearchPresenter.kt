package com.mayada1994.moviewatchlist_mvi.presenters

import com.mayada1994.moviewatchlist_mvi.interactors.SearchInteractor
import com.mayada1994.moviewatchlist_mvi.states.SearchState
import com.mayada1994.moviewatchlist_mvi.views.SearchMovieView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class SearchPresenter(private val searchInteractor: SearchInteractor) {
    private val compositeDisposable = CompositeDisposable()
    private lateinit var movieView: SearchMovieView

    fun bind(movieView: SearchMovieView) {
        this.movieView = movieView
        compositeDisposable.add(observeSearchMovieIntent())
        compositeDisposable.add(observeAddMovieToWatchlistIntent())
    }

    fun unbind() {
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.dispose()
        }
    }

    private fun observeAddMovieToWatchlistIntent() = movieView.addMovieToWatchlistIntent()
        .doOnNext { Timber.d("Intent: add movie ${it.getMovieTitle()}") }
        .observeOn(Schedulers.io())
        .flatMap { searchInteractor.addMovieToWatchlist(it) }
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { movieView.render(it) }

    private fun observeSearchMovieIntent() = movieView.searchMovieIntent()
        .doOnNext { Timber.d("Intent: search for movie that matches query: $it") }
        .observeOn(Schedulers.io())
        .doOnSubscribe { SearchState.LoadingState }
        .flatMap { searchInteractor.searchMovie(it) }
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { movieView.render(it) }
}