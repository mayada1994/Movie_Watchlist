package com.mayada1994.moviewatchlist_mvvm.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.mayada1994.moviewatchlist_mvvm.R
import com.mayada1994.moviewatchlist_mvvm.entities.SelectedScreen
import com.mayada1994.moviewatchlist_mvvm.fragments.MoviesFragment
import com.mayada1994.moviewatchlist_mvvm.fragments.MoviesFragment.MovieType
import com.mayada1994.moviewatchlist_mvvm.fragments.WatchlistFragment
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

    private val observerSelectedScreen: Observer<SelectedScreen> = mockk()
    private val observerToastMessageStringResId: Observer<Int> = mockk()

    private lateinit var mainViewModel: MainViewModel

    @Before
    fun setup() {
        mainViewModel = MainViewModel()
        mainViewModel.selectedScreen.observeForever(observerSelectedScreen)
        mainViewModel.toastMessageStringResId.observeForever(observerToastMessageStringResId)
        every { observerSelectedScreen.onChanged(any()) } just Runs
        every { observerToastMessageStringResId.onChanged(any()) } just Runs
    }

    @After
    fun clear() {
        unmockkAll()
    }

    /**
     * When:
     * - onMenuItemSelected is called with R.id.watchlist_menu_item as itemId
     * Then should:
     * - post selectedScreen in mainViewModel with SelectedScreen where fragment class is WatchlistFragment and selectedMenuItemId is 0
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
            observerSelectedScreen.onChanged(
                SelectedScreen(
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
     * - post selectedScreen in mainViewModel with SelectedScreen where fragment class is MoviesFragment, argument is MovieType.POPULAR and selectedMenuItemId is 1
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
            observerSelectedScreen.onChanged(
                SelectedScreen(
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
     * - post selectedScreen in mainViewModel with SelectedScreen where fragment class is MoviesFragment, argument is MovieType.UPCOMING and selectedMenuItemId is 2
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
            observerSelectedScreen.onChanged(
                SelectedScreen(
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
     * - post toastMessageStringResId in mainViewModel with resId as R.string.general_error_message
     */
    @Test
    fun check_onMenuItemSelected_Other() {
        //Given
        val itemId = R.id.list_item

        //When
        mainViewModel.onMenuItemSelected(itemId)

        //Then
        verify {
            observerToastMessageStringResId.onChanged(
                R.string.general_error_message
            )
        }
    }

}