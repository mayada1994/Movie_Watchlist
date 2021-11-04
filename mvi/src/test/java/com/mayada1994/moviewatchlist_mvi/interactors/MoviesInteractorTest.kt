package com.mayada1994.moviewatchlist_mvi.interactors

import com.mayada1994.moviewatchlist_mvi.R
import com.mayada1994.moviewatchlist_mvi.entities.Movie
import com.mayada1994.moviewatchlist_mvi.entities.TmbdResponse
import com.mayada1994.moviewatchlist_mvi.fragments.MoviesFragment.MovieType
import com.mayada1994.moviewatchlist_mvi.repositories.MoviesRepository
import com.mayada1994.moviewatchlist_mvi.states.MoviesState
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

class MoviesInteractorTest {

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    private val moviesRepository: MoviesRepository = mockk()

    private lateinit var interactor: MoviesInteractor

    @Before
    fun setup() {
        interactor = MoviesInteractor(moviesRepository)
    }

    @After
    fun clear() {
        unmockkAll()
    }

    /**
     * Given:
     * - getPopularMovies in moviesRepository returns some TmbdResponse with movies as results
     * When:
     * - getMovies is called with POPULAR as MovieType
     * Then should:
     * - call getPopularMovies in moviesRepository
     * - return MoviesState.DataState with returned list of movies
     */
    @Test
    fun check_getMovies_Popular() {
        //Given
        val movieType = MovieType.POPULAR

        val movies = listOf(
            Movie(title = "Harry Potter and the Goblet of Fire"),
            Movie(title = "Titanic")
        )

        val tmbdResponse = TmbdResponse(results = movies)

        val moviesState = MoviesState.DataState(movies)

        every { moviesRepository.getPopularMovies(1) } returns Single.just(tmbdResponse)

        //When
        val result = interactor.getMovies(movieType)

        //Then
        verify { moviesRepository.getPopularMovies(1) }
        result.test().assertResult(moviesState)
    }

    /**
     * Given:
     * - getPopularMovies in moviesRepository returns some TmbdResponse with empty list of movies as results
     * When:
     * - getMovies is called with POPULAR as MovieType
     * Then should:
     * - call getPopularMovies in moviesRepository
     * - return MoviesState.EmptyState
     */
    @Test
    fun check_getMovies_Popular_emptyMoviesList() {
        //Given
        val movieType = MovieType.POPULAR

        val movies = emptyList<Movie>()

        val tmbdResponse = TmbdResponse(results = movies)

        val moviesState = MoviesState.EmptyState

        every { moviesRepository.getPopularMovies(1) } returns Single.just(tmbdResponse)

        //When
        val result = interactor.getMovies(movieType)

        //Then
        verify { moviesRepository.getPopularMovies(1) }
        result.test().assertResult(moviesState)
    }

    /**
     * Given:
     * - getPopularMovies in moviesRepository throws exception
     * When:
     * - getMovies is called with POPULAR as MovieType
     * Then should:
     * - call getPopularMovies in moviesRepository
     * - return MoviesState.ErrorState with R.string.general_error_message as resId
     */
    @Test
    fun check_getMovies_Popular_error() {
        //Given
        val movieType = MovieType.POPULAR

        val testException = Exception()

        val moviesState = MoviesState.ErrorState(R.string.general_error_message)

        every { moviesRepository.getPopularMovies(1) } returns Single.error(testException)

        //When
        val result = interactor.getMovies(movieType)

        //Then
        verify { moviesRepository.getPopularMovies(1) }
        result.test().assertResult(moviesState)
    }

    /**
     * Given:
     * - getUpcomingMovies in moviesRepository returns some TmbdResponse with movies as results
     * When:
     * - getMovies is called with UPCOMING as MovieType
     * Then should:
     * - call getUpcomingMovies in moviesRepository
     * - return MoviesState.DataState with returned list of movies
     */
    @Test
    fun check_getMovies_Upcoming() {
        //Given
        val movieType = MovieType.UPCOMING

        val movies = listOf(
            Movie(title = "Harry Potter and the Goblet of Fire"),
            Movie(title = "Titanic")
        )

        val tmbdResponse = TmbdResponse(results = movies)

        val moviesState = MoviesState.DataState(movies)

        every { moviesRepository.getUpcomingMovies(1) } returns Single.just(tmbdResponse)

        //When
        val result = interactor.getMovies(movieType)

        //Then
        verify { moviesRepository.getUpcomingMovies(1) }
        result.test().assertResult(moviesState)
    }

    /**
     * Given:
     * - getUpcomingMovies in moviesRepository returns some TmbdResponse with empty list of movies as results
     * When:
     * - getMovies is called with UPCOMING as MovieType
     * Then should:
     * - call getUpcomingMovies in moviesRepository
     * - return MoviesState.EmptyState
     */
    @Test
    fun check_getMovies_Upcoming_emptyMoviesList() {
        //Given
        val movieType = MovieType.UPCOMING

        val movies = emptyList<Movie>()

        val tmbdResponse = TmbdResponse(results = movies)

        val moviesState = MoviesState.EmptyState

        every { moviesRepository.getUpcomingMovies(1) } returns Single.just(tmbdResponse)

        //When
        val result = interactor.getMovies(movieType)

        //Then
        verify { moviesRepository.getUpcomingMovies(1) }
        result.test().assertResult(moviesState)
    }

    /**
     * Given:
     * - getUpcomingMovies in moviesRepository throws exception
     * When:
     * - getMovies is called with UPCOMING as MovieType
     * Then should:
     * - call getUpcomingMovies in moviesRepository
     * - return MoviesState.ErrorState with R.string.general_error_message as resId
     */
    @Test
    fun check_getMovies_Upcoming_error() {
        //Given
        val movieType = MovieType.UPCOMING

        val testException = Exception()

        val moviesState = MoviesState.ErrorState(R.string.general_error_message)

        every { moviesRepository.getUpcomingMovies(1) } returns Single.error(testException)

        //When
        val result = interactor.getMovies(movieType)

        //Then
        verify { moviesRepository.getUpcomingMovies(1) }
        result.test().assertResult(moviesState)
    }

    /**
     * When:
     * - addMovieToWatchlist is called with some movie
     * Then should:
     * - return MoviesState.CompletedState with R.string.movie_added_to_watchlist_message as resId
     */
    @Test
    fun check_addMovieToWatchlist() {
        //Given
        val movie = Movie(title = "The Mummy")

        val moviesState = MoviesState.CompletedState(R.string.movie_added_to_watchlist_message)

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
     * - return MoviesState.ErrorState with R.string.general_error_message as resId
     */
    @Test
    fun check_addMovieToWatchlist_error() {
        //Given
        val movie = Movie(title = "The Mummy")

        val testException = Exception()

        val moviesState = MoviesState.ErrorState(R.string.general_error_message)

        every { moviesRepository.insertMovie(movie) } returns Single.error(testException)

        //When
        val result = interactor.addMovieToWatchlist(movie)

        //Then
        verify { moviesRepository.insertMovie(movie) }
        result.test().assertResult(moviesState)
    }

}