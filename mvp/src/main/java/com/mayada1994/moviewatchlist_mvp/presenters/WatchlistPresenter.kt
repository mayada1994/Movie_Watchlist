package com.mayada1994.moviewatchlist_mvp.presenters

import com.mayada1994.moviewatchlist_mvp.R
import com.mayada1994.moviewatchlist_mvp.contracts.WatchlistContract
import com.mayada1994.moviewatchlist_mvp.entities.Movie
import com.mayada1994.moviewatchlist_mvp.models.LocalDataSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class WatchlistPresenter(
    private val viewInterface: WatchlistContract.ViewInterface,
    private val localDataSource: LocalDataSource
): WatchlistContract.PresenterInterface {

    private val compositeDisposable = CompositeDisposable()

    private val selectedMovies: ArrayList<Movie> = arrayListOf()

    override fun init() {
        viewInterface.showProgress(true)
        compositeDisposable.add(
            localDataSource.getMovies()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally { viewInterface.showProgress(false) }
                .subscribeWith(object : DisposableSingleObserver<List<Movie>>() {
                    override fun onSuccess(movies: List<Movie>) {
                        if (movies.isNotEmpty()) {
                            viewInterface.setMoviesList(movies)
                            viewInterface.showPlaceholder(false)
                        } else {
                            viewInterface.showPlaceholder(true)
                        }
                        viewInterface.setFloatingActionButtonImage(
                            if (selectedMovies.isEmpty()) {
                                android.R.drawable.ic_input_add
                            } else {
                                android.R.drawable.ic_delete
                            }
                        )
                    }

                    override fun onError(e: Throwable) {
                        Timber.e(e)
                        viewInterface.showPlaceholder(true)
                        viewInterface.setFloatingActionButtonImage(
                            if (selectedMovies.isEmpty()) {
                                android.R.drawable.ic_input_add
                            } else {
                                android.R.drawable.ic_delete
                            }
                        )
                    }

                })
        )
    }

    override fun onMovieItemChecked(movie: Movie, checked: Boolean) {
        if (checked) {
            selectedMovies.add(movie)
        } else {
            selectedMovies.remove(movie)
        }
        viewInterface.setFloatingActionButtonImage(
            if (selectedMovies.isEmpty()) {
                android.R.drawable.ic_input_add
            } else {
                android.R.drawable.ic_delete
            }
        )
    }

    override fun checkMoviesList(movies: List<Movie>) {
        viewInterface.showPlaceholder(movies.isEmpty())
    }

    override fun onFloatingActionButtonClick() {
        if (selectedMovies.isEmpty()) {
            viewInterface.goToSearchScreen()
        } else {
            viewInterface.showDeleteMoviesDialog()
        }
    }

    override fun deleteMovies() {
        viewInterface.showProgress(true)
        compositeDisposable.add(localDataSource.deleteMovies(selectedMovies.toList())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally { viewInterface.showProgress(false) }
            .subscribeWith(object : DisposableCompletableObserver() {
                override fun onComplete() {
                    if (selectedMovies.size == 1) {
                        viewInterface.showToast(R.string.movie_deleted_message)
                    } else if (selectedMovies.size > 1) {
                        viewInterface.showToast(R.string.movies_deleted_message)
                    }
                    viewInterface.updateMovies(selectedMovies.toList())
                    selectedMovies.clear()
                    viewInterface.setFloatingActionButtonImage(android.R.drawable.ic_input_add)
                }

                override fun onError(e: Throwable) {
                    viewInterface.showToast(R.string.general_error_message)
                    Timber.e(e)
                }
            })
        )
    }

    override fun onDestroy() {
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.clear()
        }
    }

}