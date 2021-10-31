package com.mayada1994.moviewatchlist_mvvm.activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import com.mayada1994.moviewatchlist_mvvm.R
import com.mayada1994.moviewatchlist_mvvm.databinding.ActivityMainBinding
import com.mayada1994.moviewatchlist_mvvm.di.WatchlistComponent
import com.mayada1994.moviewatchlist_mvvm.viewmodels.BaseViewModel.BaseEvent
import com.mayada1994.moviewatchlist_mvvm.viewmodels.MainViewModel
import com.mayada1994.moviewatchlist_mvvm.viewmodels.MainViewModel.MainEvent

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel by viewModels<MainViewModel> { WatchlistComponent.viewModelFactory }

    private var selectedMenuItemId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setObservers()

        setMenu()
    }

    override fun onResume() {
        super.onResume()

        binding.navigationView.menu.getItem(selectedMenuItemId)?.isChecked = true
    }

    private fun setObservers() {
        viewModel.event.observe(this, Observer { event ->
            when (event) {
                is MainEvent.ShowSelectedScreen -> showSelectedScreen(
                    event.fragmentClass,
                    event.args,
                    event.selectedMenuItemId
                )

                is BaseEvent.ShowMessage -> Toast.makeText(this, event.resId, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun setMenu() {
        with(binding) {
            navigationView.menu.getItem(selectedMenuItemId)?.isChecked = true
//            setFragmentWithoutAddingToBackStack(WatchlistFragment())

            navigationView.setOnItemSelectedListener { menuItem ->
                viewModel.onMenuItemSelected(menuItem.itemId)
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

    private fun showSelectedScreen(
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

}