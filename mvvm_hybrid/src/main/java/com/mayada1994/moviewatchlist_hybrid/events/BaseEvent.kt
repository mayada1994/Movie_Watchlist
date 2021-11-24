package com.mayada1994.moviewatchlist_hybrid.events

import androidx.annotation.StringRes
import com.mayada1994.moviewatchlist_hybrid.events.ViewEvent

sealed class BaseEvent {

    data class ShowProgress(val isProgressVisible: Boolean): ViewEvent

    data class ShowPlaceholder(val isVisible: Boolean): ViewEvent

    data class ShowMessage(@StringRes val resId: Int): ViewEvent

}