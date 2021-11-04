package com.mayada1994.moviewatchlist_mvp.presenters

import com.mayada1994.moviewatchlist_mvp.R
import com.mayada1994.moviewatchlist_mvp.contracts.MoviesContract
import com.mayada1994.moviewatchlist_mvp.entities.Movie
import com.mayada1994.moviewatchlist_mvp.entities.TmbdResponse
import com.mayada1994.moviewatchlist_mvp.fragments.MoviesFragment.MovieType
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

class MoviesPresenterTest {

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    private val viewInterface: MoviesContract.ViewInterface = mockk()

    private val localDataSource: LocalDataSource = mockk()

    private val remoteDataSource: RemoteDataSource = mockk()

    private lateinit var moviesPresenter: MoviesPresenter

    @Before
    fun setup() {
        moviesPresenter = MoviesPresenter(viewInterface, localDataSource, remoteDataSource)
    }

    @After
    fun clear() {
        unmockkAll()
    }

    /**
     * Given:
     * - getPopularMovies in remoteDataSource returns some TmbdResponse with movies as results
     * When:
     * - init is called with POPULAR as MovieType
     * Then should:
     * - call setMoviesList in viewInterface with list of movies from given TmbdResponse
     * - call showPlaceholder in viewInterface with false as isVisible
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

        every { remoteDataSource.getPopularMovies(1) } returns Single.just(tmbdResponse)

        every { viewInterface.showProgress(true) } just Runs

        every { viewInterface.showProgress(false) } just Runs

        every { viewInterface.showPlaceholder(false) } just Runs

        every { viewInterface.setMoviesList(movies) } just Runs

        //When
        moviesPresenter.init(movieType)

        //Then
        verifyOrder {
            viewInterface.showProgress(true)
            remoteDataSource.getPopularMovies(1)
            viewInterface.setMoviesList(movies)
            viewInterface.showPlaceholder(false)
            viewInterface.showProgress(false)
        }
    }

    /**
     * Given:
     * - getPopularMovies in remoteDataSource returns some TmbdResponse with empty list as results
     * When:
     * - init is called with POPULAR as MovieType
     * Then should:
     * - not call setMoviesList in viewInterface
     * - call showPlaceholder in viewInterface with true as isVisible
     */
    @Test
    fun check_init_Popular_emptyMoviesList() {
        //Given
        val movieType = MovieType.POPULAR

        val movies = emptyList<Movie>()

        val tmbdResponse = TmbdResponse(results = movies)

        every { remoteDataSource.getPopularMovies(1) } returns Single.just(tmbdResponse)

        every { viewInterface.showProgress(true) } just Runs

        every { viewInterface.showProgress(false) } just Runs

        every { viewInterface.showPlaceholder(true) } just Runs

        //When
        moviesPresenter.init(movieType)

        //Then
        verifyOrder {
            viewInterface.showProgress(true)
            remoteDataSource.getPopularMovies(1)
            viewInterface.showPlaceholder(true)
            viewInterface.showProgress(false)
        }

        verify(exactly = 0) { viewInterface.setMoviesList(movies) }
    }

    /**
     * Given:
     * - getPopularMovies in remoteDataSource throws exception
     * When:
     * - init is called with POPULAR as MovieType
     * Then should:
     * - not call setMoviesList in viewInterface
     * - call showPlaceholder in viewInterface with true as isVisible
     * - call showToast in viewInterface with R.string.general_error_message as resId
     */
    @Test
    fun check_init_Popular_error() {
        //Given
        val movieType = MovieType.POPULAR

        val testException = Exception()

        every { remoteDataSource.getPopularMovies(1) } returns Single.error(testException)

        every { viewInterface.showProgress(true) } just Runs

        every { viewInterface.showProgress(false) } just Runs

        every { viewInterface.showPlaceholder(true) } just Runs

        every { viewInterface.showToast(R.string.general_error_message) } just Runs

        //When
        moviesPresenter.init(movieType)

        //Then
        verifyOrder {
            viewInterface.showProgress(true)
            remoteDataSource.getPopularMovies(1)
            viewInterface.showPlaceholder(true)
            viewInterface.showToast(R.string.general_error_message)
            viewInterface.showProgress(false)
        }

        verify(exactly = 0) { viewInterface.setMoviesList(any()) }
    }

