package com.mayada1994.moviewatchlist_mvi.views

import com.mayada1994.moviewatchlist_mvi.states.MainState
import io.reactivex.Observable

interface MainView {

    fun render(state: MainState)
    fun selectMenuItemIntent(): Observable<Int>

}