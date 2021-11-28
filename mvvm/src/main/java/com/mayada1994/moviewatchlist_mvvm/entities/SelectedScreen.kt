package com.mayada1994.moviewatchlist_mvvm.entities

import androidx.fragment.app.Fragment

data class SelectedScreen(
    val fragmentClass: Class<out Fragment>,
    val args: Pair<String, String>? = null,
    val selectedMenuItemId: Int
)