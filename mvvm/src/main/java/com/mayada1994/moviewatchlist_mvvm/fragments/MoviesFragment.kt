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
import com.mayada1994.moviewatchlist_mvvm.activities.MainActivity
import com.mayada1994.moviewatchlist_mvvm.adapters.MoviesAdapter
import com.mayada1994.moviewatchlist_mvvm.databinding.DialogEditWatchlistBinding
import com.mayada1994.moviewatchlist_mvvm.databinding.FragmentMoviesBinding
import com.mayada1994.moviewatchlist_mvvm.di.WatchlistComponent
import com.mayada1994.moviewatchlist_mvvm.entities.Movie
import com.mayada1994.moviewatchlist_mvvm.viewmodels.MoviesViewModel

class MoviesFragment : Fragment() {

    enum class MovieType {
        POPULAR,
        UPCOMING
    }

    private lateinit var binding: FragmentMoviesBinding

    private var movieType: MovieType? = null

    private val viewModel by viewModels<MoviesViewModel> { WatchlistComponent.viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.getString(MOVIE_TYPE)?.let {
            movieType = MovieType.valueOf(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMoviesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setObservers()

        movieType?.let { viewModel.init(it) }
    }

    private fun setObservers() {
        viewModel.moviesList.observe(viewLifecycleOwner, { movies ->
            setMoviesList(movies)
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
        binding.moviesRecyclerView.adapter = MoviesAdapter(
            movies,
            object : MoviesAdapter.OnMovieClickListener {
                override fun onClick(movie: Movie) {
                    showAddMovieDialog(movie)
                }
            }
        )
    }

    private fun showPlaceholder(isVisible: Boolean) {
        binding.imgPlaceholder.isVisible = isVisible
    }

    private fun showAddMovieDialog(movie: Movie) {
        val dialogView = DialogEditWatchlistBinding.inflate(layoutInflater)
        val alertDialog = AlertDialog.Builder(requireContext()).setView(dialogView.root).create()

        with(dialogView) {
            btnOk.setOnClickListener {
                alertDialog.dismiss()
                viewModel.addMovieToWatchlist(movie)
            }
            btnCancel.setOnClickListener {
                alertDialog.dismiss()
            }
        }
        alertDialog.show()
    }

    private fun showProgress(isProgressVisible: Boolean) {
        (requireActivity() as MainActivity).showProgress(isProgressVisible)
    }

    private fun showToast(resId: Int) {
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
    }

    companion object {
        const val MOVIE_TYPE = "MOVIE_TYPE"
    }

}