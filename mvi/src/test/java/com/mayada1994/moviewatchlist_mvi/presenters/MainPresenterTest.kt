package com.mayada1994.moviewatchlist_mvi.presenters

import com.mayada1994.moviewatchlist_mvi.R
import com.mayada1994.moviewatchlist_mvi.fragments.WatchlistFragment
import com.mayada1994.moviewatchlist_mvi.interactors.MainInteractor
import com.mayada1994.moviewatchlist_mvi.states.MainState
import com.mayada1994.moviewatchlist_mvi.views.MainView
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

class MainPresenterTest {

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    private val view: MainView = mockk(relaxed = true)

    private val interactor: MainInteractor = mockk()

    private lateinit var mainPresenter: MainPresenter

    @Before
    fun setup() {
        mainPresenter = MainPresenter(interactor)
        mainPresenter.bind(view)
    }

    @After
    fun clear() {
        mainPresenter.unbind()
        unmockkAll()
    }

    /**
     * Given:
     * - selectMenuItemIntent in view returns observable with R.id.watchlist_menu_item
     * - some main state
     * - getSelectedMenuItem with R.id.watchlist_menu_item in interactor returns main state
     * When:
     * - observeSelectMenuItemIntent is called
     * Then should:
     * - call getSelectedMenuItem with R.id.watchlist_menu_item in interactor
     * - call render with main state in view
     */
    @Test
    fun check_observeSelectMenuItemIntent() {
        //Given
        val mainState = MainState.ScreenState(
            fragmentClass = WatchlistFragment::class.java,
            selectedMenuItemId = 0
        )
        every { view.selectMenuItemIntent() } returns Observable.just(R.id.watchlist_menu_item)
        every { interactor.getSelectedMenuItem(R.id.watchlist_menu_item) } returns Observable.just(mainState)

        //When
        mainPresenter.javaClass.getDeclaredMethod("observeSelectMenuItemIntent").run {
            isAccessible = true
            invoke(mainPresenter)
        }

        //Then
        verify {
            interactor.getSelectedMenuItem(R.id.watchlist_menu_item)
            view.render(mainState)
        }
    }

}