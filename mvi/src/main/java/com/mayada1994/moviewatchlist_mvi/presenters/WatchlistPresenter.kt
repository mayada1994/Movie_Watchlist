package com.mayada1994.moviewatchlist_mvi.presenters

import com.mayada1994.moviewatchlist_mvi.entities.Movie
import com.mayada1994.moviewatchlist_mvi.interactors.WatchlistInteractor
import com.mayada1994.moviewatchlist_mvi.states.WatchlistState
import com.mayada1994.moviewatchlist_mvi.views.WatchlistView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class WatchlistPresenter(private val watchlistInteractor: WatchlistInteractor) {
    private val compositeDisposable = CompositeDisposable()
    private lateinit var view: WatchlistView

    private val selectedMovies: ArrayList<Movie> = arrayListOf()

    fun bind(view: WatchlistView) {
        this.view = view
        compositeDisposable.add(observeDisplayMoviesIntent())
        compositeDisposable.add(observeDeleteMoviesFromWatchlistIntent())
        compositeDisposable.add(observeFloatingActionButtonClickIntent())
        compositeDisposable.add(observeSelectMovieIntent())
        compositeDisposable.add(observeCheckMoviesListIntent())
    }

    fun unbind() {
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.dispose()
        }
    }

    private fun observeDeleteMoviesFromWatchlistIntent() = view.deleteMoviesFromWatchlistIntent()
        .doOnNext { Timber.d("Intent: delete ${selectedMovies.size} movie(s)") }
        .observeOn(Schedulers.io())
        .flatMap { watchlistInteractor.deleteMoviesFromWatchlist(selectedMovies) }
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { view.render(it) }

    private fun observeDisplayMoviesIntent() = view.displayMoviesIntent()
        .doOnNext { Timber.d("Intent: display movies from DB") }
        .flatMap { watchlistInteractor.getMovies() }
        .startWith(WatchlistState.LoadingState)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { view.render(it) }

    private fun observeFloatingActionButtonClickIntent() = view.floatingActionButtonClickIntent()
        .doOnNext { Timber.d("Intent: handle fab click ${selectedMovies.map { it.getMovieTitle() }}") }
        .observeOn(Schedulers.io())
        .flatMap { watchlistInteractor.onFloatingActionButtonClick(selectedMovies) }
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { view.render(it) }

    private fun observeSelectMovieIntent() = view.selectMovieIntent()
        .doOnNext {
            Timber.d("Intent: select movie ${it.first.getMovieTitle()} = ${it.second}")
            if (it.second) {
                selectedMovies.add(it.first)
            } else {
                selectedMovies.remove(it.first)
            }
        }
        .observeOn(Schedulers.io())
        .flatMap { watchlistInteractor.onMovieSelected(selectedMovies) }
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { view.render(it) }

    private fun observeCheckMoviesListIntent() = view.checkMoviesListIntent()
        .doOnNext {
            selectedMovies.clear()
            Timber.d("Intent: check movies list state")
        }
        .observeOn(Schedulers.io())
        .flatMap { watchlistInteractor.onCheckMoviesList(it) }
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { view.render(it) }

}