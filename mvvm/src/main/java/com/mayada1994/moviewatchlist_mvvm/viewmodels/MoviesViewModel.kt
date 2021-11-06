package com.mayada1994.moviewatchlist_mvvm.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.mayada1994.moviewatchlist_mvvm.R
import com.mayada1994.moviewatchlist_mvvm.entities.Movie
import com.mayada1994.moviewatchlist_mvvm.entities.TmbdResponse
import com.mayada1994.moviewatchlist_mvvm.fragments.MoviesFragment.MovieType
import com.mayada1994.moviewatchlist_mvvm.repositories.MoviesRepository
import com.mayada1994.moviewatchlist_mvvm.utils.SingleLiveEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class MoviesViewModel(private val moviesRepository: MoviesRepository) : ViewModel() {

    private val _moviesList = SingleLiveEvent<List<Movie>>()
    val moviesList: LiveData<List<Movie>>
        get() = _moviesList

    private val _isProgressVisible = SingleLiveEvent<Boolean>()
    val isProgressVisible: LiveData<Boolean>
        get() = _isProgressVisible

    private val _isPlaceholderVisible = SingleLiveEvent<Boolean>()
    val isPlaceholderVisible: LiveData<Boolean>
        get() = _isPlaceholderVisible

    private val _toastMessageStringResId = SingleLiveEvent<Int>()
    val toastMessageStringResId: LiveData<Int>
        get() = _toastMessageStringResId

    private var compositeDisposable = CompositeDisposable()

    fun init(movieType: MovieType) {
        when (movieType) {
            MovieType.POPULAR -> getPopularMovies()
            MovieType.UPCOMING -> getUpcomingMovies()
        }
    }

    private fun getPopularMovies() {
        _isProgressVisible.postValue(true)
        compositeDisposable.add(
            moviesRepository.getPopularMovies(1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally { _isProgressVisible.postValue(false) }
                .subscribeWith(object : DisposableSingleObserver<TmbdResponse>() {
                    override fun onSuccess(response: TmbdResponse) {
                        response.results.let { movies ->
                            if (movies.isNotEmpty()) {
                                _moviesList.postValue(movies)
                                _isPlaceholderVisible.postValue(false)
                            } else {
                                _isPlaceholderVisible.postValue(true)
                            }
                        }
                    }

                    override fun onError(e: Throwable) {
                        _isPlaceholderVisible.postValue(true)
                        _toastMessageStringResId.postValue(R.string.general_error_message)
                        Timber.e(e)
                    }
                })
        )
    }

    private fun getUpcomingMovies() {
        _isProgressVisible.postValue(true)
        compositeDisposable.add(
            moviesRepository.getUpcomingMovies(1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally { _isProgressVisible.postValue(false) }
                .subscribeWith(object : DisposableSingleObserver<TmbdResponse>() {
                    override fun onSuccess(response: TmbdResponse) {
                        response.results.let { movies ->
                            if (movies.isNotEmpty()) {
                                _moviesList.postValue(movies)
                                _isPlaceholderVisible.postValue(false)
                            } else {
                                _isPlaceholderVisible.postValue(true)
                            }
                        }
                    }

                    override fun onError(e: Throwable) {
                        _isPlaceholderVisible.postValue(true)
                        _toastMessageStringResId.postValue(R.string.general_error_message)
                        Timber.e(e)
                    }
                })
        )
    }

    fun addMovieToWatchlist(movie: Movie) {
        _isProgressVisible.postValue(true)
        compositeDisposable.add(
            moviesRepository.insertMovie(movie)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally { _isProgressVisible.postValue(false) }
                .subscribeWith(object : DisposableCompletableObserver() {
                    override fun onComplete() {
                        _toastMessageStringResId.postValue(R.string.movie_added_to_watchlist_message)
                    }

                    override fun onError(e: Throwable) {
                        _toastMessageStringResId.postValue(R.string.general_error_message)
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