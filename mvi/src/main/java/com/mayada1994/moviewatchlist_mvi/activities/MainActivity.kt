package com.mayada1994.moviewatchlist_mvi.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.mayada1994.moviewatchlist_mvi.R
import com.mayada1994.moviewatchlist_mvi.databinding.ActivityMainBinding
import com.mayada1994.moviewatchlist_mvi.interactors.MainInteractor
import com.mayada1994.moviewatchlist_mvi.presenters.MainPresenter
import com.mayada1994.moviewatchlist_mvi.states.MainState
import com.mayada1994.moviewatchlist_mvi.views.MainView
import io.reactivex.Observable

class MainActivity : AppCompatActivity(), MainView {

    private lateinit var binding: ActivityMainBinding

    private lateinit var presenter: MainPresenter

    private var selectedMenuItemId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        presenter = MainPresenter(MainInteractor())
        presenter.bind(this)

        setMenu()
    }

    override fun render(state: MainState) {
        when (state) {
            is MainState.ScreenState -> renderScreenState(
                state.fragmentClass,
                state.args,
                state.selectedMenuItemId
            )

            is MainState.ErrorState -> renderErrorState(state.resId)
        }
    }

    override fun selectMenuItemIntent(): Observable<Int> {
        val observable = Observable.create<Int> { emitter ->
            binding.navigationView.setOnItemSelectedListener { menuItem ->
                emitter.onNext(menuItem.itemId)
                true
            }
        }
        return observable
    }

    private fun setMenu() {
        with(binding) {
            navigationView.menu.getItem(selectedMenuItemId)?.isChecked = true
//            setFragmentWithoutAddingToBackStack(WatchlistFragment())
        }
    }

    fun setFragment(fragment: Fragment) {
        val current = getCurrentFragment()
        if (current != null && fragment.javaClass == current.javaClass) {
            return
        }
        supportFragmentManager.commit {
            addToBackStack(null)
            replace(R.id.container, fragment, fragment.javaClass.simpleName)
        }
    }

    private fun setFragmentWithoutAddingToBackStack(fragment: Fragment) {
        supportFragmentManager.commit {
            replace(R.id.container, fragment, fragment.javaClass.simpleName)
        }
    }

    private fun getCurrentFragment(): Fragment? {
        return supportFragmentManager.findFragmentById(R.id.container)
    }

    private fun clearFragments() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            for (index in 0 until supportFragmentManager.backStackEntryCount) {
                supportFragmentManager.popBackStack()
            }
        }
    }

    fun showProgress(visible: Boolean) {
        binding.progressBar.isVisible = visible
    }

    private fun renderScreenState(
        fragmentClass: Class<out Fragment>,
        args: Pair<String, String>?,
        selectedMenuItemId: Int
    ) {
        this.selectedMenuItemId = selectedMenuItemId
        clearFragments()
        setFragmentWithoutAddingToBackStack(
            fragmentClass.newInstance().apply {
                args?.run {
                    arguments = Bundle().apply { putString(first, second) }
                }
            }
        )
    }

    private fun renderErrorState(resId: Int) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show()
    }

    override fun onStop() {
        super.onStop()
        presenter.unbind()
    }

}