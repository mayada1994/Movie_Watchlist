package com.mayada1994.moviewatchlist_mvi.fragments

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
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.mayada1994.moviewatchlist_mvi.R
import com.mayada1994.moviewatchlist_mvi.activities.MainActivity
import com.mayada1994.moviewatchlist_mvi.adapters.MoviesAdapter
import com.mayada1994.moviewatchlist_mvi.databinding.DialogEditWatchlistBinding
import com.mayada1994.moviewatchlist_mvi.databinding.FragmentSearchBinding
import com.mayada1994.moviewatchlist_mvi.di.WatchlistComponent
import com.mayada1994.moviewatchlist_mvi.entities.Movie
import com.mayada1994.moviewatchlist_mvi.interactors.SearchInteractor
import com.mayada1994.moviewatchlist_mvi.presenters.SearchPresenter
import com.mayada1994.moviewatchlist_mvi.states.SearchState
import com.mayada1994.moviewatchlist_mvi.views.SearchMovieView
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class SearchFragment : Fragment(), SearchMovieView {

    private lateinit var binding: FragmentSearchBinding

    private lateinit var presenter: SearchPresenter

    private val movieSubject: PublishSubject<Movie> = PublishSubject.create()

    private val querySubject: PublishSubject<String> = PublishSubject.create()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSearchView()

        presenter = SearchPresenter(SearchInteractor(WatchlistComponent.moviesRepository))
        presenter.bind(this)
    }

    override fun render(state: SearchState) {
        when (state) {
            is SearchState.DataState -> renderDataState(state.movies)

            is SearchState.LoadingState -> renderLoadingState()

            is SearchState.EmptyState -> renderEmptyState()

            is SearchState.CompletedState -> renderCompletedState(state.resId)

            is SearchState.ErrorState -> renderErrorState(state.resId)
        }
    }

    override fun searchMovieIntent(): Observable<String> = querySubject

    override fun addMovieToWatchlistIntent(): Observable<Movie> = movieSubject

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
                    activity?.let { hideKeyboard(it) }
                    searchView.clearFocus()
                    showPlaceholder(false)
                    querySubject.onNext(query)
                    return true
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    return false
                }
            })
        }
    }

    private fun renderDataState(movies: List<Movie>) {
        showEmptySearchResult(false)

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
        showEmptySearchResult(true)
        clearMovieList()
    }

    private fun renderCompletedState(@StringRes resId: Int) {
        showProgress(false)
        showToast(resId)
    }

    private fun renderErrorState(@StringRes resId: Int) {
        showPlaceholder(true)
        showProgress(false)
        showToast(resId)
        clearMovieList()
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

    private fun showEmptySearchResult(isVisible: Boolean) {
        binding.layoutEmptyResultList.isVisible = isVisible
    }

    private fun clearMovieList() {
        binding.moviesRecyclerView.adapter = null
    }

    private fun showPlaceholder(isVisible: Boolean) {
        binding.imgPlaceholder.isVisible = isVisible
    }

    private fun showProgress(isProgressVisible: Boolean) {
        (activity as MainActivity?)?.showProgress(isProgressVisible)
    }

    private fun showToast(resId: Int) {
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show()
    }

    private fun hideKeyboard(activity: Activity) {
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
        presenter.unbind()
    }

}