package com.mayada1994.moviewatchlist_mvi.presenters

import com.mayada1994.moviewatchlist_mvi.interactors.MainInteractor
import com.mayada1994.moviewatchlist_mvi.views.MainView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class MainPresenter(private val mainInteractor: MainInteractor) {
    private val compositeDisposable = CompositeDisposable()
    private lateinit var view: MainView

    fun bind(view: MainView) {
        this.view = view
        compositeDisposable.add(observeSelectMenuItemIntent())
    }

    fun unbind() {
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.dispose()
        }
    }

    private fun observeSelectMenuItemIntent() = view.selectMenuItemIntent()
        .doOnNext { Timber.d("Intent: select menu item $it") }
        .flatMap { mainInteractor.getSelectedMenuItem(it) }
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe { view.render(it) }
}