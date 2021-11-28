package com.mayada1994.moviewatchlist_mvvm.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.mayada1994.moviewatchlist_mvvm.R
import com.mayada1994.moviewatchlist_mvvm.entities.Movie
import com.mayada1994.moviewatchlist_mvvm.entities.TmbdResponse
import com.mayada1994.moviewatchlist_mvvm.repositories.MoviesRepository
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

    private val observerMoviesList: Observer<List<Movie>> = mockk()
    private val observerIsProgressVisible: Observer<Boolean> = mockk()
    private val observerIsEmptySearchResultVisible: Observer<Boolean> = mockk()
    private val observerIsPlaceholderVisible: Observer<Boolean> = mockk()
    private val observerClearMoviesList: Observer<Boolean> = mockk()
    private val observerToastMessageStringResId: Observer<Int> = mockk()

    private val moviesRepository: MoviesRepository = mockk()

    private lateinit var searchViewModel: SearchViewModel

    @Before
    fun setup() {
        searchViewModel = SearchViewModel(moviesRepository)
        searchViewModel.moviesList.observeForever(observerMoviesList)
        searchViewModel.isProgressVisible.observeForever(observerIsProgressVisible)
        searchViewModel.isEmptySearchResultVisible.observeForever(observerIsEmptySearchResultVisible)
        searchViewModel.isPlaceholderVisible.observeForever(observerIsPlaceholderVisible)
        searchViewModel.clearMoviesList.observeForever(observerClearMoviesList)
        searchViewModel.toastMessageStringResId.observeForever(observerToastMessageStringResId)
        every { observerMoviesList.onChanged(any()) } just Runs
        every { observerIsProgressVisible.onChanged(any()) } just Runs
        every { observerIsEmptySearchResultVisible.onChanged(any()) } just Runs
        every { observerIsPlaceholderVisible.onChanged(any()) } just Runs
        every { observerClearMoviesList.onChanged(any()) } just Runs
        every { observerToastMessageStringResId.onChanged(any()) } just Runs
    }

    @After
    fun clear() {
        searchViewModel.onDestroy()
        unmockkAll()
    }

    /**
     * Given:
     * - searchMovie in moviesRepository returns some TmbdResponse with movies as results
     * When:
     * - searchMovie is called with some query
     * Then should:
     * - post moviesList in searchViewModel with list of movies from given TmbdResponse
     * - post isEmptySearchResult in searchViewModel with false as isVisible
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

        every { moviesRepository.searchMovie(query) } returns Single.just(tmbdResponse)

        //When
        searchViewModel.searchMovie(query)

        //Then
        verifyOrder {
            observerIsProgressVisible.onChanged(true)
            moviesRepository.searchMovie(query)
            observerMoviesList.onChanged(movies)
            observerIsEmptySearchResultVisible.onChanged(false)
            observerIsProgressVisible.onChanged(false)
        }
    }

    /**
     * Given:
     * - searchMovie in moviesRepository returns some TmbdResponse with empty list as results
     * When:
     * - searchMovie is called with some query
     * Then should:
     * - not post moviesList in searchViewModel with list of movies from given TmbdResponse
     * - post isEmptySearchResult in searchViewModel with true as isVisible
     */
    @Test
    fun check_searchMovie_emptyMoviesList() {
        //Given
        val query = "Titanic"

        val movies = emptyList<Movie>()

        val tmbdResponse = TmbdResponse(results = movies)

        every { moviesRepository.searchMovie(query) } returns Single.just(tmbdResponse)

        //When
        searchViewModel.searchMovie(query)

        //Then
        verifyOrder {
            observerIsProgressVisible.onChanged(true)
            moviesRepository.searchMovie(query)
            observerIsEmptySearchResultVisible.onChanged(true)
            observerClearMoviesList.onChanged(any())
            observerIsProgressVisible.onChanged(false)
        }

        verify(exactly = 0) { observerMoviesList.onChanged(movies) }
    }

    /**
     * Given:
     * - searchMovie in moviesRepository throws exception
     * When:
     * - searchMovie is called with some query
     * Then should:
     * - not post moviesList in searchViewModel
     * - post toastMessageStringResId in searchViewModel with R.string.general_error_message
     * - post isPlaceholderVisible in searchViewModel with true as isVisible
     */
    @Test
    fun check_searchMovie_error() {
        //Given
        val query = "Titanic"

        val testException = Exception()

        every { moviesRepository.searchMovie(query) } returns Single.error(testException)

        //When
        searchViewModel.searchMovie(query)

        //Then
        verifyOrder {
            observerIsProgressVisible.onChanged(true)
            moviesRepository.searchMovie(query)
            observerToastMessageStringResId.onChanged(R.string.general_error_message)
            observerIsPlaceholderVisible.onChanged(true)
            observerIsProgressVisible.onChanged(false)
        }
    }

    /**
     * When:
     * - addMovieToWatchlist is called with some movie
     * Then should:
     * - post toastMessageStringResId in searchViewModel with R.string.movie_added_to_watchlist_message
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
            observerIsProgressVisible.onChanged(true)
            moviesRepository.insertMovie(movie)
            observerToastMessageStringResId.onChanged(R.string.movie_added_to_watchlist_message)
            observerIsProgressVisible.onChanged(false)
        }
    }

    /**
     * Given:
     * - insertMovie in moviesRepository throws exception
     * When:
     * - addMovieToWatchlist is called with some movie
     * Then should:
     * - post toastMessageStringResId in searchViewModel with R.string.general_error_message
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
            observerIsProgressVisible.onChanged(true)
            moviesRepository.insertMovie(movie)
            observerToastMessageStringResId.onChanged(R.string.general_error_message)
            observerIsProgressVisible.onChanged(false)
        }
    }

}