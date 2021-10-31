package com.mayada1994.moviewatchlist_mvvm.viewmodels

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.mayada1994.moviewatchlist_mvvm.utils.SingleLiveEvent
import com.mayada1994.moviewatchlist_mvvm.utils.ViewEvent

abstract class BaseViewModel: ViewModel() {

    sealed class BaseEvent {

        data class ShowProgress(val isProgressVisible: Boolean): ViewEvent

        data class ShowPlaceholder(val isVisible: Boolean): ViewEvent

        data class ShowMessage(@StringRes val resId: Int): ViewEvent

        object OnDestroy: ViewEvent

    }

    private val _event = SingleLiveEvent<ViewEvent>()
    val event: LiveData<ViewEvent>
        get() = _event

    fun setEvent(event: ViewEvent) {
        _event.value = event
    }

}