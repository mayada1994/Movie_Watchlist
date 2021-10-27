package com.mayada1994.moviewatchlist_mvp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.mayada1994.moviewatchlist_mvp.R
import com.mayada1994.moviewatchlist_mvp.contracts.MainContract
import com.mayada1994.moviewatchlist_mvp.databinding.ActivityMainBinding
import com.mayada1994.moviewatchlist_mvp.fragments.WatchlistFragment
import com.mayada1994.moviewatchlist_mvp.presenters.MainPresenter

class MainActivity : AppCompatActivity(), MainContract.ViewInterface {

    private lateinit var binding: ActivityMainBinding

    private val presenter = MainPresenter(this)

    private var selectedMenuItemId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setMenu()
    }

    override fun onResume() {
        super.onResume()

        binding.navigationView.menu.getItem(selectedMenuItemId)?.isChecked = true
    }

    private fun setMenu() {
        with(binding) {
            navigationView.menu.getItem(selectedMenuItemId)?.isChecked = true
            setFragmentWithoutAddingToBackStack(WatchlistFragment())

            navigationView.setOnItemSelectedListener { menuItem ->
                presenter.onMenuItemSelected(menuItem.itemId)
                true
            }
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

    override fun showSelectedScreen(fragment: Fragment, selectedMenuItemId: Int) {
        this.selectedMenuItemId = selectedMenuItemId
        clearFragments()
        setFragmentWithoutAddingToBackStack(fragment)
    }

}