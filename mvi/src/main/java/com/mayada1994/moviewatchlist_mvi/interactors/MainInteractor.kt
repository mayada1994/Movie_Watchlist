package com.mayada1994.moviewatchlist_mvi.interactors

import com.mayada1994.moviewatchlist_mvi.R
import com.mayada1994.moviewatchlist_mvi.fragments.MoviesFragment
import com.mayada1994.moviewatchlist_mvi.fragments.MoviesFragment.MovieType
import com.mayada1994.moviewatchlist_mvi.states.MainState
import io.reactivex.Observable

class MainInteractor {

    fun getSelectedMenuItem(itemId: Int): Observable<MainState> {
        return Observable.just(
            when (itemId) {
//                R.id.watchlist_menu_item -> MainState.ScreenState(
//                    fragmentClass = WatchlistFragment::class.java,
//                    selectedMenuItemId = 0
//                )
//
                R.id.popular_menu_item -> MainState.ScreenState(
                    fragmentClass = MoviesFragment::class.java,
                    args = MoviesFragment.MOVIE_TYPE to MovieType.POPULAR.name,
                    selectedMenuItemId = 1
                )

                R.id.upcoming_menu_item -> {
                    MainState.ScreenState(
                        fragmentClass = MoviesFragment::class.java,
                        args = MoviesFragment.MOVIE_TYPE to MovieType.UPCOMING.name,
                        selectedMenuItemId = 2
                    )
                }

                else -> MainState.ErrorState(R.string.general_error_message)
            }
        )
    }

}