    /**
     * Given:
     * - getUpcomingMovies in remoteDataSource returns some TmbdResponse with movies as results
     * When:
     * - init is called with UPCOMING as MovieType
     * Then should:
     * - call setMoviesList in viewInterface with list of movies from given TmbdResponse
     * - call showPlaceholder in viewInterface with false as isVisible
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

        every { remoteDataSource.getUpcomingMovies(1) } returns Single.just(tmbdResponse)

        every { viewInterface.showProgress(true) } just Runs

        every { viewInterface.showProgress(false) } just Runs

        every { viewInterface.showPlaceholder(false) } just Runs

        every { viewInterface.setMoviesList(movies) } just Runs

        //When
        moviesPresenter.init(movieType)

        //Then
        verifyOrder {
            viewInterface.showProgress(true)
            remoteDataSource.getUpcomingMovies(1)
            viewInterface.setMoviesList(movies)
            viewInterface.showPlaceholder(false)
            viewInterface.showProgress(false)
        }
    }

    /**
     * Given:
     * - getUpcomingMovies in remoteDataSource returns some TmbdResponse with empty list as results
     * When:
     * - init is called with UPCOMING as MovieType
     * Then should:
     * - not call setMoviesList in viewInterface
     * - call showPlaceholder in viewInterface with true as isVisible
     */
    @Test
    fun check_init_Upcoming_emptyMoviesList() {
        //Given
        val movieType = MovieType.UPCOMING

        val movies = emptyList<Movie>()

        val tmbdResponse = TmbdResponse(results = movies)

        every { remoteDataSource.getUpcomingMovies(1) } returns Single.just(tmbdResponse)

        every { viewInterface.showProgress(true) } just Runs

        every { viewInterface.showProgress(false) } just Runs

        every { viewInterface.showPlaceholder(true) } just Runs

        //When
        moviesPresenter.init(movieType)

        //Then
        verifyOrder {
            viewInterface.showProgress(true)
            remoteDataSource.getUpcomingMovies(1)
            viewInterface.showPlaceholder(true)
            viewInterface.showProgress(false)
        }

        verify(exactly = 0) { viewInterface.setMoviesList(movies) }
    }

    /**
     * Given:
     * - getUpcomingMovies in remoteDataSource throws exception
     * When:
     * - init is called with UPCOMING as MovieType
     * Then should:
     * - not call setMoviesList in viewInterface
     * - call showPlaceholder in viewInterface with true as isVisible
     * - call showToast in viewInterface with R.string.general_error_message as resId
     */
    @Test
    fun check_init_Upcoming_error() {
        //Given
        val movieType = MovieType.UPCOMING

        val testException = Exception()

        every { remoteDataSource.getUpcomingMovies(1) } returns Single.error(testException)

        every { viewInterface.showProgress(true) } just Runs

        every { viewInterface.showProgress(false) } just Runs

        every { viewInterface.showPlaceholder(true) } just Runs

        every { viewInterface.showToast(R.string.general_error_message) } just Runs

        //When
        moviesPresenter.init(movieType)

        //Then
        verifyOrder {
            viewInterface.showProgress(true)
            remoteDataSource.getUpcomingMovies(1)
            viewInterface.showPlaceholder(true)
            viewInterface.showToast(R.string.general_error_message)
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
        moviesPresenter.addMovieToWatchlist(movie)

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
        moviesPresenter.addMovieToWatchlist(movie)

        //Then
        verifyOrder {
            viewInterface.showProgress(true)
            localDataSource.insertMovie(movie)
            viewInterface.showToast(R.string.general_error_message)
            viewInterface.showProgress(false)
        }

    }

}