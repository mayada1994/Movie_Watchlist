package com.mayada1994.moviewatchlist_mvvm.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.mayada1994.moviewatchlist_mvvm.R
import com.mayada1994.moviewatchlist_mvvm.entities.Movie
import com.mayada1994.moviewatchlist_mvvm.entities.TmbdResponse
import com.mayada1994.moviewatchlist_mvvm.repositories.MoviesRepository
import com.mayada1994.moviewatchlist_mvvm.utils.ViewEvent
import com.mayada1994.rules.RxImmediateSchedulerRule
import io.mockk.*
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class SearchViewModelTest {

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private val observerViewEvent: Observer<ViewEvent> = mockk()

    private val moviesRepository: MoviesRepository = mockk()

    private lateinit var searchViewModel: SearchViewModel

    @Before
    fun setup() {
        searchViewModel = SearchViewModel(moviesRepository)
        searchViewModel.event.observeForever(observerViewEvent)
        every { observerViewEvent.onChanged(any()) } just Runs
    }

    @After
    fun clear() {
        unmockkAll()
    }

    /**
     * Given:
     * - searchMovie in moviesRepository returns some TmbdResponse with movies as results
     * When:
     * - searchMovie is called with some query
     * Then should:
     * - call setEvent with SetMoviesList in searchViewModel with list of movies from given TmbdResponse
     * - call setEvent with ShowEmptySearchResult in searchViewModel with false as isVisible
     */
    @Test
    fun check_searchMovie() {
        //Given
        val query = "Harry"

        val movies = listOf(
            Movie(title = "Harry Potter and the Goblet of Fire"),
            Movie(title = "Harry Potter and the Chamber of Secrets")
        )

        val tmbdResponse = TmbdResponse(results = movies)

        every { moviesRepository.searchMovie(query, 1) } returns Single.just(tmbdResponse)

        //When
        searchViewModel.searchMovie(query)

        //Then
        verifyOrder {
            observerViewEvent.onChanged(BaseViewModel.BaseEvent.ShowProgress(true))
            moviesRepository.searchMovie(query, 1)
            observerViewEvent.onChanged(SearchViewModel.SearchEvent.SetMoviesList(movies))
            observerViewEvent.onChanged(SearchViewModel.SearchEvent.ShowEmptySearchResult(false))
            observerViewEvent.onChanged(BaseViewModel.BaseEvent.ShowProgress(false))
        }
    }

    /**
     * Given:
     * - searchMovie in moviesRepository returns some TmbdResponse with empty list as results
     * When:
     * - searchMovie is called with some query
     * Then should:
     * - not call setEvent with SetMoviesList in searchViewModel with list of movies from given TmbdResponse
     * - call setEvent with ShowEmptySearchResult in searchViewModel with true as isVisible
     */
    @Test
    fun check_searchMovie_emptyMoviesList() {
        //Given
        val query = "Titanic"

        val movies = emptyList<Movie>()

        val tmbdResponse = TmbdResponse(results = movies)

        every { moviesRepository.searchMovie(query, 1) } returns Single.just(tmbdResponse)

        //When
        searchViewModel.searchMovie(query)

        //Then
        verifyOrder {
            observerViewEvent.onChanged(BaseViewModel.BaseEvent.ShowProgress(true))
            moviesRepository.searchMovie(query, 1)
            observerViewEvent.onChanged(SearchViewModel.SearchEvent.ShowEmptySearchResult(true))
            observerViewEvent.onChanged(SearchViewModel.SearchEvent.ClearMovieList)
            observerViewEvent.onChanged(BaseViewModel.BaseEvent.ShowProgress(false))
        }

        verify(exactly = 0) { observerViewEvent.onChanged(SearchViewModel.SearchEvent.SetMoviesList(movies)) }
    }

    /**
     * Given:
     * - searchMovie in moviesRepository throws exception
     * When:
     * - searchMovie is called with some query
     * Then should:
     * - not call setEvent with SetMoviesList in searchViewModel
     * - call setEvent with ShowMessage in searchViewModel with R.string.general_error_message as resId
     * - call setEvent with ShowPlaceholder in searchViewModel with true as isVisible
     */
    @Test
    fun check_searchMovie_error() {
        //Given
        val query = "Titanic"

        val testException = Exception()

        every { moviesRepository.searchMovie(query, 1) } returns Single.error(testException)

        //When
        searchViewModel.searchMovie(query)

        //Then
        verifyOrder {
            observerViewEvent.onChanged(BaseViewModel.BaseEvent.ShowProgress(true))
            moviesRepository.searchMovie(query, 1)
            observerViewEvent.onChanged(BaseViewModel.BaseEvent.ShowMessage(R.string.general_error_message))
            observerViewEvent.onChanged(BaseViewModel.BaseEvent.ShowPlaceholder(true))
            observerViewEvent.onChanged(BaseViewModel.BaseEvent.ShowProgress(false))
        }
    }

    /**
     * When:
     * - addMovieToWatchlist is called with some movie
     * Then should:
     * - call setEvent with ShowMessage in searchViewModel with R.string.movie_added_to_watchlist_message as resId
     */
    @Test
    fun check_addMovieToWatchlist() {
        //Given
        val movie = Movie(title = "The Mummy")

        every { moviesRepository.insertMovie(movie) } returns Completable.complete()

        //When
        searchViewModel.addMovieToWatchlist(movie)

        //Then
        verifyOrder {
            observerViewEvent.onChanged(BaseViewModel.BaseEvent.ShowProgress(true))
            moviesRepository.insertMovie(movie)
            observerViewEvent.onChanged(BaseViewModel.BaseEvent.ShowMessage(R.string.movie_added_to_watchlist_message))
            observerViewEvent.onChanged(BaseViewModel.BaseEvent.ShowProgress(false))
        }
    }

    /**
     * Given:
     * - insertMovie in moviesRepository throws exception
     * When:
     * - addMovieToWatchlist is called with some movie
     * Then should:
     * - call setEvent with ShowMessage in searchViewModel with R.string.general_error_message as resId
     */
    @Test
    fun check_addMovieToWatchlist_error() {
        //Given
        val movie = Movie(title = "The Mummy")

        val testException = Exception()

        every { moviesRepository.insertMovie(movie) } returns Completable.error(testException)

        //When
        searchViewModel.addMovieToWatchlist(movie)

        //Then
        verifyOrder {
            observerViewEvent.onChanged(BaseViewModel.BaseEvent.ShowProgress(true))
            moviesRepository.insertMovie(movie)
            observerViewEvent.onChanged(BaseViewModel.BaseEvent.ShowMessage(R.string.general_error_message))
            observerViewEvent.onChanged(BaseViewModel.BaseEvent.ShowProgress(false))
        }
    }

}