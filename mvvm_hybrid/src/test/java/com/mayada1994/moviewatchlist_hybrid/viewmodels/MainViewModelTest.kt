package com.mayada1994.moviewatchlist_hybrid.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.mayada1994.moviewatchlist_hybrid.R
import com.mayada1994.moviewatchlist_hybrid.fragments.MoviesFragment
import com.mayada1994.moviewatchlist_hybrid.fragments.MoviesFragment.MovieType
import com.mayada1994.moviewatchlist_hybrid.fragments.WatchlistFragment
import com.mayada1994.moviewatchlist_hybrid.utils.ViewEvent
import com.mayada1994.rules.RxImmediateSchedulerRule
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class MainViewModelTest {

    @Rule @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    @get:Rule var rule: TestRule = InstantTaskExecutorRule()

    private val observerViewEvent: Observer<ViewEvent> = mockk()

    private lateinit var mainViewModel: MainViewModel

    @Before
    fun setup() {
        mainViewModel = MainViewModel()
        mainViewModel.event.observeForever(observerViewEvent)
        every { observerViewEvent.onChanged(any()) } just Runs
    }

    @After
    fun clear() {
        unmockkAll()
    }

    /**
     * When:
     * - onMenuItemSelected is called with R.id.watchlist_menu_item as itemId
     * Then should:
     * - call setEvent in mainViewModel with ShowSelectedScreen where fragment class is WatchlistFragment and selectedMenuItemId is 0
     */
    @Test
    fun check_onMenuItemSelected_WatchlistFragment() {
        //Given
        val itemId = R.id.watchlist_menu_item
        val fragmentClass = WatchlistFragment::class.java

        //When
        mainViewModel.onMenuItemSelected(itemId)

        //Then
        verify {
            observerViewEvent.onChanged(
                MainViewModel.MainEvent.ShowSelectedScreen(
                    fragmentClass = fragmentClass,
                    selectedMenuItemId = 0
                )
            )
        }
    }

    /**
     * When:
     * - onMenuItemSelected is called with R.id.popular_menu_item as itemId
     * Then should:
     * - call setEvent in mainViewModel with ShowSelectedScreen where fragment class is MoviesFragment, argument is MovieType.POPULAR and selectedMenuItemId is 1
     */
    @Test
    fun check_onMenuItemSelected_MoviesFragment_Popular() {
        //Given
        val itemId = R.id.popular_menu_item
        val movieType = MovieType.POPULAR.name
        val fragmentClass = MoviesFragment::class.java

        //When
        mainViewModel.onMenuItemSelected(itemId)

        //Then
        verify {
            observerViewEvent.onChanged(
                MainViewModel.MainEvent.ShowSelectedScreen(
                    fragmentClass = fragmentClass,
                    args = MoviesFragment.MOVIE_TYPE to movieType,
                    selectedMenuItemId = 1
                )
            )
        }
    }

    /**
     * When:
     * - onMenuItemSelected is called with R.id.upcoming_menu_item as itemId
     * Then should:
     * - call setEvent in mainViewModel with ShowSelectedScreen where fragment class is MoviesFragment, argument is MovieType.UPCOMING and selectedMenuItemId is 2
     */
    @Test
    fun check_onMenuItemSelected_MoviesFragment_Upcoming() {
        //Given
        val itemId = R.id.upcoming_menu_item
        val movieType = MovieType.UPCOMING.name
        val fragmentClass = MoviesFragment::class.java

        //When
        mainViewModel.onMenuItemSelected(itemId)

        //Then
        verify {
            observerViewEvent.onChanged(
                MainViewModel.MainEvent.ShowSelectedScreen(
                    fragmentClass = fragmentClass,
                    args = MoviesFragment.MOVIE_TYPE to movieType,
                    selectedMenuItemId = 2
                )
            )
        }
    }

    /**
     * When:
     * - onMenuItemSelected is called with other itemId
     * Then should:
     * - call setEvent in mainViewModel with ShowMessage where resId is R.string.general_error_message
     */
    @Test
    fun check_onMenuItemSelected_Other() {
        //Given
        val itemId = R.id.list_item

        //When
        mainViewModel.onMenuItemSelected(itemId)

        //Then
        verify {
            observerViewEvent.onChanged(
                BaseViewModel.BaseEvent.ShowMessage(R.string.general_error_message)
            )
        }
    }

}