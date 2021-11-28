package com.mayada1994.moviewatchlist_mvi.states

import androidx.annotation.StringRes
import androidx.fragment.app.Fragment

sealed class MainState {

    data class ScreenState(
        val fragmentClass: Class<out Fragment>,
        val args: Pair<String, String>? = null,
        val selectedMenuItemId: Int
    ) : MainState()

    data class ErrorState(@StringRes val resId: Int) : MainState()

}
