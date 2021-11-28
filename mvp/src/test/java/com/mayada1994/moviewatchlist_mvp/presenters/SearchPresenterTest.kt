package com.mayada1994.moviewatchlist_mvp.presenters

import com.mayada1994.moviewatchlist_mvp.R
import com.mayada1994.moviewatchlist_mvp.contracts.SearchContract
import com.mayada1994.moviewatchlist_mvp.entities.Movie
import com.mayada1994.moviewatchlist_mvp.entities.TmbdResponse
import com.mayada1994.moviewatchlist_mvp.models.LocalDataSource
import com.mayada1994.moviewatchlist_mvp.models.RemoteDataSource
import com.mayada1994.rules.RxImmediateSchedulerRule
import io.mockk.*
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SearchPresenterTest {

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    private val viewInterface: SearchContract.ViewInterface = mockk()

    private val localDataSource: LocalDataSource = mockk()

    private val remoteDataSource: RemoteDataSource = mockk()

    private lateinit var searchPresenter: SearchPresenter

    @Before
    fun setup() {
        searchPresenter = SearchPresenter(viewInterface, localDataSource, remoteDataSource)
    }

    @After
    fun clear() {
        unmockkAll()
    }

    /**
     * Given:
     * - searchMovie in remoteDataSource returns some TmbdResponse with movies as results
     * When:
     * - searchMovie is called with some query
     * Then should:
     * - call setMoviesList in viewInterface with list of movies from given TmbdResponse
     * - call showEmptySearchResult in viewInterface with false as isVisible
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

        every { viewInterface.showPlaceholder(false) } just Runs

        every { remoteDataSource.searchMovie(query) } returns Single.just(tmbdResponse)

        every { viewInterface.showProgress(true) } just Runs

        every { viewInterface.showProgress(false) } just Runs

        every { viewInterface.showEmptySearchResult(false) } just Runs

        every { viewInterface.setMoviesList(movies) } just Runs

        //When
        searchPresenter.searchMovie(query)

        //Then
        verifyOrder {
            viewInterface.showProgress(true)
            remoteDataSource.searchMovie(query)
            viewInterface.setMoviesList(movies)
            viewInterface.showEmptySearchResult(false)
            viewInterface.showProgress(false)
        }
    }

    /**
     * Given:
     * - searchMovie in remoteDataSource returns some TmbdResponse with empty list as results
     * When:
     * - searchMovie is called with some query
     * Then should:
     * - not call setMoviesList in viewInterface with list of movies from given TmbdResponse
     * - call showEmptySearchResult in viewInterface with true as isVisible
     */
    @Test
    fun check_searchMovie_emptyMoviesList() {
        //Given
        val query = "Titanic"

        val movies = emptyList<Movie>()

        val tmbdResponse = TmbdResponse(results = movies)

        every { viewInterface.showPlaceholder(false) } just Runs

        every { remoteDataSource.searchMovie(query) } returns Single.just(tmbdResponse)

        every { viewInterface.showProgress(true) } just Runs

        every { viewInterface.showProgress(false) } just Runs

        every { viewInterface.showEmptySearchResult(true) } just Runs

        every { viewInterface.clearMovieList() } just Runs

        //When
        searchPresenter.searchMovie(query)

        //Then
        verifyOrder {
            viewInterface.showProgress(true)
            remoteDataSource.searchMovie(query)
            viewInterface.showEmptySearchResult(true)
            viewInterface.clearMovieList()
            viewInterface.showProgress(false)
        }

        verify(exactly = 0) { viewInterface.setMoviesList(movies) }
    }

    /**
     * Given:
     * - searchMovie in remoteDataSource throws exception
     * When:
     * - searchMovie is called with some query
     * Then should:
     * - not call setMoviesList in viewInterface
     * - call showToast in viewInterface with R.string.general_error_message as resId
     * - call showPlaceholder in viewInterface with true as isVisible
     */
    @Test
    fun check_searchMovie_error() {
        //Given
        val query = "Titanic"

        val testException = Exception()

        every { viewInterface.showPlaceholder(false) } just Runs

        every { remoteDataSource.searchMovie(query) } returns Single.error(testException)

        every { viewInterface.showProgress(true) } just Runs

        every { viewInterface.showProgress(false) } just Runs

        every { viewInterface.showPlaceholder(true) } just Runs

        every { viewInterface.showToast(R.string.general_error_message) } just Runs

        //When
        searchPresenter.searchMovie(query)

        //Then
        verifyOrder {
            viewInterface.showProgress(true)
            remoteDataSource.searchMovie(query)
            viewInterface.showToast(R.string.general_error_message)
            viewInterface.showPlaceholder(true)
            viewInterface.showProgress(false)
        }

        verify(exactly = 0) { viewInterface.setMoviesList(any()) }
    }

    /**
     * When:
     * - addMovieToWatchlist is called with some movie
     * Then should:
     * - call showToast in viewInterface with R.string.movie_added_to_watchlist_message as resId
     */
    @Test
    fun check_addMovieToWatchlist() {
        //Given
        val movie = Movie(title = "The Mummy")

        every { localDataSource.insertMovie(movie) } returns Completable.complete()

        every { viewInterface.showProgress(true) } just Runs

        every { viewInterface.showProgress(false) } just Runs

        every { viewInterface.showToast(R.string.movie_added_to_watchlist_message) } just Runs

        //When
        searchPresenter.addMovieToWatchlist(movie)

        //Then
        verifyOrder {
            viewInterface.showProgress(true)
            localDataSource.insertMovie(movie)
            viewInterface.showToast(R.string.movie_added_to_watchlist_message)
            viewInterface.showProgress(false)
        }

    }

    /**
     * Given:
     * - insertMovie in localDataSource throws exception
     * When:
     * - addMovieToWatchlist is called with some movie
     * Then should:
     * - call showToast in viewInterface with R.string.general_error_message as resId
     */
    @Test
    fun check_addMovieToWatchlist_error() {
        //Given
        val movie = Movie(title = "The Mummy")

        val testException = Exception()

        every { localDataSource.insertMovie(movie) } returns Completable.error(testException)

        every { viewInterface.showProgress(true) } just Runs

        every { viewInterface.showProgress(false) } just Runs

        every { viewInterface.showToast(R.string.general_error_message) } just Runs

        //When
        searchPresenter.addMovieToWatchlist(movie)

        //Then
        verifyOrder {
            viewInterface.showProgress(true)
            localDataSource.insertMovie(movie)
            viewInterface.showToast(R.string.general_error_message)
            viewInterface.showProgress(false)
        }

    }

}