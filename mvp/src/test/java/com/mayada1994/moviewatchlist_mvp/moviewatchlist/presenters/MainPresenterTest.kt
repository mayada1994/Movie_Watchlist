package com.mayada1994.moviewatchlist_mvp.moviewatchlist.presenters

import com.mayada1994.moviewatchlist_mvp.R
import com.mayada1994.moviewatchlist_mvp.contracts.MainContract
import com.mayada1994.moviewatchlist_mvp.fragments.MoviesFragment
import com.mayada1994.moviewatchlist_mvp.fragments.WatchlistFragment
import com.mayada1994.moviewatchlist_mvp.presenters.MainPresenter
import com.mayada1994.moviewatchlist_mvp.rules.RxImmediateSchedulerRule
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainPresenterTest {

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    private val viewInterface: MainContract.ViewInterface = mockk()

    private lateinit var mainPresenter: MainPresenter

    @Before
    fun setup() {
        mainPresenter = MainPresenter(viewInterface)
    }

    @After
    fun clear() {
        unmockkAll()
    }

    /**
     * When:
     * - onMenuItemSelected is called with R.id.watchlist_menu_item as itemId
     * Then should:
     * - call showSelectedScreen in viewInterface with instance of WatchlistFragment
     */
    @Test
    fun check_onMenuItemSelected_WatchlistFragment() {
        //Given
        val itemId = R.id.watchlist_menu_item
        val fragmentClass = WatchlistFragment::class.java
        every { viewInterface.showSelectedScreen(fragmentClass, null, 0) } just Runs

        //When
        mainPresenter.onMenuItemSelected(itemId)

        //Then
        verify {
            viewInterface.showSelectedScreen(fragmentClass = fragmentClass, selectedMenuItemId = 0)
        }
    }

    /**
     * When:
     * - onMenuItemSelected is called with R.id.popular_menu_item as itemId
     * Then should:
     * - call showSelectedScreen in viewInterface with instance of MoviesFragment with MovieType.POPULAR as argument
     */
    @Test
    fun check_onMenuItemSelected_MoviesFragment_Popular() {
        //Given
        val itemId = R.id.popular_menu_item
        val movieType = MoviesFragment.MovieType.POPULAR.name
        val fragmentClass = MoviesFragment::class.java
        every { viewInterface.showSelectedScreen(fragmentClass, MoviesFragment.MOVIE_TYPE to movieType, 1) } just Runs

        //When
        mainPresenter.onMenuItemSelected(itemId)

        //Then
        verify {
            viewInterface.showSelectedScreen(fragmentClass = fragmentClass, args = MoviesFragment.MOVIE_TYPE to movieType, selectedMenuItemId = 1)
        }
    }

    /**
     * When:
     * - onMenuItemSelected is called with R.id.upcoming_menu_item as itemId
     * Then should:
     * - call showSelectedScreen in viewInterface with instance of MoviesFragment with MovieType.UPCOMING as argument
     */
    @Test
    fun check_onMenuItemSelected_MoviesFragment_Upcoming() {
        //Given
        val itemId = R.id.upcoming_menu_item
        val movieType = MoviesFragment.MovieType.UPCOMING.name
        val fragmentClass = MoviesFragment::class.java
        every { viewInterface.showSelectedScreen(fragmentClass, MoviesFragment.MOVIE_TYPE to movieType, 2) } just Runs

        //When
        mainPresenter.onMenuItemSelected(itemId)

        //Then
        verify {
            viewInterface.showSelectedScreen(fragmentClass = fragmentClass, args = MoviesFragment.MOVIE_TYPE to movieType, selectedMenuItemId = 2)
        }
    }

    /**
     * When:
     * - onMenuItemSelected is called with other itemId
     * Then should:
     * - not call showSelectedScreen in viewInterface
     */
    @Test
    fun check_onMenuItemSelected_Other() {
        //Given
        val itemId = R.id.list_item

        //When
        mainPresenter.onMenuItemSelected(itemId)

        //Then
        verify(exactly = 0) {
            viewInterface.showSelectedScreen(fragmentClass = any(), args = any(), selectedMenuItemId =any())
        }
    }

}