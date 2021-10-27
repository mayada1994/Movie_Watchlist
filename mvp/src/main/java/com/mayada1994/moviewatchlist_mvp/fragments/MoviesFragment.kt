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
import com.mayada1994.moviewatchlist_mvp.adapters.MoviesAdapter
import com.mayada1994.moviewatchlist_mvp.contracts.MoviesContract
import com.mayada1994.moviewatchlist_mvp.databinding.DialogEditWatchlistBinding
import com.mayada1994.moviewatchlist_mvp.databinding.FragmentMoviesBinding
import com.mayada1994.moviewatchlist_mvp.di.WatchlistComponent
import com.mayada1994.moviewatchlist_mvp.entities.Movie
import com.mayada1994.moviewatchlist_mvp.models.LocalDataSource
import com.mayada1994.moviewatchlist_mvp.models.RemoteDataSource
import com.mayada1994.moviewatchlist_mvp.presenters.MoviesPresenter

class MoviesFragment : Fragment(), MoviesContract.ViewInterface {

    enum class MovieType {
        POPULAR,
        UPCOMING
    }

    private lateinit var binding: FragmentMoviesBinding

    private var movieType: MovieType? = null

    private lateinit var presenter: MoviesPresenter

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

        presenter = MoviesPresenter(
            this,
            LocalDataSource(WatchlistComponent.movieDao),
            RemoteDataSource(WatchlistComponent.moviesService, getString(R.string.api_key))
        )

        movieType?.let { presenter.init(it) }
    }

    override fun setMoviesList(movies: List<Movie>) {
        binding.moviesRecyclerView.adapter = MoviesAdapter(
            movies,
            object : MoviesAdapter.OnMovieClickListener {
                override fun onClick(movie: Movie) {
                    showAddMovieDialog(movie)
                }
            }
        )
    }

    override fun showPlaceholder(isVisible: Boolean) {
        binding.imgPlaceholder.isVisible = isVisible
    }

    private fun showAddMovieDialog(movie: Movie) {
        val dialogView = DialogEditWatchlistBinding.inflate(layoutInflater)
        val alertDialog = AlertDialog.Builder(requireContext()).setView(dialogView.root).create()

        with(dialogView) {
            btnOk.setOnClickListener {
                alertDialog.dismiss()
                presenter.addMovieToWatchlist(movie)
            }
            btnCancel.setOnClickListener {
                alertDialog.dismiss()
            }
        }
        alertDialog.show()
    }

    override fun showProgress(isProgressVisible: Boolean) {
        (requireActivity() as MainActivity).showProgress(isProgressVisible)
    }

    override fun showToast(resId: Int) {
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }

    companion object {
        const val MOVIE_TYPE = "MOVIE_TYPE"

        @JvmStatic
        fun newInstance(movieType: MovieType) =
            MoviesFragment().apply {
                arguments = Bundle().apply {
                    putString(MOVIE_TYPE, movieType.name)
                }
            }
    }
}