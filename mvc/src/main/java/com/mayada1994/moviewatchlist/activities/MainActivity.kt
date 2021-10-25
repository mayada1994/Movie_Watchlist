package com.mayada1994.moviewatchlist.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.mayada1994.moviewatchlist.R
import com.mayada1994.moviewatchlist.databinding.ActivityMainBinding
import com.mayada1994.moviewatchlist.fragments.MoviesFragment
import com.mayada1994.moviewatchlist.fragments.MoviesFragment.MovieType
import com.mayada1994.moviewatchlist.fragments.WatchlistFragment
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

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
                when (menuItem.itemId) {
                    R.id.watchlist_menu_item -> {
                        selectedMenuItemId = 0
                        clearFragments()
                        setFragmentWithoutAddingToBackStack(WatchlistFragment())
                    }
                    R.id.popular_menu_item -> {
                        selectedMenuItemId = 1
                        clearFragments()
                        setFragmentWithoutAddingToBackStack(MoviesFragment.newInstance(MovieType.POPULAR))
                    }
                    R.id.upcoming_menu_item -> {
                        selectedMenuItemId = 2
                        clearFragments()
                        setFragmentWithoutAddingToBackStack(MoviesFragment.newInstance(MovieType.UPCOMING))
                    }
                    else -> Timber.e("Unknown menu item")
                }
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

}