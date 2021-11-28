package com.mayada1994.moviewatchlist_mvp.presenters

import com.mayada1994.moviewatchlist_mvp.R
import com.mayada1994.moviewatchlist_mvp.contracts.SearchContract
import com.mayada1994.moviewatchlist_mvp.entities.Movie
import com.mayada1994.moviewatchlist_mvp.entities.TmbdResponse
import com.mayada1994.moviewatchlist_mvp.models.LocalDataSource
import com.mayada1994.moviewatchlist_mvp.models.RemoteDataSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class SearchPresenter(
    private val viewInterface: SearchContract.ViewInterface,
    private val localDataSource: LocalDataSource,
    private val remoteDataSource: RemoteDataSource
): SearchContract.PresenterInterface {

    private val compositeDisposable = CompositeDisposable()

    override fun searchMovie(query: String) {
        viewInterface.showPlaceholder(false)
        viewInterface.showProgress(true)
        compositeDisposable.add(
            remoteDataSource.searchMovie(query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally { viewInterface.showProgress(false) }
                .subscribeWith(object : DisposableSingleObserver<TmbdResponse>() {
                    override fun onSuccess(response: TmbdResponse) {
                        response.results.let { movies ->
                            if (movies.isNotEmpty()) {
                                viewInterface.setMoviesList(movies)
                                viewInterface.showEmptySearchResult(false)
                            } else {
                                viewInterface.showEmptySearchResult(true)
                                viewInterface.clearMovieList()
                            }
                        }
                    }

                    override fun onError(e: Throwable) {
                        viewInterface.showToast(R.string.general_error_message)
                        viewInterface.showPlaceholder(true)
                        Timber.e(e)
                    }
                })
        )
    }

    override fun addMovieToWatchlist(movie: Movie) {
        viewInterface.showProgress(true)
        compositeDisposable.add(
            localDataSource.insertMovie(movie)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally { viewInterface.showProgress(false) }
                .subscribeWith(object : DisposableCompletableObserver() {
                    override fun onComplete() {
                        viewInterface.showToast(R.string.movie_added_to_watchlist_message)
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