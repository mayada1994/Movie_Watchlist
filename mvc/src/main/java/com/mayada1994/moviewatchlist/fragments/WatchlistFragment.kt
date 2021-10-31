package com.mayada1994.moviewatchlist.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.mayada1994.moviewatchlist.R
import com.mayada1994.moviewatchlist.activities.MainActivity
import com.mayada1994.moviewatchlist.adapters.WatchlistAdapter
import com.mayada1994.moviewatchlist.databinding.DialogEditWatchlistBinding
import com.mayada1994.moviewatchlist.databinding.FragmentWatchlistBinding
import com.mayada1994.moviewatchlist.di.WatchlistComponent
import com.mayada1994.moviewatchlist.entities.Movie
import com.mayada1994.moviewatchlist.models.LocalDataSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class WatchlistFragment : Fragment() {

    private lateinit var binding: FragmentWatchlistBinding

    private lateinit var dataSource: LocalDataSource

    private val compositeDisposable = CompositeDisposable()

    private val selectedMovies: ArrayList<Movie> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWatchlistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataSource = WatchlistComponent.localDataSource

        setListeners()
    }

    override fun onResume() {
        super.onResume()

        getWatchlist()
    }

    private fun setListeners() {
        binding.fab.setOnClickListener {
            if (selectedMovies.isEmpty()) {
                (requireActivity() as MainActivity).setFragment(SearchFragment())
            } else {
                showDeleteMoviesDialog()
            }
        }
    }

    private fun setMovieList(movies: List<Movie>) {
        with(binding) {
            layoutEmptyList.isVisible = movies.isNullOrEmpty()

            if (movies.isNotEmpty()) {
                moviesRecyclerView.adapter = WatchlistAdapter(
                    ArrayList(movies),
                    object : WatchlistAdapter.OnWatchlistItemSelectListener {
                        override fun onItemSelect(item: Movie, checked: Boolean) {
                            if (checked) {
                                selectedMovies.add(item)
                            } else {
                                selectedMovies.remove(item)
                            }
                            fab.setImageResource(
                                if (selectedMovies.isEmpty()) {
                                    android.R.drawable.ic_input_add
                                } else {
                                    android.R.drawable.ic_delete
                                }
                            )
                        }

                        override fun checkMoviesList(movies: List<Movie>) {
                            layoutEmptyList.isVisible = movies.isNullOrEmpty()
                        }
                    }
                )
            }
        }
    }

    private fun showDeleteMoviesDialog() {
        val dialogView = DialogEditWatchlistBinding.inflate(layoutInflater)
        val alertDialog = AlertDialog.Builder(requireContext()).setView(dialogView.root).create()

        with(dialogView) {
            txtPrompt.text = getString(R.string.dialog_delete_movie_prompt_message)
            btnOk.setOnClickListener {
                alertDialog.dismiss()
                deleteMovies()
            }
            btnCancel.setOnClickListener {
                alertDialog.dismiss()
            }
        }
        alertDialog.show()
    }

    private fun getWatchlist() {
        (requireActivity() as MainActivity).showProgress(true)
        compositeDisposable.add(
            dataSource.getMovies()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally { (requireActivity() as MainActivity).showProgress(false) }
                .subscribeWith(object : DisposableSingleObserver<List<Movie>>() {
                    override fun onSuccess(movies: List<Movie>) {
                        setMovieList(movies)
                    }

                    override fun onError(e: Throwable) {
                        Timber.e(e)
                        setMovieList(emptyList())
                    }

                })
        )
    }

    private fun deleteMovies() {
        (requireActivity() as MainActivity).showProgress(true)
        compositeDisposable.add(dataSource.deleteMovies(selectedMovies.toList())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doFinally { (requireActivity() as MainActivity).showProgress(false) }
            .subscribeWith(object : DisposableCompletableObserver() {
                override fun onComplete() {
                    if (selectedMovies.size == 1) {
                        Toast.makeText(context, R.string.movie_deleted_message, Toast.LENGTH_SHORT).show()
                    } else if (selectedMovies.size > 1) {
                        Toast.makeText(context, R.string.movies_deleted_message, Toast.LENGTH_SHORT).show()
                    }
                    binding.run {
                        (moviesRecyclerView.adapter as WatchlistAdapter).updateItems(selectedMovies.toList())
                        selectedMovies.clear()
                        fab.setImageResource(android.R.drawable.ic_input_add)
                    }
                }

                override fun onError(e: Throwable) {
                    Toast.makeText(context, R.string.general_error_message, Toast.LENGTH_SHORT).show()
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

}