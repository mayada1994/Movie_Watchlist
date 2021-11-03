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
import com.mayada1994.moviewatchlist_mvi.R
import com.mayada1994.moviewatchlist_mvi.activities.MainActivity
import com.mayada1994.moviewatchlist_mvi.adapters.WatchlistAdapter
import com.mayada1994.moviewatchlist_mvi.databinding.DialogEditWatchlistBinding
import com.mayada1994.moviewatchlist_mvi.databinding.FragmentWatchlistBinding
import com.mayada1994.moviewatchlist_mvi.di.WatchlistComponent
import com.mayada1994.moviewatchlist_mvi.entities.Movie
import com.mayada1994.moviewatchlist_mvi.interactors.WatchlistInteractor
import com.mayada1994.moviewatchlist_mvi.presenters.WatchlistPresenter
import com.mayada1994.moviewatchlist_mvi.states.WatchlistState
import com.mayada1994.moviewatchlist_mvi.views.WatchlistView
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class WatchlistFragment : Fragment(), WatchlistView {

    private lateinit var binding: FragmentWatchlistBinding

    private val fabSubject: PublishSubject<Unit> = PublishSubject.create()

    private val moviesSubject: PublishSubject<Unit> = PublishSubject.create()

    private val selectedMoviesSubject: PublishSubject<Pair<Movie, Boolean>> = PublishSubject.create()

    private val checkedMoviesListSubject: PublishSubject<List<Movie>> = PublishSubject.create()

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

        setListeners()

        presenter = WatchlistPresenter(WatchlistInteractor(WatchlistComponent.moviesRepository))
        presenter.bind(this)
    }

    private fun setListeners() {
        binding.fab.setOnClickListener {
            fabSubject.onNext(Unit)
        }
    }

    override fun render(state: WatchlistState) {
        when (state) {
            is WatchlistState.DataState -> renderDataState(state.movies)

            is WatchlistState.FloatingActionButtonImageState -> renderFloatingActionButtonImageState(state.resId)

            is WatchlistState.NavigateToSearchScreenState -> renderNavigateToSearchScreenState()

            is WatchlistState.ShowDeleteMoviesDialogState -> renderShowDeleteMoviesDialogState()

            is WatchlistState.UpdateDataState -> renderUpdateDataState(state.movies, state.resId)

            is WatchlistState.LoadingState -> renderLoadingState()

            is WatchlistState.EmptyState -> renderEmptyState()

            is WatchlistState.ErrorState -> renderErrorState(state.resId)
        }
    }

    override fun displayMoviesIntent(): Observable<Unit> = Observable.just(Unit)

    override fun deleteMoviesFromWatchlistIntent(): Observable<Unit> = moviesSubject

    override fun floatingActionButtonClickIntent(): Observable<Unit> = fabSubject

    override fun selectMovieIntent(): Observable<Pair<Movie, Boolean>> = selectedMoviesSubject

    override fun checkMoviesListIntent(): Observable<List<Movie>> = checkedMoviesListSubject

    private fun renderDataState(movies: List<Movie>) {
        showPlaceholder(false)
        showProgress(false)

        binding.moviesRecyclerView.adapter = WatchlistAdapter(
            ArrayList(movies),
            object : WatchlistAdapter.OnWatchlistItemSelectListener {
                override fun onItemSelect(item: Movie, checked: Boolean) {
                    selectedMoviesSubject.onNext(item to checked)
                }

                override fun checkMoviesList(movies: List<Movie>) {
                    checkedMoviesListSubject.onNext(movies)
                }
            }
        )
    }

    private fun renderUpdateDataState(selectedMovies: List<Movie>, @StringRes resId: Int) {
        (binding.moviesRecyclerView.adapter as WatchlistAdapter).updateItems(selectedMovies.toList())
        showProgress(false)
        showToast(resId)
        renderFloatingActionButtonImageState(android.R.drawable.ic_input_add)
    }

    private fun renderLoadingState() {
        showProgress(true)
    }

    private fun renderEmptyState() {
        showProgress(false)
        showPlaceholder(true)
        renderFloatingActionButtonImageState(android.R.drawable.ic_input_add)
    }

    private fun renderErrorState(@StringRes resId: Int) {
        showPlaceholder(true)
        showProgress(false)
        showToast(resId)
    }

    private fun renderShowDeleteMoviesDialogState() {
        val dialogView = DialogEditWatchlistBinding.inflate(layoutInflater)
        val alertDialog = AlertDialog.Builder(requireContext()).setView(dialogView.root).create()

        with(dialogView) {
            txtPrompt.text = getString(R.string.dialog_delete_movie_prompt_message)
            btnOk.setOnClickListener {
                alertDialog.dismiss()
                moviesSubject.onNext(Unit)
            }
            btnCancel.setOnClickListener {
                alertDialog.dismiss()
            }
        }
        alertDialog.show()
    }

    private fun renderNavigateToSearchScreenState() {
//        (requireActivity() as MainActivity).setFragment(SearchFragment())
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

    private fun renderFloatingActionButtonImageState(resId: Int) {
        binding.fab.setImageResource(resId)
    }

    override fun onStop() {
        super.onStop()
        presenter.unbind()
    }

}