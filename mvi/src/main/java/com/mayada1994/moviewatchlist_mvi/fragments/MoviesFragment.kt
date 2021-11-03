package com.mayada1994.moviewatchlist_mvi.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.mayada1994.moviewatchlist_mvi.activities.MainActivity
import com.mayada1994.moviewatchlist_mvi.adapters.MoviesAdapter
import com.mayada1994.moviewatchlist_mvi.databinding.DialogEditWatchlistBinding
import com.mayada1994.moviewatchlist_mvi.databinding.FragmentMoviesBinding
import com.mayada1994.moviewatchlist_mvi.di.WatchlistComponent
import com.mayada1994.moviewatchlist_mvi.entities.Movie
import com.mayada1994.moviewatchlist_mvi.interactors.MoviesInteractor
import com.mayada1994.moviewatchlist_mvi.presenters.MoviesPresenter
import com.mayada1994.moviewatchlist_mvi.states.MoviesState
import com.mayada1994.moviewatchlist_mvi.views.MoviesView
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class MoviesFragment : Fragment(), MoviesView {

    enum class MovieType {
        POPULAR,
        UPCOMING
    }

    private lateinit var binding: FragmentMoviesBinding

    private var movieType: MovieType? = null

    private lateinit var presenter: MoviesPresenter

    private val movieSubject: PublishSubject<Movie> = PublishSubject.create()

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

        presenter = MoviesPresenter(MoviesInteractor(WatchlistComponent.moviesRepository))
        presenter.bind(this)
    }


    override fun render(state: MoviesState) {
        when(state) {
            is MoviesState.DataState -> renderDataState(state.movies)

            is MoviesState.LoadingState -> renderLoadingState()

            is MoviesState.EmptyState -> renderEmptyState()

            is MoviesState.CompletedState -> renderCompletedState(state.resId)

            is MoviesState.ErrorState -> renderErrorState(state.resId)
        }
    }

    override fun displayMoviesIntent(): Observable<MovieType> {
        return Observable.create { emitter ->
            movieType?.let { emitter.onNext(it) }
        }
    }

    override fun addMovieToWatchlistIntent(): Observable<Movie> = movieSubject

    private fun renderDataState(movies: List<Movie>) {
        showPlaceholder(false)
        showProgress(false)

        binding.moviesRecyclerView.adapter = MoviesAdapter(
            movies,
            object : MoviesAdapter.OnMovieClickListener {
                override fun onClick(movie: Movie) {
                    showAddMovieDialog(movie)
                }
            }
        )
    }

    private fun renderLoadingState() {
        showProgress(true)
    }

    private fun renderEmptyState() {
        showProgress(false)
        showPlaceholder(true)
    }

    private fun renderCompletedState(@StringRes resId: Int) {
        showProgress(false)
        showToast(resId)
    }

    private fun renderErrorState(@StringRes resId: Int) {
        showPlaceholder(true)
        showProgress(false)
        showToast(resId)
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
                movieSubject.onNext(movie)
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

    override fun onStop() {
        super.onStop()
        presenter.unbind()
    }

    companion object {
        const val MOVIE_TYPE = "MOVIE_TYPE"
    }

}