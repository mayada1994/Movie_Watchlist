package com.mayada1994.moviewatchlist_mvp.presenters

import com.mayada1994.moviewatchlist_mvp.R
import com.mayada1994.moviewatchlist_mvp.contracts.MainContract
import com.mayada1994.moviewatchlist_mvp.fragments.MoviesFragment
import com.mayada1994.moviewatchlist_mvp.fragments.MoviesFragment.MovieType
import timber.log.Timber

class MainPresenter(
    private val viewInterface: MainContract.ViewInterface
) : MainContract.PresenterInterface {

    override fun onMenuItemSelected(itemId: Int) {
        //TODO: Add fragments and uncomment
        when (itemId) {
//            R.id.watchlist_menu_item -> viewInterface.showSelectedScreen(WatchlistFragment(), 0)

            R.id.popular_menu_item -> viewInterface.showSelectedScreen(
                MoviesFragment.newInstance(
                    MovieType.POPULAR
                ), 1
            )

            R.id.upcoming_menu_item -> viewInterface.showSelectedScreen(
                MoviesFragment.newInstance(
                    MovieType.UPCOMING
                ), 2
            )

            else -> Timber.e("Unknown menu item")
        }
    }

}