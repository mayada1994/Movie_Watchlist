package com.mayada1994.moviewatchlist_mvi.interactors

import com.mayada1994.moviewatchlist_mvi.R
import com.mayada1994.moviewatchlist_mvi.entities.Movie
import com.mayada1994.moviewatchlist_mvi.entities.TmbdResponse
import com.mayada1994.moviewatchlist_mvi.repositories.MoviesRepository
import com.mayada1994.moviewatchlist_mvi.states.SearchState
import com.mayada1994.rules.RxImmediateSchedulerRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import io.reactivex.Single
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SearchInteractorTest {

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    private val moviesRepository: MoviesRepository = mockk()

    private lateinit var interactor: SearchInteractor

    @Before
    fun setup() {
        interactor = SearchInteractor(moviesRepository)
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
     * - call searchMovie in moviesRepository
     * - return SearchState.DataState with returned list of movies
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

        val moviesState = SearchState.DataState(movies)

        every { moviesRepository.searchMovie(query) } returns Single.just(tmbdResponse)

        //When
        val result = interactor.searchMovie(query)

        //Then
        verify { moviesRepository.searchMovie(query) }
        result.test().assertResult(moviesState)
    }

    /**
     * Given:
     * - searchMovie in moviesRepository returns some TmbdResponse with empty list of movies as results
     * When:
     * - searchMovie is called with some query
     * Then should:
     * - call searchMovie in moviesRepository
     * - return SearchState.EmptyState
     */
    @Test
    fun check_searchMovie_emptyMoviesList() {
        //Given
        val query = "Harry"

        val movies = emptyList<Movie>()

        val tmbdResponse = TmbdResponse(results = movies)

        val moviesState = SearchState.EmptyState

        every { moviesRepository.searchMovie(query) } returns Single.just(tmbdResponse)

        //When
        val result = interactor.searchMovie(query)

        //Then
        verify { moviesRepository.searchMovie(query) }
        result.test().assertResult(moviesState)
    }

    /**
     * Given:
     * - searchMovie in moviesRepository throws exception
     * When:
     * - searchMovie is called with some query
     * Then should:
     * - call searchMovie in moviesRepository
     * - return SearchState.ErrorState with R.string.general_error_message as resId
     */
    @Test
    fun check_searchMovie_error() {
        //Given
        val query = "Harry"

        val testException = Exception()

        val moviesState = SearchState.ErrorState(R.string.general_error_message)

        every { moviesRepository.searchMovie(query) } returns Single.error(testException)

        //When
        val result = interactor.searchMovie(query)

        //Then
        verify { moviesRepository.searchMovie(query) }
        result.test().assertResult(moviesState)
    }

    /**
     * When:
     * - addMovieToWatchlist is called with some movie
     * Then should:
     * - return SearchState.CompletedState with R.string.movie_added_to_watchlist_message as resId
     */
    @Test
    fun check_addMovieToWatchlist() {
        //Given
        val movie = Movie(title = "The Mummy")

        val moviesState = SearchState.CompletedState(R.string.movie_added_to_watchlist_message)

        every { moviesRepository.insertMovie(movie) } returns Single.just(1L)

        //When
        val result = interactor.addMovieToWatchlist(movie)

        //Then
        verify { moviesRepository.insertMovie(movie) }
        result.test().assertResult(moviesState)
    }

    /**
     * Given:
     * - insertMovie in moviesRepository throws exception
     * When:
     * - addMovieToWatchlist is called with some movie
     * Then should:
     * - return SearchState.CompletedState with R.string.general_error_message as resId
     */
    @Test
    fun check_addMovieToWatchlist_error() {
        //Given
        val movie = Movie(title = "The Mummy")

        val testException = Exception()

        val moviesState = SearchState.CompletedState(R.string.general_error_message)

        every { moviesRepository.insertMovie(movie) } returns Single.error(testException)

        //When
        val result = interactor.addMovieToWatchlist(movie)

        //Then
        verify { moviesRepository.insertMovie(movie) }
        result.test().assertResult(moviesState)
    }
    
}