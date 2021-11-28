package com.mayada1994.moviewatchlist_hybrid.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.mayada1994.moviewatchlist_hybrid.events.ViewEvent
import com.mayada1994.moviewatchlist_hybrid.utils.SingleLiveEvent

abstract class BaseViewModel: ViewModel() {

    private val _event = SingleLiveEvent<ViewEvent>()
    val event: LiveData<ViewEvent>
        get() = _event

    fun setEvent(event: ViewEvent) {
        _event.value = event
    }

}