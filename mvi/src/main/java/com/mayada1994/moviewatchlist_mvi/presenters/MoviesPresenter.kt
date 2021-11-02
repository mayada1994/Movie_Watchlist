package com.mayada1994.moviewatchlist_mvi.presenters

import com.mayada1994.moviewatchlist_mvi.interactors.MoviesInteractor
import com.mayada1994.moviewatchlist_mvi.states.MoviesState
import com.mayada1994.moviewatchlist_mvi.views.MoviesView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class MoviesPresenter(private val moviesInteractor: MoviesInteractor) {
    private val compositeDisposable = CompositeDisposable()
    private lateinit var view: MoviesView

    fun bind(view: MoviesView) {
        this.view = view
        compositeDisposable.add(observeDisplayMoviesIntent())
        compositeDisposable.add(observeAddMovieToWatchlistIntent())
    }

    fun unbind() {
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.dispose()
        }
    }
    private fun observeAddMovieToWatchlistIntent() = view.addMovieToWatchlistIntent()
        .doOnNext { Timber.d("Intent: add movie ${it.getMovieTitle()}") }
        .observeOn(Schedulers.io())
        .flatMap { moviesInteractor.addMovieToWatchlist(it) }
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { view.render(it) }

    private fun observeDisplayMoviesIntent() = view.displayMoviesIntent()
        .doOnNext { Timber.d("Intent: display movies of type $it") }
        .flatMap { moviesInteractor.getMovies(it) }
        .startWith(MoviesState.LoadingState)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { view.render(it) }
}