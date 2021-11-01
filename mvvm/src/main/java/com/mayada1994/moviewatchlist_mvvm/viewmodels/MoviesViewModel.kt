package com.mayada1994.moviewatchlist_mvvm.viewmodels

import com.mayada1994.moviewatchlist_mvvm.R
import com.mayada1994.moviewatchlist_mvvm.entities.Movie
import com.mayada1994.moviewatchlist_mvvm.entities.TmbdResponse
import com.mayada1994.moviewatchlist_mvvm.fragments.MoviesFragment.MovieType
import com.mayada1994.moviewatchlist_mvvm.repositories.MoviesRepository
import com.mayada1994.moviewatchlist_mvvm.utils.ViewEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class MoviesViewModel(private val moviesRepository: MoviesRepository) : BaseViewModel() {

    sealed class MoviesEvent {
        data class SetMoviesList(val movies: List<Movie>) : ViewEvent
    }

    private var compositeDisposable = CompositeDisposable()

    fun init(movieType: MovieType) {
        when (movieType) {
            MovieType.POPULAR -> getPopularMovies()
            MovieType.UPCOMING -> getUpcomingMovies()
        }
    }

    private fun getPopularMovies() {
        setEvent(BaseEvent.ShowProgress(true))
        compositeDisposable.add(
            moviesRepository.getPopularMovies(1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally { setEvent(BaseEvent.ShowProgress(false)) }
                .subscribeWith(object : DisposableSingleObserver<TmbdResponse>() {
                    override fun onSuccess(response: TmbdResponse) {
                        response.results.let { movies ->
                            if (movies.isNotEmpty()) {
                                setEvent(MoviesEvent.SetMoviesList(movies))
                                setEvent(BaseEvent.ShowPlaceholder(false))
                            } else {
                                setEvent(BaseEvent.ShowPlaceholder(true))
                            }
                        }
                    }

                    override fun onError(e: Throwable) {
                        setEvent(BaseEvent.ShowPlaceholder(true))
                        setEvent(BaseEvent.ShowMessage(R.string.general_error_message))
                        Timber.e(e)
                    }
                })
        )
    }

    private fun getUpcomingMovies() {
        setEvent(BaseEvent.ShowProgress(true))
        compositeDisposable.add(
            moviesRepository.getUpcomingMovies(1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally { setEvent(BaseEvent.ShowProgress(false)) }
                .subscribeWith(object : DisposableSingleObserver<TmbdResponse>() {
                    override fun onSuccess(response: TmbdResponse) {
                        response.results.let { movies ->
                            if (movies.isNotEmpty()) {
                                setEvent(MoviesEvent.SetMoviesList(movies))
                                setEvent(BaseEvent.ShowPlaceholder(false))
                            } else {
                                setEvent(BaseEvent.ShowPlaceholder(true))
                            }
                        }
                    }

                    override fun onError(e: Throwable) {
                        setEvent(BaseEvent.ShowPlaceholder(true))
                        setEvent(BaseEvent.ShowMessage(R.string.general_error_message))
                        Timber.e(e)
                    }
                })
        )
    }

    fun addMovieToWatchlist(movie: Movie) {
        setEvent(BaseEvent.ShowProgress(true))
        compositeDisposable.add(
            moviesRepository.insertMovie(movie)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally { setEvent(BaseEvent.ShowProgress(false)) }
                .subscribeWith(object : DisposableCompletableObserver() {
                    override fun onComplete() {
                        setEvent(BaseEvent.ShowMessage(R.string.movie_added_to_watchlist_message))
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