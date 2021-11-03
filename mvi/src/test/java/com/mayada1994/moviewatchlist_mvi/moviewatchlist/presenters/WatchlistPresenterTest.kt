package com.mayada1994.moviewatchlist_mvi.moviewatchlist.presenters

import com.mayada1994.moviewatchlist_mvi.R
import com.mayada1994.moviewatchlist_mvi.entities.Movie
import com.mayada1994.moviewatchlist_mvi.interactors.WatchlistInteractor
import com.mayada1994.moviewatchlist_mvi.presenters.WatchlistPresenter
import com.mayada1994.moviewatchlist_mvi.rules.RxImmediateSchedulerRule
import com.mayada1994.moviewatchlist_mvi.states.WatchlistState
import com.mayada1994.moviewatchlist_mvi.views.WatchlistView
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import io.reactivex.Observable
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class WatchlistPresenterTest {

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    private val view: WatchlistView = mockk(relaxed = true)

    private val interactor: WatchlistInteractor = mockk()

    private lateinit var presenter: WatchlistPresenter

    @Before
    fun setup() {
        presenter = WatchlistPresenter(interactor)
        presenter.bind(view)
    }

    @After
    fun clear() {
        presenter.unbind()
        unmockkAll()
    }

    /**
     * Given:
     * - deleteMoviesFromWatchlistIntent in view returns observable with Unit
     * - some search state
     * - deleteMoviesFromWatchlist with some list of movies in interactor returns search state
     * When:
     * - observeDeleteMoviesFromWatchlistIntent is called
     * Then should:
     * - call deleteMoviesFromWatchlist with some list of movies in interactor
     * - call render with search state in view
     */
    @Test
    fun check_observeDeleteMoviesFromWatchlistIntent() {
        //Given
        val movies = listOf(Movie(title = "Titanic"))
        val searchState = WatchlistState.UpdateDataState(movies, R.string.movie_deleted_message)

        every { view.deleteMoviesFromWatchlistIntent() } returns Observable.just(Unit)
        every { interactor.deleteMoviesFromWatchlist(movies) } returns Observable.just(searchState)
        every { view.selectMovieIntent() } returns Observable.just(movies[0] to true)

        presenter.javaClass.getDeclaredMethod("observeSelectMovieIntent").run {
            isAccessible = true
            invoke(presenter)
        }

        //When
        presenter.javaClass.getDeclaredMethod("observeDeleteMoviesFromWatchlistIntent").run {
            isAccessible = true
            invoke(presenter)
        }

        //Then
        verify {
            interactor.deleteMoviesFromWatchlist(movies)
            view.render(searchState)
        }
    }

    /**
     * Given:
     * - searchMovieIntent in view returns observable with Unit
     * - some search state
     * - getMovies in interactor returns search state
     * When:
     * - observeDisplayMoviesIntent is called
     * Then should:
     * - call getMovies in interactor
     * - call render with search state in view
     */
    @Test
    fun check_observeDisplayMoviesIntent() {
        //Given
        val movieState = WatchlistState.DataState(listOf(Movie(title = "Titanic")))

        every { view.displayMoviesIntent() } returns Observable.just(Unit)
        every { interactor.getMovies() } returns Observable.just(movieState)

        //When
        presenter.javaClass.getDeclaredMethod("observeDisplayMoviesIntent").run {
            isAccessible = true
            invoke(presenter)
        }

        //Then
        verify {
            interactor.getMovies()
            view.render(movieState)
        }
    }

    /**
     * Given:
     * - floatingActionButtonClickIntent in view returns observable with Unit
     * - some search state
     * - onFloatingActionButtonClick with some list of movies in interactor returns search state
     * When:
     * - observeFloatingActionButtonClickIntent is called
     * Then should:
     * - call onFloatingActionButtonClick with some list of movies in interactor
     * - call render with search state in view
     */
    @Test
    fun check_observeFloatingActionButtonClickIntent() {
        //Given
        val movies = listOf(Movie(title = "Titanic"))
        val searchState = WatchlistState.ShowDeleteMoviesDialogState

        every { view.floatingActionButtonClickIntent() } returns Observable.just(Unit)
        every { interactor.onFloatingActionButtonClick(movies) } returns Observable.just(searchState)
        every { view.selectMovieIntent() } returns Observable.just(movies[0] to true)

        presenter.javaClass.getDeclaredMethod("observeSelectMovieIntent").run {
            isAccessible = true
            invoke(presenter)
        }

        //When
        presenter.javaClass.getDeclaredMethod("observeFloatingActionButtonClickIntent").run {
            isAccessible = true
            invoke(presenter)
        }

        //Then
        verify {
            interactor.onFloatingActionButtonClick(movies)
            view.render(searchState)
        }
    }

    /**
     * Given:
     * - selectMovieIntent in view returns observable with pair of some movie to true
     * - some search state
     * - onMovieSelected with some list of movies in interactor returns search state
     * When:
     * - observeSelectMovieIntent is called
     * Then should:
     * - call onMovieSelected with some list of movies in interactor
     * - call render with search state in view
     */
    @Test
    fun check_observeSelectMovieIntent() {
        //Given
        val movies = listOf(Movie(title = "Titanic"))
        val searchState = WatchlistState.FloatingActionButtonImageState(android.R.drawable.ic_delete)

        every { view.selectMovieIntent() } returns Observable.just(movies[0] to true)
        every { interactor.onMovieSelected(movies) } returns Observable.just(searchState)

        //When
        presenter.javaClass.getDeclaredMethod("observeSelectMovieIntent").run {
            isAccessible = true
            invoke(presenter)
        }

        //Then
        verify {
            interactor.onMovieSelected(movies)
            view.render(searchState)
        }
    }

    /**
     * Given:
     * - checkMoviesListIntent in view returns observable with some list of movies
     * - some search state
     * - onCheckMoviesList with some list of movies in interactor returns search state
     * When:
     * - observeCheckMoviesListIntent is called
     * Then should:
     * - call onCheckMoviesList with some list of movies in interactor
     * - call render with search state in view
     */
    @Test
    fun check_observeCheckMoviesListIntent() {
        //Given
        val movies = listOf(Movie(title = "Titanic"))
        val searchState = WatchlistState.FloatingActionButtonImageState(android.R.drawable.ic_input_add)

        every { view.checkMoviesListIntent() } returns Observable.just(movies)
        every { interactor.onCheckMoviesList(movies) } returns Observable.just(searchState)

        //When
        presenter.javaClass.getDeclaredMethod("observeCheckMoviesListIntent").run {
            isAccessible = true
            invoke(presenter)
        }

        //Then
        verify {
            interactor.onCheckMoviesList(movies)
            view.render(searchState)
        }
    }

}