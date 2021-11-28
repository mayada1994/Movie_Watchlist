package com.mayada1994.moviewatchlist_mvp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.mayada1994.moviewatchlist_mvp.R
import com.mayada1994.moviewatchlist_mvp.activities.MainActivity
import com.mayada1994.moviewatchlist_mvp.adapters.WatchlistAdapter
import com.mayada1994.moviewatchlist_mvp.contracts.WatchlistContract
import com.mayada1994.moviewatchlist_mvp.databinding.DialogEditWatchlistBinding
import com.mayada1994.moviewatchlist_mvp.databinding.FragmentWatchlistBinding
import com.mayada1994.moviewatchlist_mvp.di.WatchlistComponent
import com.mayada1994.moviewatchlist_mvp.entities.Movie
import com.mayada1994.moviewatchlist_mvp.presenters.WatchlistPresenter

class WatchlistFragment : Fragment(), WatchlistContract.ViewInterface {

    private lateinit var binding: FragmentWatchlistBinding

    private lateinit var presenter: WatchlistPresenter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWatchlistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter = WatchlistPresenter(this, WatchlistComponent.localDataSource)

        setListeners()
    }

    override fun onResume() {
        super.onResume()

        presenter.init()
    }

    private fun setListeners() {
        binding.fab.setOnClickListener { presenter.onFloatingActionButtonClick() }
    }

    override fun setMoviesList(movies: List<Movie>) {
        binding.moviesRecyclerView.adapter = WatchlistAdapter(
            ArrayList(movies),
            object : WatchlistAdapter.OnWatchlistItemSelectListener {
                override fun onItemSelect(item: Movie, checked: Boolean) {
                    presenter.onMovieItemChecked(item, checked)
                }

                override fun checkMoviesList(movies: List<Movie>) {
                    presenter.checkMoviesList(movies)
                }
            }
        )
    }

    override fun updateMovies(selectedMovies: List<Movie>) {
        (binding.moviesRecyclerView.adapter as WatchlistAdapter).updateItems(selectedMovies.toList())
    }

    override fun showDeleteMoviesDialog() {
        val dialogView = DialogEditWatchlistBinding.inflate(layoutInflater)
        val alertDialog = AlertDialog.Builder(requireContext()).setView(dialogView.root).create()

        with(dialogView) {
            txtPrompt.text = getString(R.string.dialog_delete_movie_prompt_message)
            btnOk.setOnClickListener {
                alertDialog.dismiss()
                presenter.deleteMovies()
            }
            btnCancel.setOnClickListener {
                alertDialog.dismiss()
            }
        }
        alertDialog.show()
    }

    override fun goToSearchScreen() {
        (requireActivity() as MainActivity).setFragment(SearchFragment())
    }

    override fun showPlaceholder(isVisible: Boolean) {
        binding.layoutEmptyList.isVisible = isVisible
    }

    override fun showProgress(isProgressVisible: Boolean) {
        (requireActivity() as MainActivity).showProgress(isProgressVisible)
    }

    override fun showToast(resId: Int) {
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show()
    }

    override fun setFloatingActionButtonImage(resId: Int) {
        binding.fab.setImageResource(resId)
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }

}