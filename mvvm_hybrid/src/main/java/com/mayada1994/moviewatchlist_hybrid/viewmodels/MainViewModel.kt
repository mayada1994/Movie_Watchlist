package com.mayada1994.moviewatchlist_hybrid.viewmodels

import com.mayada1994.moviewatchlist_hybrid.R
import com.mayada1994.moviewatchlist_hybrid.events.BaseEvent
import com.mayada1994.moviewatchlist_hybrid.events.MainEvent
import com.mayada1994.moviewatchlist_hybrid.fragments.MoviesFragment
import com.mayada1994.moviewatchlist_hybrid.fragments.MoviesFragment.MovieType
import com.mayada1994.moviewatchlist_hybrid.fragments.WatchlistFragment

class MainViewModel : BaseViewModel() {

    fun onMenuItemSelected(itemId: Int) {
        setEvent(
            when (itemId) {
                R.id.watchlist_menu_item -> MainEvent.ShowSelectedScreen(
                    fragmentClass = WatchlistFragment::class.java,
                    selectedMenuItemId = 0
                )

                R.id.popular_menu_item -> MainEvent.ShowSelectedScreen(
                    fragmentClass = MoviesFragment::class.java,
                    args = MoviesFragment.MOVIE_TYPE to MovieType.POPULAR.name,
                    selectedMenuItemId = 1
                )

                R.id.upcoming_menu_item -> {
                    MainEvent.ShowSelectedScreen(
                        fragmentClass = MoviesFragment::class.java,
                        args = MoviesFragment.MOVIE_TYPE to MovieType.UPCOMING.name,
                        selectedMenuItemId = 2
                    )
                }
                else -> BaseEvent.ShowMessage(R.string.general_error_message)
            }
        )
    }

}