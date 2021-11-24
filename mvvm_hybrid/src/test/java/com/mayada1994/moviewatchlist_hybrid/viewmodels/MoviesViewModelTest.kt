package com.mayada1994.moviewatchlist_hybrid.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.mayada1994.moviewatchlist_hybrid.R
import com.mayada1994.moviewatchlist_hybrid.entities.Movie
import com.mayada1994.moviewatchlist_hybrid.entities.TmbdResponse
import com.mayada1994.moviewatchlist_hybrid.events.BaseEvent
import com.mayada1994.moviewatchlist_hybrid.events.MoviesEvent
import com.mayada1994.moviewatchlist_hybrid.events.ViewEvent
import com.mayada1994.moviewatchlist_hybrid.fragments.MoviesFragment.MovieType
import com.mayada1994.moviewatchlist_hybrid.repositories.MoviesRepository
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

    private val observerViewEvent: Observer<ViewEvent> = mockk()

    private val moviesRepository: MoviesRepository = mockk()

    private lateinit var moviesViewModel: MoviesViewModel

    @Before
    fun setup() {
        moviesViewModel = MoviesViewModel(moviesRepository)
        moviesViewModel.event.observeForever(observerViewEvent)
        every { observerViewEvent.onChanged(any()) } just Runs
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
     * - call setEvent with SetMoviesList in moviesViewModel with list of movies from given TmbdResponse
     * - call setEvent with ShowPlaceholder in moviesViewModel with false as isVisible
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
            observerViewEvent.onChanged(BaseEvent.ShowProgress(true))
            moviesRepository.getPopularMovies(1)
            observerViewEvent.onChanged(MoviesEvent.SetMoviesList(movies))
            observerViewEvent.onChanged(BaseEvent.ShowPlaceholder(false))
            observerViewEvent.onChanged(BaseEvent.ShowProgress(false))
        }
    }

    /**
     * Given:
     * - getPopularMovies in moviesRepository returns some TmbdResponse with empty list as results
     * When:
     * - init is called with POPULAR as MovieType
     * Then should:
     * - not call setEvent with SetMoviesList in moviesViewModel
     * - call setEvent with ShowPlaceholder in moviesViewModel with true as isVisible
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
            observerViewEvent.onChanged(BaseEvent.ShowProgress(true))
            moviesRepository.getPopularMovies(1)
            observerViewEvent.onChanged(BaseEvent.ShowPlaceholder(true))
            observerViewEvent.onChanged(BaseEvent.ShowProgress(false))
        }

        verify(exactly = 0) { observerViewEvent.onChanged(MoviesEvent.SetMoviesList(movies)) }
    }

    /**
     * Given:
     * - getPopularMovies in moviesRepository throws exception
     * When:
     * - init is called with POPULAR as MovieType
     * Then should:
     * - not call setEvent with SetMoviesList in moviesViewModel
     * - call setEvent with ShowPlaceholder in moviesViewModel with true as isVisible
     * - call setEvent with ShowMessage in moviesViewModel with R.string.general_error_message as resId
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
            observerViewEvent.onChanged(BaseEvent.ShowProgress(true))
            moviesRepository.getPopularMovies(1)
            observerViewEvent.onChanged(BaseEvent.ShowPlaceholder(true))
            observerViewEvent.onChanged(BaseEvent.ShowMessage(R.string.general_error_message))
            observerViewEvent.onChanged(BaseEvent.ShowProgress(false))
        }
    }

    /**
     * Given:
     * - getUpcomingMovies in moviesRepository returns some TmbdResponse with movies as results
     * When:
     * - init is called with UPCOMING as MovieType
     * Then should:
     * - call setEvent with SetMoviesList in moviesViewModel with list of movies from given TmbdResponse
     * - call setEvent with ShowPlaceholder in moviesViewModel with false as isVisible
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
            observerViewEvent.onChanged(BaseEvent.ShowProgress(true))
            moviesRepository.getUpcomingMovies(1)
            observerViewEvent.onChanged(MoviesEvent.SetMoviesList(movies))
            observerViewEvent.onChanged(BaseEvent.ShowPlaceholder(false))
            observerViewEvent.onChanged(BaseEvent.ShowProgress(false))
        }
    }

    /**
     * Given:
     * - getUpcomingMovies in moviesRepository returns some TmbdResponse with empty list as results
     * When:
     * - init is called with UPCOMING as MovieType
     * Then should:
     * - not call setEvent with SetMoviesList in moviesViewModel
     * - call setEvent with ShowPlaceholder in moviesViewModel with true as isVisible
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
            observerViewEvent.onChanged(BaseEvent.ShowProgress(true))
            moviesRepository.getUpcomingMovies(1)
            observerViewEvent.onChanged(BaseEvent.ShowPlaceholder(true))
            observerViewEvent.onChanged(BaseEvent.ShowProgress(false))
        }

        verify(exactly = 0) { observerViewEvent.onChanged(MoviesEvent.SetMoviesList(movies)) }
    }

    /**
     * Given:
     * - getUpcomingMovies in moviesRepository throws exception
     * When:
     * - init is called with UPCOMING as MovieType
     * Then should:
     * - not setEvent with SetMoviesList in moviesViewModel
     * - call setEvent with ShowPlaceholder in moviesViewModel with true as isVisible
     * - call setEvent with ShowMessage in moviesViewModel with R.string.general_error_message as resId
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
            observerViewEvent.onChanged(BaseEvent.ShowProgress(true))
            moviesRepository.getUpcomingMovies(1)
            observerViewEvent.onChanged(BaseEvent.ShowPlaceholder(true))
            observerViewEvent.onChanged(BaseEvent.ShowMessage(R.string.general_error_message))
            observerViewEvent.onChanged(BaseEvent.ShowProgress(false))
        }
    }

    /**
     * When:
     * - addMovieToWatchlist is called with some movie
     * Then should:
     * - call setEvent with ShowMessage with R.string.movie_added_to_watchlist_message as resId
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
            observerViewEvent.onChanged(BaseEvent.ShowProgress(true))
            moviesRepository.insertMovie(movie)
            observerViewEvent.onChanged(BaseEvent.ShowMessage(R.string.movie_added_to_watchlist_message))
            observerViewEvent.onChanged(BaseEvent.ShowProgress(false))
        }

    }

    /**
     * Given:
     * - insertMovie in moviesRepository throws exception
     * When:
     * - addMovieToWatchlist is called with some movie
     * Then should:
     * - call setEvent with ShowMessage with R.string.general_error_message as resId
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
            observerViewEvent.onChanged(BaseEvent.ShowProgress(true))
            moviesRepository.insertMovie(movie)
            observerViewEvent.onChanged(BaseEvent.ShowMessage(R.string.general_error_message))
            observerViewEvent.onChanged(BaseEvent.ShowProgress(false))
        }

    }

}