package com.mayada1994.moviewatchlist_mvvm.viewmodels

import androidx.annotation.DrawableRes
import com.mayada1994.moviewatchlist_mvvm.R
import com.mayada1994.moviewatchlist_mvvm.entities.Movie
import com.mayada1994.moviewatchlist_mvvm.repositories.MoviesRepository
import com.mayada1994.moviewatchlist_mvvm.utils.ViewEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class WatchlistViewModel(private val moviesRepository: MoviesRepository): BaseViewModel() {

    sealed class WatchlistEvent {
        data class SetMoviesList(val movies: List<Movie>) : ViewEvent

        data class SetFloatingActionButtonImage(@DrawableRes val resId: Int) : ViewEvent

        object GoToSearchScreen : ViewEvent

        object ShowDeleteMoviesDialog : ViewEvent

        data class UpdateMovies(val movies: List<Movie>) : ViewEvent
    }

    private val compositeDisposable = CompositeDisposable()

    private val selectedMovies: ArrayList<Movie> = arrayListOf()

    fun init() {
        setEvent(BaseEvent.ShowProgress(true))
        compositeDisposable.add(
            moviesRepository.getMovies()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally { setEvent(BaseEvent.ShowProgress(false)) }
                .subscribeWith(object : DisposableSingleObserver<List<Movie>>() {
                    override fun onSuccess(movies: List<Movie>) {
                        if (movies.isNotEmpty()) {
                            setEvent(WatchlistEvent.SetMoviesList(movies))
                            setEvent(BaseEvent.ShowPlaceholder(false))
                        } else {
                            setEvent(BaseEvent.ShowPlaceholder(true))
                        }
                        setEvent(WatchlistEvent.SetFloatingActionButtonImage(
                                if (selectedMovies.isEmpty()) {
                                    android.R.drawable.ic_input_add
                                } else {
                                    android.R.drawable.ic_delete
                                }
                            )
                        )
                    }

                    override fun onError(e: Throwable) {
                        Timber.e(e)
                        setEvent(BaseEvent.ShowPlaceholder(true))
                        setEvent(WatchlistEvent.SetFloatingActionButtonImage(
                            if (selectedMovies.isEmpty()) {
                                android.R.drawable.ic_input_add
                            } else {
                                android.R.drawable.ic_delete
                            }
                        ))
                    }
                })
        )
    }

    fun onMovieItemChecked(movie: Movie, checked: Boolean) {
        if (checked) {
            selectedMovies.add(movie)
        } else {
            selectedMovies.remove(movie)
        }
        setEvent(WatchlistEvent.SetFloatingActionButtonImage(
            if (selectedMovies.isEmpty()) {
                android.R.drawable.ic_input_add
            } else {
                android.R.drawable.ic_delete
            }
        ))
    }

    fun checkMoviesList(movies: List<Movie>) {
        setEvent(BaseEvent.ShowPlaceholder(movies.isEmpty()))
    }

    fun onFloatingActionButtonClick() {
        if (selectedMovies.isEmpty()) {
            setEvent(WatchlistEvent.GoToSearchScreen)
        } else {
            setEvent(WatchlistEvent.ShowDeleteMoviesDialog)
        }
    }

    fun deleteMovies() {
        setEvent(BaseEvent.ShowProgress(true))
        compositeDisposable.add(moviesRepository.deleteMovies(selectedMovies.toList())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally { setEvent(BaseEvent.ShowProgress(false)) }
            .subscribeWith(object : DisposableCompletableObserver() {
                override fun onComplete() {
                    if (selectedMovies.size == 1) {
                        setEvent(BaseEvent.ShowMessage(R.string.movie_deleted_message))
                    } else if (selectedMovies.size > 1) {
                        setEvent(BaseEvent.ShowMessage(R.string.movies_deleted_message))
                    }
                    setEvent(WatchlistEvent.UpdateMovies(selectedMovies.toList()))
                    selectedMovies.clear()
                    setEvent(WatchlistEvent.SetFloatingActionButtonImage(android.R.drawable.ic_input_add))
                }

                override fun onError(e: Throwable) {
                    setEvent(BaseEvent.ShowMessage(R.string.general_error_message))
                    Timber.e(e)
                }
            })
        )
    }

    fun onDestroy() {
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.clear()
        }
    }

}