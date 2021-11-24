package com.mayada1994.moviewatchlist.fragments

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.mayada1994.moviewatchlist.R
import com.mayada1994.moviewatchlist.activities.MainActivity
import com.mayada1994.moviewatchlist.adapters.MoviesAdapter
import com.mayada1994.moviewatchlist.databinding.DialogEditWatchlistBinding
import com.mayada1994.moviewatchlist.databinding.FragmentSearchBinding
import com.mayada1994.moviewatchlist.di.WatchlistComponent
import com.mayada1994.moviewatchlist.entities.Movie
import com.mayada1994.moviewatchlist.entities.TmbdResponse
import com.mayada1994.moviewatchlist.models.LocalDataSource
import com.mayada1994.moviewatchlist.models.RemoteDataSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableCompletableObserver
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding

    private val compositeDisposable = CompositeDisposable()

    private lateinit var localDataSource: LocalDataSource

    private lateinit var remoteDataSource: RemoteDataSource

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        localDataSource = WatchlistComponent.localDataSource
        remoteDataSource = WatchlistComponent.remoteDataSource

        setupSearchView()
    }

    private fun setupSearchView() {
        with(binding) {
            val first = searchView.getChildAt(0) as LinearLayout
            val second = first.getChildAt(2) as LinearLayout
            val third = second.getChildAt(1) as LinearLayout
            val searchIcon = second.getChildAt(0) as ImageView

            val autoComplete = third.getChildAt(0) as SearchView.SearchAutoComplete
            autoComplete.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.slate_blue
                )
            )
            autoComplete.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17f)
            autoComplete.setPadding(
                0,
                autoComplete.paddingTop,
                autoComplete.paddingRight,
                autoComplete.paddingBottom
            )

            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT
            )
            layoutParams.setMargins(0, second.paddingTop, second.paddingRight, second.paddingBottom)
            second.layoutParams = layoutParams

            val searchIconLayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT
            )
            searchIconLayoutParams.setMargins(8, searchIcon.paddingTop, 4, searchIcon.paddingBottom)
            searchIcon.layoutParams = searchIconLayoutParams

            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    imgPlaceholder.isVisible = false
                    activity?.let { hideKeyboard(it) }
                    searchView.clearFocus()
                    searchMovie(query)
                    return true
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    return false
                }
            })
        }
    }

    private fun searchMovie(query: String) {
        (requireActivity() as MainActivity).showProgress(true)
        compositeDisposable.add(
            remoteDataSource.searchMovie(query)
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
            layoutEmptyResultList.isVisible = movies.isNullOrEmpty()
            moviesRecyclerView.adapter = null

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

    fun hideKeyboard(activity: Activity) {
        val view = activity.currentFocus
        if (view != null) {
            val inputManager =
                activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(
                view.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.clear()
        }
    }

}