package com.mayada1994.moviewatchlist_mvi.moviewatchlist.interactors

import com.mayada1994.moviewatchlist_mvi.R
import com.mayada1994.moviewatchlist_mvi.fragments.MoviesFragment
import com.mayada1994.moviewatchlist_mvi.fragments.WatchlistFragment
import com.mayada1994.moviewatchlist_mvi.interactors.MainInteractor
import com.mayada1994.moviewatchlist_mvi.rules.RxImmediateSchedulerRule
import com.mayada1994.moviewatchlist_mvi.states.MainState
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainInteractorTest {

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    private lateinit var interactor: MainInteractor

    @Before
    fun setup() {
        interactor = MainInteractor()
    }

    @After
    fun clear() {
        unmockkAll()
    }

    /**
     * When:
     * - getSelectedMenuItem is called with R.id.watchlist_menu_item as itemId
     * Then should:
     * - return MainState.ScreenState with WatchlistFragment::class.java as fragmentClass and 0 as selectedMenuItemId
     */
    @Test
    fun check_getSelectedMenuItem_WatchlistFragment() {
        //Given
        val itemId = R.id.watchlist_menu_item

        val mainState = MainState.ScreenState(
            fragmentClass = WatchlistFragment::class.java,
            selectedMenuItemId = 0
        )

        //When
        val result = interactor.getSelectedMenuItem(itemId)

        //Then
        result.test().assertResult(mainState)
    }

    /**
     * When:
     * - getSelectedMenuItem is called with R.id.popular_menu_item as itemId
     * Then should:
     * - return MainState.ScreenState with MoviesFragment::class.java as fragmentClass, MovieType.POPULAR as argument and 1 as selectedMenuItemId
     */
    @Test
    fun check_getSelectedMenuItem_MoviesFragment_Popular() {
        //Given
        val itemId = R.id.popular_menu_item

        val mainState = MainState.ScreenState(
            fragmentClass = MoviesFragment::class.java,
            args = MoviesFragment.MOVIE_TYPE to MoviesFragment.MovieType.POPULAR.name,
            selectedMenuItemId = 1
        )

        //When
        val result = interactor.getSelectedMenuItem(itemId)

        //Then
        result.test().assertResult(mainState)
    }

    /**
     * When:
     * - getSelectedMenuItem is called with R.id.upcoming_menu_item as itemId
     * Then should:
     * - return MainState.ScreenState with MoviesFragment::class.java as fragmentClass, MovieType.UPCOMING as argument and 2 as selectedMenuItemId
     */
    @Test
    fun check_getSelectedMenuItem_MoviesFragment_Upcoming() {
        //Given
        val itemId = R.id.upcoming_menu_item

        val mainState = MainState.ScreenState(
            fragmentClass = MoviesFragment::class.java,
            args = MoviesFragment.MOVIE_TYPE to MoviesFragment.MovieType.UPCOMING.name,
            selectedMenuItemId = 2
        )

        //When
        val result = interactor.getSelectedMenuItem(itemId)

        //Then
        result.test().assertResult(mainState)
    }

    /**
     * When:
     * - getSelectedMenuItem is called with R.id.list_item as itemId
     * Then should:
     * - return MainState.ErrorState with R.string.general_error_message as resId
     */
    @Test
    fun check_getSelectedMenuItem_Other() {
        //Given
        val itemId = R.id.list_item

        val mainState = MainState.ErrorState(R.string.general_error_message)

        //When
        val result = interactor.getSelectedMenuItem(itemId)

        //Then
        result.test().assertResult(mainState)
    }

}