package com.mayada1994.moviewatchlist_mvp.contracts

import androidx.annotation.IdRes
import androidx.fragment.app.Fragment

class MainContract {

    interface PresenterInterface {
        fun onMenuItemSelected(@IdRes itemId: Int)
    }

    interface ViewInterface {
        fun showSelectedScreen(fragmentClass: Class<out Fragment>, args: Pair<String, String>? = null, selectedMenuItemId: Int)
    }

}