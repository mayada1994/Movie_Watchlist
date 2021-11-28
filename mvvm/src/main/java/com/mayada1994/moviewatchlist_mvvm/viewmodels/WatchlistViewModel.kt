package com.mayada1994.moviewatchlist_mvvm.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.mayada1994.moviewatchlist_mvvm.R
import com.mayada1994.moviewatchlist_mvvm.entities.Movie
import com.mayada1994.moviewatchlist_mvvm.repositories.MoviesRepository
import com.mayada1994.moviewatchlist_mvvm.utils.SingleLiveEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class WatchlistViewModel(private val moviesRepository: MoviesRepository): ViewModel() {

    private val _moviesList = SingleLiveEvent<List<Movie>>()
    val moviesList: LiveData<List<Movie>>
        get() = _moviesList

    private val _floatingActionButtonImage = SingleLiveEvent<Int>()
    val floatingActionButtonImage: LiveData<Int>
        get() = _floatingActionButtonImage

    private val _navigateToSearchScreen = SingleLiveEvent<Boolean>()
    val navigateToSearchScreen: LiveData<Boolean>
        get() = _navigateToSearchScreen

    private val _showDeleteMoviesDialog = SingleLiveEvent<Boolean>()
    val showDeleteMoviesDialog: LiveData<Boolean>
        get() = _showDeleteMoviesDialog

    private val _updateMoviesList = SingleLiveEvent<List<Movie>>()
    val updateMoviesList: LiveData<List<Movie>>
        get() = _updateMoviesList

    private val _isProgressVisible = SingleLiveEvent<Boolean>()
    val isProgressVisible: LiveData<Boolean>
        get() = _isProgressVisible

    private val _isPlaceholderVisible = SingleLiveEvent<Boolean>()
    val isPlaceholderVisible: LiveData<Boolean>
        get() = _isPlaceholderVisible

    private val _toastMessageStringResId = SingleLiveEvent<Int>()
    val toastMessageStringResId: LiveData<Int>
        get() = _toastMessageStringResId

    private val compositeDisposable = CompositeDisposable()

    private val selectedMovies: ArrayList<Movie> = arrayListOf()

    fun init() {
        _isProgressVisible.postValue(true)
        compositeDisposable.add(
            moviesRepository.getMovies()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally { _isProgressVisible.postValue(false) }
                .subscribeWith(object : DisposableSingleObserver<List<Movie>>() {
                    override fun onSuccess(movies: List<Movie>) {
                        if (movies.isNotEmpty()) {
                            _moviesList.postValue(movies)
                            _isPlaceholderVisible.postValue(false)
                        } else {
                            _isPlaceholderVisible.postValue(true)
                        }
                        _floatingActionButtonImage.postValue(
                            if (selectedMovies.isEmpty()) {
                                android.R.drawable.ic_input_add
                            } else {
                                android.R.drawable.ic_delete
                            }
                        )
                    }

                    override fun onError(e: Throwable) {
                        Timber.e(e)
                        _isPlaceholderVisible.postValue(true)
                        _floatingActionButtonImage.postValue(
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

    fun onMovieItemChecked(movie: Movie, checked: Boolean) {
        if (checked) {
            selectedMovies.add(movie)
        } else {
            selectedMovies.remove(movie)
        }
        _floatingActionButtonImage.postValue(
            if (selectedMovies.isEmpty()) {
                android.R.drawable.ic_input_add
            } else {
                android.R.drawable.ic_delete
            }
        )
    }

    fun checkMoviesList(movies: List<Movie>) {
        _isPlaceholderVisible.postValue(movies.isEmpty())
    }

    fun onFloatingActionButtonClick() {
        if (selectedMovies.isEmpty()) {
            _navigateToSearchScreen.postValue(true)
        } else {
            _showDeleteMoviesDialog.postValue(true)
        }
    }

    fun deleteMovies() {
        _isProgressVisible.postValue(true)
        compositeDisposable.add(moviesRepository.deleteMovies(selectedMovies.toList())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally { _isProgressVisible.postValue(false) }
            .subscribeWith(object : DisposableCompletableObserver() {
                override fun onComplete() {
                    if (selectedMovies.size == 1) {
                        _toastMessageStringResId.postValue(R.string.movie_deleted_message)
                    } else if (selectedMovies.size > 1) {
                        _toastMessageStringResId.postValue(R.string.movies_deleted_message)
                    }
                    _updateMoviesList.postValue(selectedMovies.toList())
                    selectedMovies.clear()
                    _floatingActionButtonImage.postValue(android.R.drawable.ic_input_add)
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