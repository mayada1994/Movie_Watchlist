package com.mayada1994.moviewatchlist_mvvm.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.mayada1994.moviewatchlist_mvvm.R
import com.mayada1994.moviewatchlist_mvvm.activities.MainActivity
import com.mayada1994.moviewatchlist_mvvm.adapters.WatchlistAdapter
import com.mayada1994.moviewatchlist_mvvm.databinding.DialogEditWatchlistBinding
import com.mayada1994.moviewatchlist_mvvm.databinding.FragmentWatchlistBinding
import com.mayada1994.moviewatchlist_mvvm.di.WatchlistComponent
import com.mayada1994.moviewatchlist_mvvm.entities.Movie
import com.mayada1994.moviewatchlist_mvvm.viewmodels.WatchlistViewModel

class WatchlistFragment : Fragment() {

    private lateinit var binding: FragmentWatchlistBinding

    private val viewModel by viewModels<WatchlistViewModel> { WatchlistComponent.viewModelFactory }

    private var deleteDialog: AlertDialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWatchlistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListeners()
        setObservers()
    }

    override fun onResume() {
        super.onResume()

        viewModel.init()
    }

    private fun setListeners() {
        binding.fab.setOnClickListener { viewModel.onFloatingActionButtonClick() }
    }

    private fun setObservers() {
        viewModel.moviesList.observe(viewLifecycleOwner, { movies ->
            setMoviesList(movies)
        })

        viewModel.floatingActionButtonImage.observe(viewLifecycleOwner, { resId ->
            setFloatingActionButtonImage(resId)
        })

        viewModel.navigateToSearchScreen.observe(viewLifecycleOwner, {
            goToSearchScreen()
        })

        viewModel.showDeleteMoviesDialog.observe(viewLifecycleOwner, {
            showDeleteMoviesDialog()
        })

        viewModel.updateMoviesList.observe(viewLifecycleOwner, { movies ->
            updateMovies(movies)
        })

        viewModel.isProgressVisible.observe(viewLifecycleOwner, { isProgressVisible ->
            showProgress(isProgressVisible)
        })

        viewModel.isPlaceholderVisible.observe(viewLifecycleOwner, { isPlaceholderVisible ->
            showPlaceholder(isPlaceholderVisible)
        })

        viewModel.toastMessageStringResId.observe(viewLifecycleOwner, { resId ->
            showToast(resId)
        })
    }

    private fun setMoviesList(movies: List<Movie>) {
        binding.moviesRecyclerView.adapter = WatchlistAdapter(
            ArrayList(movies),
            object : WatchlistAdapter.OnWatchlistItemSelectListener {
                override fun onItemSelect(item: Movie, checked: Boolean) {
                    viewModel.onMovieItemChecked(item, checked)
                }

                override fun checkMoviesList(movies: List<Movie>) {
                    viewModel.checkMoviesList(movies)
                }
            }
        )
    }

    private fun updateMovies(selectedMovies: List<Movie>) {
        (binding.moviesRecyclerView.adapter as WatchlistAdapter).updateItems(selectedMovies.toList())
    }

    private fun showDeleteMoviesDialog() {
        if (deleteDialog == null) {
            val dialogView = DialogEditWatchlistBinding.inflate(layoutInflater)
            val alertDialog =
                AlertDialog.Builder(requireContext()).setView(dialogView.root).create()
            deleteDialog = alertDialog
            with(dialogView) {
                txtPrompt.text = getString(R.string.dialog_delete_movie_prompt_message)
                btnOk.setOnClickListener {
                    alertDialog.dismiss()
                    viewModel.deleteMovies()
                }
                btnCancel.setOnClickListener {
                    alertDialog.dismiss()
                }
            }
            alertDialog.setOnDismissListener { deleteDialog = null }
            alertDialog.show()
        }
    }

    private fun goToSearchScreen() {
        (requireActivity() as MainActivity).setFragment(SearchFragment())
    }

    private fun showPlaceholder(isVisible: Boolean) {
        binding.layoutEmptyList.isVisible = isVisible
    }

    private fun showProgress(isProgressVisible: Boolean) {
        (requireActivity() as MainActivity).showProgress(isProgressVisible)
    }

    private fun showToast(resId: Int) {
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show()
    }

    private fun setFloatingActionButtonImage(resId: Int) {
        binding.fab.setImageResource(resId)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
    }

}