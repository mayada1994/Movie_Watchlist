package com.mayada1994.moviewatchlist_mvvm.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.mayada1994.moviewatchlist_mvvm.R
import com.mayada1994.moviewatchlist_mvvm.entities.Movie
import com.mayada1994.moviewatchlist_mvvm.entities.TmbdResponse
import com.mayada1994.moviewatchlist_mvvm.fragments.MoviesFragment.MovieType
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

class MoviesViewModelTest {

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    @get:Rule var rule: TestRule = InstantTaskExecutorRule()

    private val observerMoviesList: Observer<List<Movie>> = mockk()
    private val observerIsProgressVisible: Observer<Boolean> = mockk()
    private val observerIsPlaceholderVisible: Observer<Boolean> = mockk()
    private val observerToastMessageStringResId: Observer<Int> = mockk()

    private val moviesRepository: MoviesRepository = mockk()

    private lateinit var moviesViewModel: MoviesViewModel

    @Before
    fun setup() {
        moviesViewModel = MoviesViewModel(moviesRepository)
        moviesViewModel.moviesList.observeForever(observerMoviesList)
        moviesViewModel.isProgressVisible.observeForever(observerIsProgressVisible)
        moviesViewModel.isPlaceholderVisible.observeForever(observerIsPlaceholderVisible)
        moviesViewModel.toastMessageStringResId.observeForever(observerToastMessageStringResId)
        every { observerMoviesList.onChanged(any()) } just Runs
        every { observerIsProgressVisible.onChanged(any()) } just Runs
        every { observerIsPlaceholderVisible.onChanged(any()) } just Runs
        every { observerToastMessageStringResId.onChanged(any()) } just Runs
    }

    @After
    fun clear() {
        moviesViewModel.onDestroy()
        unmockkAll()
    }

    /**
     * Given:
     * - getPopularMovies in moviesRepository returns some TmbdResponse with movies as results
     * When:
     * - init is called with POPULAR as MovieType
     * Then should:
     * - post moviesList in moviesViewModel with list of movies from given TmbdResponse
     * - post isPlaceholderVisible in moviesViewModel with false as isVisible
     */
    @Test
    fun check_init_Popular() {
        //Given
        val movieType = MovieType.POPULAR

        val movies = listOf(
            Movie(title = "Harry Potter and the Goblet of Fire"),
            Movie(title = "Titanic")
        )

        val tmbdResponse = TmbdResponse(results = movies)

        every { moviesRepository.getPopularMovies(1) } returns Single.just(tmbdResponse)

        //When
        moviesViewModel.init(movieType)

        //Then
        verifyOrder {
            observerIsProgressVisible.onChanged(true)
            moviesRepository.getPopularMovies(1)
            observerMoviesList.onChanged(movies)
            observerIsPlaceholderVisible.onChanged(false)
            observerIsProgressVisible.onChanged(false)
        }
    }

    /**
     * Given:
     * - getPopularMovies in moviesRepository returns some TmbdResponse with empty list as results
     * When:
     * - init is called with POPULAR as MovieType
     * Then should:
     * - not post moviesList in moviesViewModel
     * - post isPlaceholderVisible in moviesViewModel with true as isVisible
     */
    @Test
    fun check_init_Popular_emptyMoviesList() {
        //Given
        val movieType = MovieType.POPULAR

        val movies = emptyList<Movie>()

        val tmbdResponse = TmbdResponse(results = movies)

        every { moviesRepository.getPopularMovies(1) } returns Single.just(tmbdResponse)

        //When
        moviesViewModel.init(movieType)

        //Then
        verifyOrder {
            observerIsProgressVisible.onChanged(true)
            moviesRepository.getPopularMovies(1)
            observerIsPlaceholderVisible.onChanged(true)
            observerIsProgressVisible.onChanged(false)
        }

        verify(exactly = 0) { observerMoviesList.onChanged(movies) }
    }

    /**
     * Given:
     * - getPopularMovies in moviesRepository throws exception
     * When:
     * - init is called with POPULAR as MovieType
     * Then should:
     * - not call setEvent with SetMoviesList in moviesViewModel
     * - post isPlaceholderVisible in moviesViewModel with true as isVisible
     * - post toastMessageStringResId in moviesViewModel with R.string.general_error_message
     */
    @Test
    fun check_init_Popular_error() {
        //Given
        val movieType = MovieType.POPULAR

        val testException = Exception()

        every { moviesRepository.getPopularMovies(1) } returns Single.error(testException)

        //When
        moviesViewModel.init(movieType)

        //Then
        verifyOrder {
            observerIsProgressVisible.onChanged(true)
            moviesRepository.getPopularMovies(1)
            observerIsPlaceholderVisible.onChanged(true)
            observerToastMessageStringResId.onChanged(R.string.general_error_message)
            observerIsProgressVisible.onChanged(false)
        }
    }

