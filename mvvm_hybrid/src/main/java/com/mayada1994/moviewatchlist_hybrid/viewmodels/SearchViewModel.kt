package com.mayada1994.moviewatchlist_hybrid.viewmodels

import com.mayada1994.moviewatchlist_hybrid.R
import com.mayada1994.moviewatchlist_hybrid.entities.Movie
import com.mayada1994.moviewatchlist_hybrid.entities.TmbdResponse
import com.mayada1994.moviewatchlist_hybrid.repositories.MoviesRepository
import com.mayada1994.moviewatchlist_hybrid.utils.ViewEvent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class SearchViewModel(private val moviesRepository: MoviesRepository) : BaseViewModel() {

    sealed class SearchEvent {
        data class SetMoviesList(val movies: List<Movie>) : ViewEvent

        data class ShowEmptySearchResult(val isVisible: Boolean) : ViewEvent

        object ClearMovieList : ViewEvent
    }

    private val compositeDisposable = CompositeDisposable()

    fun searchMovie(query: String) {
        setEvent(BaseEvent.ShowPlaceholder(false))
        setEvent(BaseEvent.ShowProgress(true))
        compositeDisposable.add(
            moviesRepository.searchMovie(query, 1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally { setEvent(BaseEvent.ShowProgress(false)) }
                .subscribeWith(object : DisposableSingleObserver<TmbdResponse>() {
                    override fun onSuccess(response: TmbdResponse) {
                        response.results.let { movies ->
                            if (movies.isNotEmpty()) {
                                setEvent(SearchEvent.SetMoviesList(movies))
                                setEvent(SearchEvent.ShowEmptySearchResult(false))
                            } else {
                                setEvent(SearchEvent.ShowEmptySearchResult(true))
                                setEvent(SearchEvent.ClearMovieList)
                            }
                        }
                    }

                    override fun onError(e: Throwable) {
                        setEvent(BaseEvent.ShowMessage(R.string.general_error_message))
                        setEvent(BaseEvent.ShowPlaceholder(true))
                        setEvent(SearchEvent.ClearMovieList)
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