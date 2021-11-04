package com.mayada1994.moviewatchlist_mvi.presenters

import com.mayada1994.moviewatchlist_mvi.R
import com.mayada1994.moviewatchlist_mvi.entities.Movie
import com.mayada1994.moviewatchlist_mvi.fragments.MoviesFragment
import com.mayada1994.moviewatchlist_mvi.interactors.MoviesInteractor
import com.mayada1994.moviewatchlist_mvi.states.MoviesState
import com.mayada1994.moviewatchlist_mvi.views.MoviesView
import com.mayada1994.rules.RxImmediateSchedulerRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import io.reactivex.Observable
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MoviesPresenterTest {

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    private val view: MoviesView = mockk(relaxed = true)

    private val interactor: MoviesInteractor = mockk()

    private lateinit var presenter: MoviesPresenter

    @Before
    fun setup() {
        presenter = MoviesPresenter(interactor)
        presenter.bind(view)
    }

    @After
    fun clear() {
        presenter.unbind()
        unmockkAll()
    }

    /**
     * Given:
     * - addMovieToWatchlistIntent in view returns observable with some movie
     * - some movie state
     * - addMovieToWatchlist with some movie in interactor returns movie state
     * When:
     * - observeAddMovieToWatchlistIntent is called
     * Then should:
     * - call addMovieToWatchlist with some movie in interactor
     * - call render with movie state in view
     */
    @Test
    fun check_observeAddMovieToWatchlistIntent() {
        //Given
        val movie = Movie(title = "Titanic")
        val movieState = MoviesState.CompletedState(R.string.movie_added_to_watchlist_message)

        every { view.addMovieToWatchlistIntent() } returns Observable.just(movie)
        every { interactor.addMovieToWatchlist(movie) } returns Observable.just(movieState)

        //When
        presenter.javaClass.getDeclaredMethod("observeAddMovieToWatchlistIntent").run {
            isAccessible = true
            invoke(presenter)
        }

        //Then
        verify {
            interactor.addMovieToWatchlist(movie)
            view.render(movieState)
        }
    }

    /**
     * Given:
     * - displayMoviesIntent in view returns observable with MovieType.POPULAR
     * - some movie state
     * - getMovies with MovieType.POPULAR in interactor returns movie state
     * When:
     * - observeDisplayMoviesIntent is called
     * Then should:
     * - call getMovies with some MovieType.POPULAR in interactor
     * - call render with movie state in view
     */
    @Test
    fun check_observeDisplayMoviesIntent() {
        //Given
        val type = MoviesFragment.MovieType.POPULAR
        val movieState = MoviesState.DataState(listOf(Movie(title = "Titanic")))

        every { view.displayMoviesIntent() } returns Observable.just(type)
        every { interactor.getMovies(type) } returns Observable.just(movieState)

        //When
        presenter.javaClass.getDeclaredMethod("observeDisplayMoviesIntent").run {
            isAccessible = true
            invoke(presenter)
        }

        //Then
        verify {
            interactor.getMovies(type)
            view.render(movieState)
        }
    }

}