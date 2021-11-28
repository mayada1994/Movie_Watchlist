package com.mayada1994.moviewatchlist_mvvm.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.mayada1994.moviewatchlist_mvvm.R
import com.mayada1994.moviewatchlist_mvvm.entities.Movie
import com.mayada1994.moviewatchlist_mvvm.repositories.MoviesRepository
import com.mayada1994.rules.RxImmediateSchedulerRule
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

    private val observerMoviesList: Observer<List<Movie>> = mockk()
    private val observerFloatingActionButtonImage: Observer<Int> = mockk()
    private val observerNavigateToSearchScreen: Observer<Boolean> = mockk()
    private val observerShowDeleteMoviesDialog: Observer<Boolean> = mockk()
    private val observerUpdateMoviesList: Observer<List<Movie>> = mockk()
    private val observerIsProgressVisible: Observer<Boolean> = mockk()
    private val observerIsPlaceholderVisible: Observer<Boolean> = mockk()
    private val observerToastMessageStringResId: Observer<Int> = mockk()

    private val moviesRepository: MoviesRepository = mockk()

    private lateinit var watchlistViewModel: WatchlistViewModel

    @Before
    fun setup() {
        watchlistViewModel = WatchlistViewModel(moviesRepository)
        watchlistViewModel.moviesList.observeForever(observerMoviesList)
        watchlistViewModel.floatingActionButtonImage.observeForever(observerFloatingActionButtonImage)
        watchlistViewModel.navigateToSearchScreen.observeForever(observerNavigateToSearchScreen)
        watchlistViewModel.showDeleteMoviesDialog.observeForever(observerShowDeleteMoviesDialog)
        watchlistViewModel.updateMoviesList.observeForever(observerUpdateMoviesList)
        watchlistViewModel.isProgressVisible.observeForever(observerIsProgressVisible)
        watchlistViewModel.isPlaceholderVisible.observeForever(observerIsPlaceholderVisible)
        watchlistViewModel.toastMessageStringResId.observeForever(observerToastMessageStringResId)
        every { observerMoviesList.onChanged(any()) } just Runs
        every { observerFloatingActionButtonImage.onChanged(any()) } just Runs
        every { observerNavigateToSearchScreen.onChanged(any()) } just Runs
        every { observerShowDeleteMoviesDialog.onChanged(any()) } just Runs
        every { observerUpdateMoviesList.onChanged(any()) } just Runs
        every { observerIsProgressVisible.onChanged(any()) } just Runs
        every { observerIsPlaceholderVisible.onChanged(any()) } just Runs
        every { observerToastMessageStringResId.onChanged(any()) } just Runs
    }

    @After
    fun clear() {
        watchlistViewModel.onDestroy()
        unmockkAll()
    }

    /**
     * Given:
     * - getMovies in moviesRepository returns some list of movies from DB
     * When:
     * - init is called
     * Then should:
     * - call getMovies in moviesRepository to get some list of movies from DB
     * - post moviesList in watchlistViewModel with list of movies returned from DB
     * - post isPlaceholderVisible in watchlistViewModel with false as isVisible
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
            observerIsProgressVisible.onChanged(true)
            moviesRepository.getMovies()
            observerMoviesList.onChanged(movies)
            observerIsPlaceholderVisible.onChanged(false)
            observerIsProgressVisible.onChanged(false)
        }
    }

    /**
     * Given:
     * - getMovies in moviesRepository returns empty list of movies from DB
     * When:
     * - init is called
     * Then should:
     * - call getMovies in moviesRepository to get some list of movies from DB
     * - not post moviesList in watchlistViewModel
     * - post isPlaceholderVisible in watchlistViewModel with true as isVisible
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
            observerIsProgressVisible.onChanged(true)
            moviesRepository.getMovies()
            observerIsPlaceholderVisible.onChanged(true)
            observerIsProgressVisible.onChanged(false)
        }

        verify(exactly = 0) { observerMoviesList.onChanged(movies) }
    }

    /**
     * Given:
     * - getMovies in moviesRepository throws exception
     * When:
     * - init is called
     * Then should:
     * - call getMovies in moviesRepository to get some list of movies from DB
     * - not post moviesList in watchlistViewModel
     * - post isPlaceholderVisible in watchlistViewModel with true as isVisible
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
            observerIsProgressVisible.onChanged(true)
            moviesRepository.getMovies()
            observerIsPlaceholderVisible.onChanged(true)
            observerIsProgressVisible.onChanged(false)
        }
    }

    /**
     * Given:
     * - selectedMovies is not empty
     * When:
     * - init is called
     * Then should:
     * - post floatingActionButtonImage in watchlistViewModel with android.R.drawable.ic_delete
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
            observerFloatingActionButtonImage.onChanged(android.R.drawable.ic_delete)
        }
    }

    /**
     * Given:
     * - selectedMovies is empty
     * When:
     * - init is called
     * Then should:
     * - post floatingActionButtonImage in watchlistViewModel with android.R.drawable.ic_input_add
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
            observerFloatingActionButtonImage.onChanged(android.R.drawable.ic_input_add)
        }
    }

    /**
     * Given:
     * - getMovies in moviesRepository throws exception
     * - selectedMovies is not empty
     * When:
     * - init is called
     * Then should:
     * - post floatingActionButtonImage in watchlistViewModel with android.R.drawable.ic_delete
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
            observerFloatingActionButtonImage.onChanged(android.R.drawable.ic_delete)
        }
    }

    /**
     * Given:
     * - getMovies in moviesRepository throws exception
     * - selectedMovies is empty
     * When:
     * - init is called
     * Then should:
     * - post floatingActionButtonImage in watchlistViewModel with android.R.drawable.ic_input_add
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
            observerFloatingActionButtonImage.onChanged(android.R.drawable.ic_input_add)
        }
    }

    /**
     * Given:
     * - selectedMovies is not empty
     * When:
     * - onMovieItemChecked is called
     * Then should:
     * - post floatingActionButtonImage in watchlistViewModel with android.R.drawable.ic_delete
     */
    @Test
    fun check_onMovieItemChecked() {
        //When
        watchlistViewModel.onMovieItemChecked(Movie(title = "Fantastic Four"), true)

        //Then
        verify {
            observerFloatingActionButtonImage.onChanged(android.R.drawable.ic_delete)
        }
    }

    /**
     * Given:
     * - selectedMovies is empty
     * When:
     * - onMovieItemChecked is called
     * Then should:
     * - post floatingActionButtonImage in watchlistViewModel with android.R.drawable.ic_input_add
     */
    @Test
    fun check_onMovieItemChecked_emptyList() {
        //When
        watchlistViewModel.onMovieItemChecked(Movie(title = "Fantastic Four"), false)

        //Then
        verify {
            observerFloatingActionButtonImage.onChanged(android.R.drawable.ic_input_add)
        }
    }

    /**
     * Given:
     * - movies is not empty
     * When:
     * - checkMoviesList is called
     * Then should:
     * - post isPlaceholderVisible in watchlistViewModel with false as isVisible
     */
    @Test
    fun check_checkMoviesList() {
        //When
        watchlistViewModel.checkMoviesList(listOf(Movie(title = "Titanic")))

        //Then
        verify {
            observerIsPlaceholderVisible.onChanged(false)
        }
    }

    /**
     * Given:
     * - movies is empty
     * When:
     * - checkMoviesList is called
     * Then should:
     * - post isPlaceholderVisible in watchlistViewModel with true as isVisible
     */
    @Test
    fun check_checkMoviesList_emptyList() {
        //When
        watchlistViewModel.checkMoviesList(emptyList())

        //Then
        verify {
            observerIsPlaceholderVisible.onChanged(true)
        }
    }

    /**
     * Given:
     * - selectedMovies is not empty
     * When:
     * - onFloatingActionButtonClick is called
     * Then should:
     * - post showDeleteMoviesDialog in watchlistViewModel
     */
    @Test
    fun check_onFloatingActionButtonClick() {
        //Given
        watchlistViewModel.onMovieItemChecked(Movie(title = "Titanic"), true)

        //When
        watchlistViewModel.onFloatingActionButtonClick()

        //Then
        verify {
            observerShowDeleteMoviesDialog.onChanged(any())
        }
    }

    /**
     * Given:
     * - selectedMovies is empty
     * When:
     * - checkMoviesList is called
     * Then should:
     * - post navigateToSearchScreen in watchlistViewModel
     */
    @Test
    fun check_onFloatingActionButtonClick_emptyList() {
        //When
        watchlistViewModel.onFloatingActionButtonClick()

        //Then
        verify {
            observerNavigateToSearchScreen.onChanged(any())
        }
    }

    /**
     * Given:
     * - some list of movies as selectedMovies
     * When:
     * - deleteMovies is called with selectedMovies
     * Then should:
     * - call deleteMovies in moviesRepository with selectedMovies to delete movies from DB
     * - post updateMoviesList in watchlistViewModel with selectedMovies
     * - post floatingActionButtonImage in watchlistViewModel with android.R.drawable.ic_input_add
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
            observerFloatingActionButtonImage.onChanged(android.R.drawable.ic_delete)
            observerIsProgressVisible.onChanged(true)
            moviesRepository.deleteMovies(movies)
            observerUpdateMoviesList.onChanged(movies)
            observerFloatingActionButtonImage.onChanged(android.R.drawable.ic_input_add)
            observerIsProgressVisible.onChanged(false)
        }

    }

    /**
     * Given:
     * - deleteMovies in moviesRepository throws exception
     * - some list of movies as selectedMovies
     * When:
     * - deleteMovies is called with selectedMovies
     * Then should:
     * - post toastMessageStringResId in watchlistViewModel with R.string.general_error_message
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
            observerFloatingActionButtonImage.onChanged(android.R.drawable.ic_delete)
            observerIsProgressVisible.onChanged(true)
            moviesRepository.deleteMovies(movies)
            observerToastMessageStringResId.onChanged(R.string.general_error_message)
            observerIsProgressVisible.onChanged(false)
        }

    }

    /**
     * Given:
     * - selectedMovies contains one element
     * When:
     * - deleteMovies is called with selectedMovies
     * Then should:
     * - post toastMessageStringResId in watchlistViewModel with R.string.movie_deleted_message
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
            observerToastMessageStringResId.onChanged(R.string.movie_deleted_message)
        }
    }

    /**
     * Given:
     * - selectedMovies contains multiple elements
     * When:
     * - deleteMovies is called with selectedMovies
     * Then should:
     * - post toastMessageStringResId in watchlistViewModel with R.string.movies_deleted_message
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
            observerToastMessageStringResId.onChanged(R.string.movies_deleted_message)
        }
    }

}