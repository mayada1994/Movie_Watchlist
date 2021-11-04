package com.mayada1994.moviewatchlist_mvvm.moviewatchlist.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.mayada1994.moviewatchlist_mvvm.R
import com.mayada1994.moviewatchlist_mvvm.entities.Movie
import com.mayada1994.moviewatchlist_mvvm.repositories.MoviesRepository
import com.mayada1994.moviewatchlist_mvvm.rules.RxImmediateSchedulerRule
import com.mayada1994.moviewatchlist_mvvm.utils.ViewEvent
import com.mayada1994.moviewatchlist_mvvm.viewmodels.BaseViewModel
import com.mayada1994.moviewatchlist_mvvm.viewmodels.WatchlistViewModel
import io.mockk.*
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class WatchlistViewModelTest {

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private val observerViewEvent: Observer<ViewEvent> = mockk()

    private val moviesRepository: MoviesRepository = mockk()

    private lateinit var watchlistViewModel: WatchlistViewModel

    @Before
    fun setup() {
        watchlistViewModel = WatchlistViewModel(moviesRepository)
        watchlistViewModel.event.observeForever(observerViewEvent)
        every { observerViewEvent.onChanged(any()) } just Runs
    }

    @After
    fun clear() {
        unmockkAll()
    }

    /**
     * Given:
     * - getMovies in moviesRepository returns some list of movies from DB
     * When:
     * - init is called
     * Then should:
     * - call getMovies in moviesRepository to get some list of movies from DB
     * - call setEvent with SetMoviesList in watchlistViewModel with list of movies returned from DB
     * - call setEvent with ShowPlaceholder in watchlistViewModel with false as isVisible
     */
    @Test
    fun check_init() {
        //Given
        val movies = listOf(
            Movie(title = "Harry Potter and the Goblet of Fire"),
            Movie(title = "Harry Potter and the Chamber of Secrets")
        )

        every { moviesRepository.getMovies() } returns Single.just(movies)

        //When
        watchlistViewModel.init()

        //Then
        verifyOrder {
            observerViewEvent.onChanged(BaseViewModel.BaseEvent.ShowProgress(true))
            moviesRepository.getMovies()
            observerViewEvent.onChanged(WatchlistViewModel.WatchlistEvent.SetMoviesList(movies))
            observerViewEvent.onChanged(BaseViewModel.BaseEvent.ShowPlaceholder(false))
            observerViewEvent.onChanged(BaseViewModel.BaseEvent.ShowProgress(false))
        }
    }

    /**
     * Given:
     * - getMovies in moviesRepository returns empty list of movies from DB
     * When:
     * - init is called
     * Then should:
     * - call getMovies in moviesRepository to get some list of movies from DB
     * - not call setEvent with SetMoviesList in watchlistViewModel
     * - call setEvent with ShowPlaceholder in watchlistViewModel with true as isVisible
     */
    @Test
    fun check_init_emptyList() {
        //Given
        val movies = emptyList<Movie>()

        every { moviesRepository.getMovies() } returns Single.just(movies)

        //When
        watchlistViewModel.init()

        //Then
        verifyOrder {
            observerViewEvent.onChanged(BaseViewModel.BaseEvent.ShowProgress(true))
            moviesRepository.getMovies()
            observerViewEvent.onChanged(BaseViewModel.BaseEvent.ShowPlaceholder(true))
            observerViewEvent.onChanged(BaseViewModel.BaseEvent.ShowProgress(false))
        }

        verify(exactly = 0) { observerViewEvent.onChanged(WatchlistViewModel.WatchlistEvent.SetMoviesList(movies)) }
    }

    /**
     * Given:
     * - getMovies in moviesRepository throws exception
     * When:
     * - init is called
     * Then should:
     * - call getMovies in moviesRepository to get some list of movies from DB
     * - not call setEvent with SetMoviesList in watchlistViewModel
     * - call setEvent with ShowPlaceholder in watchlistViewModel with true as isVisible
     */
    @Test
    fun check_init_error() {
        //Given
        val testException = Exception()

        every { moviesRepository.getMovies() } returns Single.error(testException)

        //When
        watchlistViewModel.init()

        //Then
        verifyOrder {
            observerViewEvent.onChanged(BaseViewModel.BaseEvent.ShowProgress(true))
            moviesRepository.getMovies()
            observerViewEvent.onChanged(BaseViewModel.BaseEvent.ShowPlaceholder(true))
            observerViewEvent.onChanged(BaseViewModel.BaseEvent.ShowProgress(false))
        }
    }

    /**
     * Given:
     * - selectedMovies is not empty
     * When:
     * - init is called
     * Then should:
     * - call setEvent with SetFloatingActionButtonImage in watchlistViewModel with android.R.drawable.ic_delete as resId
     */
    @Test
    fun check_init_fab() {
        //Given
        val movies = listOf(
            Movie(title = "Harry Potter and the Goblet of Fire"),
            Movie(title = "Harry Potter and the Chamber of Secrets")
        )

        every { moviesRepository.getMovies() } returns Single.just(movies)

        watchlistViewModel.onMovieItemChecked(movies[0], true)

        //When
        watchlistViewModel.init()

        //Then
        verify {
            observerViewEvent.onChanged(WatchlistViewModel.WatchlistEvent.SetFloatingActionButtonImage(android.R.drawable.ic_delete))
        }
    }

    /**
     * Given:
     * - selectedMovies is empty
     * When:
     * - init is called
     * Then should:
     * - call setEvent with SetFloatingActionButtonImage in watchlistViewModel with android.R.drawable.ic_input_add as resId
     */
    @Test
    fun check_init_fab_emptyList() {
        //Given
        val movies = listOf(
            Movie(title = "Harry Potter and the Goblet of Fire"),
            Movie(title = "Harry Potter and the Chamber of Secrets")
        )

        every { moviesRepository.getMovies() } returns Single.just(movies)

        //When
        watchlistViewModel.init()

        //Then
        verify {
            observerViewEvent.onChanged(WatchlistViewModel.WatchlistEvent.SetFloatingActionButtonImage(android.R.drawable.ic_input_add))
        }
    }

    /**
     * Given:
     * - getMovies in moviesRepository throws exception
     * - selectedMovies is not empty
     * When:
     * - init is called
     * Then should:
     * - call setEvent with SetFloatingActionButtonImage in watchlistViewModel with android.R.drawable.ic_delete as resId
     */
    @Test
    fun check_init_error_fab() {
        //Given
        val testException = Exception()

        every { moviesRepository.getMovies() } returns Single.error(testException)

        watchlistViewModel.onMovieItemChecked(Movie(title = "Fantastic Four"), true)

        //When
        watchlistViewModel.init()

        //Then
        verify {
            observerViewEvent.onChanged(WatchlistViewModel.WatchlistEvent.SetFloatingActionButtonImage(android.R.drawable.ic_delete))
        }
    }

    /**
     * Given:
     * - getMovies in moviesRepository throws exception
     * - selectedMovies is empty
     * When:
     * - init is called
     * Then should:
     * - call setEvent with SetFloatingActionButtonImage in watchlistViewModel with android.R.drawable.ic_input_add as resId
     */
    @Test
    fun check_init_error_fab_emptyList() {
        //Given
        val testException = Exception()

        every { moviesRepository.getMovies() } returns Single.error(testException)

        //When
        watchlistViewModel.init()

        //Then
        verify {
            observerViewEvent.onChanged(WatchlistViewModel.WatchlistEvent.SetFloatingActionButtonImage(android.R.drawable.ic_input_add))
        }
    }

    /**
     * Given:
     * - selectedMovies is not empty
     * When:
     * - onMovieItemChecked is called
     * Then should:
     * - call setEvent with SetFloatingActionButtonImage in watchlistViewModel with android.R.drawable.ic_delete as resId
     */
    @Test
    fun check_onMovieItemChecked() {
        //When
        watchlistViewModel.onMovieItemChecked(Movie(title = "Fantastic Four"), true)

        //Then
        verify {
            observerViewEvent.onChanged(WatchlistViewModel.WatchlistEvent.SetFloatingActionButtonImage(android.R.drawable.ic_delete))
        }
    }

    /**
     * Given:
     * - selectedMovies is empty
     * When:
     * - onMovieItemChecked is called
     * Then should:
     * - call setEvent with SetFloatingActionButtonImage in watchlistViewModel with android.R.drawable.ic_input_add as resId
     */
    @Test
    fun check_onMovieItemChecked_emptyList() {
        //When
        watchlistViewModel.onMovieItemChecked(Movie(title = "Fantastic Four"), false)

        //Then
        verify {
            observerViewEvent.onChanged(WatchlistViewModel.WatchlistEvent.SetFloatingActionButtonImage(android.R.drawable.ic_input_add))
        }
    }

    /**
     * Given:
     * - movies is not empty
     * When:
     * - checkMoviesList is called
     * Then should:
     * - call setEvent with ShowPlaceholder in watchlistViewModel with false as isVisible
     */
    @Test
    fun check_checkMoviesList() {
        //When
        watchlistViewModel.checkMoviesList(listOf(Movie(title = "Titanic")))

        //Then
        verify {
            observerViewEvent.onChanged(BaseViewModel.BaseEvent.ShowPlaceholder(false))
        }
    }

    /**
     * Given:
     * - movies is empty
     * When:
     * - checkMoviesList is called
     * Then should:
     * - call setEvent with ShowPlaceholder in watchlistViewModel with true as isVisible
     */
    @Test
    fun check_checkMoviesList_emptyList() {
        //When
        watchlistViewModel.checkMoviesList(emptyList())

        //Then
        verify {
            observerViewEvent.onChanged(BaseViewModel.BaseEvent.ShowPlaceholder(true))
        }
    }

    /**
     * Given:
     * - selectedMovies is not empty
     * When:
     * - onFloatingActionButtonClick is called
     * Then should:
     * - call setEvent with ShowDeleteMoviesDialog in watchlistViewModel
     */
    @Test
    fun check_onFloatingActionButtonClick() {
        //Given
        watchlistViewModel.onMovieItemChecked(Movie(title = "Titanic"), true)

        //When
        watchlistViewModel.onFloatingActionButtonClick()

        //Then
        verify {
            observerViewEvent.onChanged(WatchlistViewModel.WatchlistEvent.ShowDeleteMoviesDialog)
        }
    }

    /**
     * Given:
     * - selectedMovies is empty
     * When:
     * - checkMoviesList is called
     * Then should:
     * - call setEvent with GoToSearchScreen in watchlistViewModel with true as isVisible
     */
    @Test
    fun check_onFloatingActionButtonClick_emptyList() {
        //When
        watchlistViewModel.onFloatingActionButtonClick()

        //Then
        verify {
            observerViewEvent.onChanged(WatchlistViewModel.WatchlistEvent.GoToSearchScreen)
        }
    }

    /**
     * Given:
     * - some list of movies as selectedMovies
     * When:
     * - deleteMovies is called with selectedMovies
     * Then should:
     * - call setEvent with DeleteMovies in moviesRepository with selectedMovies to delete movies from DB
     * - call setEvent with UpdateMovies in watchlistViewModel with selectedMovies
     * - call setEvent with SetFloatingActionButtonImage in watchlistViewModel with android.R.drawable.ic_input_add as resId
     */
    @Test
    fun check_deleteMovies() {
        //Given
        val movies = listOf(Movie(title = "The Mummy"))

        every { moviesRepository.deleteMovies(any()) } returns Completable.complete()

        watchlistViewModel.onMovieItemChecked(movies[0], true)

        //When
        watchlistViewModel.deleteMovies()

        //Then
        verifyOrder {
            observerViewEvent.onChanged(WatchlistViewModel.WatchlistEvent.SetFloatingActionButtonImage(android.R.drawable.ic_delete))
            observerViewEvent.onChanged(BaseViewModel.BaseEvent.ShowProgress(true))
            moviesRepository.deleteMovies(movies)
            observerViewEvent.onChanged(WatchlistViewModel.WatchlistEvent.UpdateMovies(movies))
            observerViewEvent.onChanged(WatchlistViewModel.WatchlistEvent.SetFloatingActionButtonImage(android.R.drawable.ic_input_add))
            observerViewEvent.onChanged(BaseViewModel.BaseEvent.ShowProgress(false))
        }

    }

    /**
     * Given:
     * - deleteMovies in moviesRepository throws exception
     * - some list of movies as selectedMovies
     * When:
     * - deleteMovies is called with selectedMovies
     * Then should:
     * - call setEvent with ShowMessage in watchlistViewModel with R.string.general_error_message as resId
     */
    @Test
    fun check_deleteMovies_error() {
        //Given
        val movies = listOf(Movie(title = "The Mummy"))

        val testException = Exception()

        every { moviesRepository.deleteMovies(any()) } returns Completable.error(testException)

        watchlistViewModel.onMovieItemChecked(movies[0], true)

        //When
        watchlistViewModel.deleteMovies()

        //Then
        verifyOrder {
            observerViewEvent.onChanged(WatchlistViewModel.WatchlistEvent.SetFloatingActionButtonImage(android.R.drawable.ic_delete))
            observerViewEvent.onChanged(BaseViewModel.BaseEvent.ShowProgress(true))
            moviesRepository.deleteMovies(movies)
            observerViewEvent.onChanged(BaseViewModel.BaseEvent.ShowMessage(R.string.general_error_message))
            observerViewEvent.onChanged(BaseViewModel.BaseEvent.ShowProgress(false))
        }

    }

    /**
     * Given:
     * - selectedMovies contains one element
     * When:
     * - deleteMovies is called with selectedMovies
     * Then should:
     * - call setEvent with ShowMessage in watchlistViewModel with R.string.movie_deleted_message as resId
     */
    @Test
    fun check_deleteMovies_selectedMovies_singleElement() {
        //Given
        val movies = listOf(Movie(title = "The Mummy"))

        every { moviesRepository.deleteMovies(any()) } returns Completable.complete()

        watchlistViewModel.onMovieItemChecked(movies[0], true)

        //When
        watchlistViewModel.deleteMovies()

        //Then
        verifyOrder {
            observerViewEvent.onChanged(BaseViewModel.BaseEvent.ShowMessage(R.string.movie_deleted_message))
        }
    }

    /**
     * Given:
     * - selectedMovies contains multiple elements
     * When:
     * - deleteMovies is called with selectedMovies
     * Then should:
     * - call setEvent with ShowMessage in watchlistViewModel with R.string.movies_deleted_message as resId
     */
    @Test
    fun check_deleteMovies_selectedMovies_multipleElements() {
        //Given
        val movies = listOf(Movie(title = "The Mummy"), Movie(title = "The Mummy Returns"))

        every { moviesRepository.deleteMovies(any()) } returns Completable.complete()

        movies.forEach {
            watchlistViewModel.onMovieItemChecked(it, true)
        }

        //When
        watchlistViewModel.deleteMovies()

        //Then
        verifyOrder {
            observerViewEvent.onChanged(BaseViewModel.BaseEvent.ShowMessage(R.string.movies_deleted_message))
        }
    }

}