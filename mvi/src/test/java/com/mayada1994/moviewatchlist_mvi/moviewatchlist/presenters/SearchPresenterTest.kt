package com.mayada1994.moviewatchlist_mvi.moviewatchlist.presenters

import com.mayada1994.moviewatchlist_mvi.R
import com.mayada1994.moviewatchlist_mvi.entities.Movie
import com.mayada1994.moviewatchlist_mvi.interactors.SearchInteractor
import com.mayada1994.moviewatchlist_mvi.presenters.SearchPresenter
import com.mayada1994.moviewatchlist_mvi.rules.RxImmediateSchedulerRule
import com.mayada1994.moviewatchlist_mvi.states.SearchState
import com.mayada1994.moviewatchlist_mvi.views.SearchMovieView
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import io.reactivex.Observable
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SearchPresenterTest {

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    private val view: SearchMovieView = mockk(relaxed = true)

    private val interactor: SearchInteractor = mockk()

    private lateinit var presenter: SearchPresenter

    @Before
    fun setup() {
        presenter = SearchPresenter(interactor)
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
     * - some search state
     * - addMovieToWatchlist with some movie in interactor returns search state
     * When:
     * - observeAddMovieToWatchlistIntent is called
     * Then should:
     * - call addMovieToWatchlist with some movie in interactor
     * - call render with search state in view
     */
    @Test
    fun check_observeAddMovieToWatchlistIntent() {
        //Given
        val movie = Movie(title = "Titanic")
        val searchState = SearchState.CompletedState(R.string.movie_added_to_watchlist_message)

        every { view.addMovieToWatchlistIntent() } returns Observable.just(movie)
        every { interactor.addMovieToWatchlist(movie) } returns Observable.just(searchState)

        //When
        presenter.javaClass.getDeclaredMethod("observeAddMovieToWatchlistIntent").run {
            isAccessible = true
            invoke(presenter)
        }

        //Then
        verify {
            interactor.addMovieToWatchlist(movie)
            view.render(searchState)
        }
    }

    /**
     * Given:
     * - searchMovieIntent in view returns observable with "Titanic" as query
     * - some search state
     * - searchMovie with query in interactor returns search state
     * When:
     * - observeSearchMovieIntent is called
     * Then should:
     * - call searchMovie with query in interactor
     * - call render with search state in view
     */
    @Test
    fun check_observeSearchMovieIntent() {
        //Given
        val query = "Titanic"
        val movieState = SearchState.DataState(listOf(Movie(title = "Titanic")))

        every { view.searchMovieIntent() } returns Observable.just(query)
        every { interactor.searchMovie(query) } returns Observable.just(movieState)

        //When
        presenter.javaClass.getDeclaredMethod("observeSearchMovieIntent").run {
            isAccessible = true
            invoke(presenter)
        }

        //Then
        verify {
            interactor.searchMovie(query)
            view.render(movieState)
        }
    }

}