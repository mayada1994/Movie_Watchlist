package com.mayada1994.moviewatchlist_hybrid.events

import androidx.fragment.app.Fragment
import com.mayada1994.moviewatchlist_hybrid.events.ViewEvent

sealed class MainEvent {
    data class ShowSelectedScreen(
        val fragmentClass: Class<out Fragment>,
        val args: Pair<String, String>? = null,
        val selectedMenuItemId: Int
    ) : ViewEvent
}