    /**
     * Given:
     * - getUpcomingMovies in moviesRepository returns some TmbdResponse with movies as results
     * When:
     * - init is called with UPCOMING as MovieType
     * Then should:
     * - post moviesList in moviesViewModel with list of movies from given TmbdResponse
     * - post isPlaceholderVisible in moviesViewModel with false as isVisible
     */
    @Test
    fun check_init_Upcoming() {
        //Given
        val movieType = MovieType.UPCOMING

        val movies = listOf(
            Movie(title = "Harry Potter and the Goblet of Fire"),
            Movie(title = "Titanic")
        )

        val tmbdResponse = TmbdResponse(results = movies)

        every { moviesRepository.getUpcomingMovies(1) } returns Single.just(tmbdResponse)

        //When
        moviesViewModel.init(movieType)

        //Then
        verifyOrder {
            observerIsProgressVisible.onChanged(true)
            moviesRepository.getUpcomingMovies(1)
            observerMoviesList.onChanged(movies)
            observerIsPlaceholderVisible.onChanged(false)
            observerIsProgressVisible.onChanged(false)
        }
    }

    /**
     * Given:
     * - getUpcomingMovies in moviesRepository returns some TmbdResponse with empty list as results
     * When:
     * - init is called with UPCOMING as MovieType
     * Then should:
     * - not post moviesList in moviesViewModel
     * - post isPlaceholderVisible in moviesViewModel with true as isVisible
     */
    @Test
    fun check_init_Upcoming_emptyMoviesList() {
        //Given
        val movieType = MovieType.UPCOMING

        val movies = emptyList<Movie>()

        val tmbdResponse = TmbdResponse(results = movies)

        every { moviesRepository.getUpcomingMovies(1) } returns Single.just(tmbdResponse)

        //When
        moviesViewModel.init(movieType)

        //Then
        verifyOrder {
            observerIsProgressVisible.onChanged(true)
            moviesRepository.getUpcomingMovies(1)
            observerIsPlaceholderVisible.onChanged(true)
            observerIsProgressVisible.onChanged(false)
        }

        verify(exactly = 0) { observerMoviesList.onChanged(movies) }
    }

    /**
     * Given:
     * - getUpcomingMovies in moviesRepository throws exception
     * When:
     * - init is called with UPCOMING as MovieType
     * Then should:
     * - not post moviesList in moviesViewModel
     * - post isPlaceholderVisible in moviesViewModel with true as isVisible
     * - post toastMessageStringResId in moviesViewModel with R.string.general_error_message
     */
    @Test
    fun check_init_Upcoming_error() {
        //Given
        val movieType = MovieType.UPCOMING

        val testException = Exception()

        every { moviesRepository.getUpcomingMovies(1) } returns Single.error(testException)

        //When
        moviesViewModel.init(movieType)

        //Then
        verifyOrder {
            observerIsProgressVisible.onChanged(true)
            moviesRepository.getUpcomingMovies(1)
            observerIsPlaceholderVisible.onChanged(true)
            observerToastMessageStringResId.onChanged(R.string.general_error_message)
            observerIsProgressVisible.onChanged(false)
        }
    }

    /**
     * When:
     * - addMovieToWatchlist is called with some movie
     * Then should:
     * - post toastMessageStringResId with R.string.movie_added_to_watchlist_message
     */
    @Test
    fun check_addMovieToWatchlist() {
        //Given
        val movie = Movie(title = "The Mummy")

        every { moviesRepository.insertMovie(movie) } returns Completable.complete()

        //When
        moviesViewModel.addMovieToWatchlist(movie)

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
     * - post toastMessageStringResId with R.string.general_error_message
     */
    @Test
    fun check_addMovieToWatchlist_error() {
        //Given
        val movie = Movie(title = "The Mummy")

        val testException = Exception()

        every { moviesRepository.insertMovie(movie) } returns Completable.error(testException)

        //When
        moviesViewModel.addMovieToWatchlist(movie)

        //Then
        verifyOrder {
            observerIsProgressVisible.onChanged(true)
            moviesRepository.insertMovie(movie)
            observerToastMessageStringResId.onChanged(R.string.general_error_message)
            observerIsProgressVisible.onChanged(false)
        }

    }

}