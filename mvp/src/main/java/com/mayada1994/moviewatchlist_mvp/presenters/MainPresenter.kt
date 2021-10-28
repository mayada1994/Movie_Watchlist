package com.mayada1994.moviewatchlist_mvp.presenters

import com.mayada1994.moviewatchlist_mvp.R
import com.mayada1994.moviewatchlist_mvp.contracts.MainContract
import com.mayada1994.moviewatchlist_mvp.fragments.MoviesFragment
import com.mayada1994.moviewatchlist_mvp.fragments.MoviesFragment.MovieType
import com.mayada1994.moviewatchlist_mvp.fragments.WatchlistFragment
import timber.log.Timber

class MainPresenter(
    private val viewInterface: MainContract.ViewInterface
) : MainContract.PresenterInterface {

    override fun onMenuItemSelected(itemId: Int) {
        when (itemId) {
            R.id.watchlist_menu_item -> viewInterface.showSelectedScreen(
                fragmentClass = WatchlistFragment::class.java,
                selectedMenuItemId = 0
            )

            R.id.popular_menu_item -> viewInterface.showSelectedScreen(
                fragmentClass = MoviesFragment::class.java,
                args = MoviesFragment.MOVIE_TYPE to MovieType.POPULAR.name,
                selectedMenuItemId = 1
            )

            R.id.upcoming_menu_item -> {
                viewInterface.showSelectedScreen(
                    fragmentClass = MoviesFragment::class.java,
                    args = MoviesFragment.MOVIE_TYPE to MovieType.UPCOMING.name,
                    selectedMenuItemId = 2
                )
            }

            else -> Timber.e("Unknown menu item")
        }
    }

}