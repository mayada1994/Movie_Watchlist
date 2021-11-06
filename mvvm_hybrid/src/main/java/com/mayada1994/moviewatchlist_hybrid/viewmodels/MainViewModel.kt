package com.mayada1994.moviewatchlist_hybrid.viewmodels

import androidx.fragment.app.Fragment
import com.mayada1994.moviewatchlist_hybrid.R
import com.mayada1994.moviewatchlist_hybrid.fragments.MoviesFragment
import com.mayada1994.moviewatchlist_hybrid.fragments.MoviesFragment.MovieType
import com.mayada1994.moviewatchlist_hybrid.fragments.WatchlistFragment
import com.mayada1994.moviewatchlist_hybrid.utils.ViewEvent

class MainViewModel : BaseViewModel() {

    sealed class MainEvent {
        data class ShowSelectedScreen(
            val fragmentClass: Class<out Fragment>,
            val args: Pair<String, String>? = null,
            val selectedMenuItemId: Int
        ) : ViewEvent
    }

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