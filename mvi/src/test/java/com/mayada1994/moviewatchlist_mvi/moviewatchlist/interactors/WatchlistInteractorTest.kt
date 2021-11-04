package com.mayada1994.moviewatchlist_mvi.moviewatchlist.interactors

import com.mayada1994.moviewatchlist_mvi.R
import com.mayada1994.moviewatchlist_mvi.entities.Movie
import com.mayada1994.moviewatchlist_mvi.interactors.WatchlistInteractor
import com.mayada1994.moviewatchlist_mvi.repositories.MoviesRepository
import com.mayada1994.moviewatchlist_mvi.rules.RxImmediateSchedulerRule
import com.mayada1994.moviewatchlist_mvi.states.WatchlistState
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import io.reactivex.Single
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class WatchlistInteractorTest {

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    private val moviesRepository: MoviesRepository = mockk()

    private lateinit var interactor: WatchlistInteractor

    @Before
    fun setup() {
        interactor = WatchlistInteractor(moviesRepository)
    }

    @After
    fun clear() {
        unmockkAll()
    }

    /**
     * Given:
     * - getMovies in moviesRepository returns some list of movies from DB
     * When:
     * - getMovies is called
     * Then should:
     * - call getMovies in moviesRepository to get some list of movies from DB
     * - return WatchlistState.DataState with returned list of movies
     */
    @Test
    fun check_getMovies() {
        //Given
        val movies = listOf(
            Movie(title = "Harry Potter and the Goblet of Fire"),
            Movie(title = "Titanic")
        )

        val watchlistState = WatchlistState.DataState(movies)

        every { moviesRepository.getMovies() } returns Single.just(movies)

        //When
        val result = interactor.getMovies()

        //Then
        verify { moviesRepository.getMovies() }
        result.test().assertResult(watchlistState)
    }

    /**
     * Given:
     * - getMovies in moviesRepository returns empty list of movies from DB
     * When:
     * - getMovies is called
     * Then should:
     * - call getMovies in moviesRepository to get some list of movies from DB
     * - return WatchlistState.EmptyState
     */
    @Test
    fun check_getMovies_emptyMoviesList() {
        //Given
        val movies = emptyList<Movie>()

        val watchlistState = WatchlistState.EmptyState

        every { moviesRepository.getMovies() } returns Single.just(movies)

        //When
        val result = interactor.getMovies()

        //Then
        verify { moviesRepository.getMovies() }
        result.test().assertResult(watchlistState)
    }

    /**
     * Given:
     * - getMovies in moviesRepository throws exception
     * When:
     * - getMovies is called
     * Then should:
     * - call getMovies in moviesRepository to get some list of movies from DB
     * - return WatchlistState.ErrorState with R.string.general_error_message as resId
     */
    @Test
    fun check_getMovies_error() {
        //Given
        val testException = Exception()

        val watchlistState = WatchlistState.ErrorState(R.string.general_error_message)

        every { moviesRepository.getMovies() } returns Single.error(testException)

        //When
        val result = interactor.getMovies()

        //Then
        verify { moviesRepository.getMovies() }
        result.test().assertResult(watchlistState)
    }

    /**
     * Given:
     * - some list of movies containing one element
     * When:
     * - deleteMoviesFromWatchlist is called
     * Then should:
     * - return WatchlistState.UpdateDataState with list of movies and R.string.movie_deleted_message as resId
     */
    @Test
    fun check_deleteMoviesFromWatchlist_singleElement() {
        //Given
        val movies = listOf(Movie(title = "The Mummy"))

        val watchlistState = WatchlistState.UpdateDataState(movies, R.string.movie_deleted_message)

        every { moviesRepository.deleteMovies(movies) } returns Single.just(Unit)

        interactor.onMovieSelected(movies)

        //When
        val result = interactor.deleteMoviesFromWatchlist(movies)

        //Then
        verify { moviesRepository.deleteMovies(movies) }
        result.test().assertResult(watchlistState)
    }

    /**
     * Given:
     * - some list of movies containing multiple elements
     * When:
     * - deleteMoviesFromWatchlist is called
     * Then should:
     * - return WatchlistState.UpdateDataState with list of movies and R.string.movies_deleted_message as resId
     */
    @Test
    fun check_deleteMoviesFromWatchlist_multipleElements() {
        //Given
        val movies = listOf(Movie(title = "The Mummy"), Movie(title = "The Mummy Returns"))

        val watchlistState = WatchlistState.UpdateDataState(movies, R.string.movies_deleted_message)

        every { moviesRepository.deleteMovies(movies) } returns Single.just(Unit)

        interactor.onMovieSelected(movies)

        //When
        val result = interactor.deleteMoviesFromWatchlist(movies)

        //Then
        verify { moviesRepository.deleteMovies(movies) }
        result.test().assertResult(watchlistState)
    }

    /**
     * Given:
     * - insertMovie in moviesRepository throws exception
     * When:
     * - deleteMoviesFromWatchlist is called
     * Then should:
     * - return WatchlistState.ErrorState with R.string.general_error_message as resId
     */
    @Test
    fun check_deleteMoviesFromWatchlist_error() {
        //Given
        val movies = listOf(Movie(title = "The Mummy"))
        
        val testException = Exception()

        val watchlistState = WatchlistState.ErrorState(R.string.general_error_message)

        every { moviesRepository.deleteMovies(movies) } returns Single.error(testException)

        //When
        val result = interactor.deleteMoviesFromWatchlist(movies)

        //Then
        verify { moviesRepository.deleteMovies(movies) }
        result.test().assertResult(watchlistState)
    }

    /**
     * When:
     * - onFloatingActionButtonClick is called with some list of movies
     * Then should:
     * - return WatchlistState.ShowDeleteMoviesDialogState
     */
    @Test
    fun check_onFloatingActionButtonClick() {
        //Given
        val movies = listOf(Movie(title = "The Mummy"))

        val watchlistState = WatchlistState.ShowDeleteMoviesDialogState

        //When
        val result = interactor.onFloatingActionButtonClick(movies)

        //Then
        result.test().assertResult(watchlistState)
    }

    /**
     * When:
     * - onFloatingActionButtonClick is called with empty list of movies
     * Then should:
     * - return WatchlistState.NavigateToSearchScreenState
     */
    @Test
    fun check_onFloatingActionButtonClick_emptyList() {
        //Given
        val movies = emptyList<Movie>()

        val watchlistState = WatchlistState.NavigateToSearchScreenState

        //When
        val result = interactor.onFloatingActionButtonClick(movies)

        //Then
        result.test().assertResult(watchlistState)
    }

    /**
     * When:
     * - onMovieSelected is called with some list of movies
     * Then should:
     * - return WatchlistState.FloatingActionButtonImageState with android.R.drawable.ic_delete as resId
     */
    @Test
    fun check_onMovieSelected() {
        //Given
        val movies = listOf(Movie(title = "The Mummy"))

        val watchlistState = WatchlistState.FloatingActionButtonImageState(android.R.drawable.ic_delete)

        //When
        val result = interactor.onMovieSelected(movies)

        //Then
        result.test().assertResult(watchlistState)
    }

    /**
     * When:
     * - onMovieSelected is called with empty list of movies
     * Then should:
     * - return WatchlistState.FloatingActionButtonImageState with android.R.drawable.ic_input_add as resId
     */
    @Test
    fun check_onMovieSelected_emptyList() {
        //Given
        val movies = emptyList<Movie>()

        val watchlistState = WatchlistState.FloatingActionButtonImageState(android.R.drawable.ic_input_add)

        //When
        val result = interactor.onMovieSelected(movies)

        //Then
        result.test().assertResult(watchlistState)
    }

    /**
     * When:
     * - onCheckMoviesList is called with some list of movies
     * Then should:
     * - return WatchlistState.FloatingActionButtonImageState with android.R.drawable.ic_input_add as resId
     */
    @Test
    fun check_onCheckMoviesList() {
        //Given
        val movies = listOf(Movie(title = "The Mummy"))

        val watchlistState = WatchlistState.FloatingActionButtonImageState(android.R.drawable.ic_input_add)

        //When
        val result = interactor.onCheckMoviesList(movies)

        //Then
        result.test().assertResult(watchlistState)
    }

    /**
     * When:
     * - onCheckMoviesList is called with empty list of movies
     * Then should:
     * - return WatchlistState.EmptyState
     */
    @Test
    fun check_onCheckMoviesList_emptyList() {
        //Given
        val movies = emptyList<Movie>()

        val watchlistState = WatchlistState.EmptyState

        //When
        val result = interactor.onCheckMoviesList(movies)

        //Then
        result.test().assertResult(watchlistState)
    }
    
}