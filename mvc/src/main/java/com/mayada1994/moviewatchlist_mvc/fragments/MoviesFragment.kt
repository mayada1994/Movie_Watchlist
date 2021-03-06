package com.mayada1994.moviewatchlist_mvc.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.mayada1994.moviewatchlist_mvc.R
import com.mayada1994.moviewatchlist_mvc.activities.MainActivity
import com.mayada1994.moviewatchlist_mvc.adapters.MoviesAdapter
import com.mayada1994.moviewatchlist_mvc.databinding.DialogEditWatchlistBinding
import com.mayada1994.moviewatchlist_mvc.databinding.FragmentMoviesBinding
import com.mayada1994.moviewatchlist_mvc.di.WatchlistComponent
import com.mayada1994.moviewatchlist_mvc.entities.Movie
import com.mayada1994.moviewatchlist_mvc.entities.TmbdResponse
import com.mayada1994.moviewatchlist_mvc.models.LocalDataSource
import com.mayada1994.moviewatchlist_mvc.models.RemoteDataSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class MoviesFragment : Fragment() {

    enum class MovieType {
        POPULAR,
        UPCOMING
    }

    private lateinit var binding: FragmentMoviesBinding

    private var movieType: MovieType? = null

    private var compositeDisposable = CompositeDisposable()

    private lateinit var localDataSource: LocalDataSource

    private lateinit var remoteDataSource: RemoteDataSource

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

        localDataSource = WatchlistComponent.localDataSource
        remoteDataSource = WatchlistComponent.remoteDataSource

        when (movieType) {
            MovieType.POPULAR -> getPopularMovies()
            MovieType.UPCOMING -> getUpcomingMovies()
            else -> Timber.e("Unknown movie type $movieType")
        }
    }

    private fun getPopularMovies() {
        (requireActivity() as MainActivity).showProgress(true)
        compositeDisposable.add(
            remoteDataSource.getPopularMovies()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally { (requireActivity() as MainActivity).showProgress(false) }
                .subscribeWith(object : DisposableSingleObserver<TmbdResponse>() {
                    override fun onSuccess(response: TmbdResponse) {
                        setMoviesList(response.results)
                    }

                    override fun onError(e: Throwable) {
                        Toast.makeText(context, R.string.general_error_message, Toast.LENGTH_SHORT)
                            .show()
                        Timber.e(e)
                    }
                })
        )
    }

    private fun getUpcomingMovies() {
        (requireActivity() as MainActivity).showProgress(true)
        compositeDisposable.add(
            remoteDataSource.getUpcomingMovies()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally { (requireActivity() as MainActivity).showProgress(false) }
                .subscribeWith(object : DisposableSingleObserver<TmbdResponse>() {
                    override fun onSuccess(response: TmbdResponse) {
                        setMoviesList(response.results)
                    }

                    override fun onError(e: Throwable) {
                        Toast.makeText(context, R.string.general_error_message, Toast.LENGTH_SHORT)
                            .show()
                        Timber.e(e)
                    }
                })
        )
    }

    private fun setMoviesList(movies: List<Movie>) {
        with(binding) {
            imgPlaceholder.isVisible = movies.isEmpty()
            if (movies.isNotEmpty()) {
                moviesRecyclerView.adapter = MoviesAdapter(
                    movies,
                    object : MoviesAdapter.OnMovieClickListener {
                        override fun onClick(movie: Movie) {
                            showAddMovieDialog(movie)
                        }
                    }
                )
            }
        }
    }

    private fun showAddMovieDialog(movie: Movie) {
        val dialogView = DialogEditWatchlistBinding.inflate(layoutInflater)
        val alertDialog = AlertDialog.Builder(requireContext()).setView(dialogView.root).create()

        with(dialogView) {
            btnOk.setOnClickListener {
                alertDialog.dismiss()
                addMovieToWatchlist(movie)
            }
            btnCancel.setOnClickListener {
                alertDialog.dismiss()
            }
        }
        alertDialog.show()
    }

    private fun addMovieToWatchlist(movie: Movie) {
        (requireActivity() as MainActivity).showProgress(true)
        compositeDisposable.add(
            localDataSource.insertMovie(movie)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally { (requireActivity() as MainActivity).showProgress(false) }
                .subscribeWith(object : DisposableCompletableObserver() {
                    override fun onComplete() {
                        Toast.makeText(
                            context,
                            R.string.movie_added_to_watchlist_message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onError(e: Throwable) {
                        Toast.makeText(context, R.string.general_error_message, Toast.LENGTH_SHORT)
                            .show()
                        Timber.e(e)
                    }
                })
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.clear()
        }
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