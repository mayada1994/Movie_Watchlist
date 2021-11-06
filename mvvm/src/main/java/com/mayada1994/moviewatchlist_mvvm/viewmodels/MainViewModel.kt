package com.mayada1994.moviewatchlist_mvvm.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.mayada1994.moviewatchlist_mvvm.R
import com.mayada1994.moviewatchlist_mvvm.entities.SelectedScreen
import com.mayada1994.moviewatchlist_mvvm.fragments.MoviesFragment
import com.mayada1994.moviewatchlist_mvvm.fragments.MoviesFragment.MovieType
import com.mayada1994.moviewatchlist_mvvm.fragments.WatchlistFragment
import com.mayada1994.moviewatchlist_mvvm.utils.SingleLiveEvent

class MainViewModel : ViewModel() {

    private val _selectedScreen = SingleLiveEvent<SelectedScreen>()
    val selectedScreen: LiveData<SelectedScreen>
        get() = _selectedScreen

    private val _toastMessageStringResId = SingleLiveEvent<Int>()
    val toastMessageStringResId: LiveData<Int>
        get() = _toastMessageStringResId

    fun onMenuItemSelected(itemId: Int) {
        when (itemId) {
            R.id.watchlist_menu_item -> _selectedScreen.postValue(
                SelectedScreen(
                    fragmentClass = WatchlistFragment::class.java,
                    selectedMenuItemId = 0
                )
            )

            R.id.popular_menu_item -> _selectedScreen.postValue(
                SelectedScreen(
                    fragmentClass = MoviesFragment::class.java,
                    args = MoviesFragment.MOVIE_TYPE to MovieType.POPULAR.name,
                    selectedMenuItemId = 1
                )
            )

            R.id.upcoming_menu_item -> {
                _selectedScreen.postValue(
                    SelectedScreen(
                        fragmentClass = MoviesFragment::class.java,
                        args = MoviesFragment.MOVIE_TYPE to MovieType.UPCOMING.name,
                        selectedMenuItemId = 2
                    )
                )
            }
            else -> _toastMessageStringResId.postValue(R.string.general_error_message)
        }
    }

}