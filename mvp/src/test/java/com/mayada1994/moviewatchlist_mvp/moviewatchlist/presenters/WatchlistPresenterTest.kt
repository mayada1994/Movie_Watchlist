package com.mayada1994.moviewatchlist_mvp.moviewatchlist.presenters

import com.mayada1994.moviewatchlist_mvp.R
import com.mayada1994.moviewatchlist_mvp.contracts.WatchlistContract
import com.mayada1994.moviewatchlist_mvp.entities.Movie
import com.mayada1994.moviewatchlist_mvp.models.LocalDataSource
import com.mayada1994.moviewatchlist_mvp.presenters.WatchlistPresenter
import com.mayada1994.moviewatchlist_mvp.rules.RxImmediateSchedulerRule
import io.mockk.*
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class WatchlistPresenterTest {

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    private val viewInterface: WatchlistContract.ViewInterface = mockk()

    private val localDataSource: LocalDataSource = mockk()

    private lateinit var watchlistPresenter: WatchlistPresenter

    @Before
    fun setup() {
        watchlistPresenter = WatchlistPresenter(viewInterface, localDataSource)
    }

    @After
    fun clear() {
        unmockkAll()
    }

    /**
     * Given:
     * - getMovies in localDataSource returns some list of movies from DB
     * When:
     * - init is called
     * Then should:
     * - call getMovies in localDataSource to get some list of movies from DB
     * - call setMoviesList in viewInterface with list of movies returned from DB
     * - call showPlaceholder in viewInterface with false as isVisible
     */
    @Test
    fun check_init() {
        //Given
        val movies = listOf(
            Movie(title = "Harry Potter and the Goblet of Fire"),
            Movie(title = "Harry Potter and the Chamber of Secrets")
        )

        every { viewInterface.showPlaceholder(false) } just Runs

        every { localDataSource.getMovies() } returns Single.just(movies)

        every { viewInterface.showProgress(true) } just Runs

        every { viewInterface.showProgress(false) } just Runs

        every { viewInterface.setMoviesList(movies) } just Runs

        every { viewInterface.setFloatingActionButtonImage(any()) } just Runs

        //When
        watchlistPresenter.init()

        //Then
        verifyOrder {
            viewInterface.showProgress(true)
            localDataSource.getMovies()
            viewInterface.setMoviesList(movies)
            viewInterface.showPlaceholder(false)
            viewInterface.showProgress(false)
        }
    }

    /**
     * Given:
     * - getMovies in localDataSource returns some list of movies from DB
     * When:
     * - init is called
     * Then should:
     * - call getMovies in localDataSource to get some list of movies from DB
     * - not call setMoviesList in viewInterface
     * - call showPlaceholder in viewInterface with true as isVisible
     */
    @Test
    fun check_init_emptyList() {
        //Given
        val movies = emptyList<Movie>()

        every { viewInterface.showPlaceholder(true) } just Runs

        every { localDataSource.getMovies() } returns Single.just(movies)

        every { viewInterface.showProgress(true) } just Runs

        every { viewInterface.showProgress(false) } just Runs

        every { viewInterface.setFloatingActionButtonImage(any()) } just Runs

        //When
        watchlistPresenter.init()

        //Then
        verifyOrder {
            viewInterface.showProgress(true)
            localDataSource.getMovies()
            viewInterface.showPlaceholder(true)
            viewInterface.showProgress(false)
        }

        verify(exactly = 0) { viewInterface.setMoviesList(any()) }
    }

    /**
     * Given:
     * - getMovies in localDataSource throws exception
     * - getMovies in localDataSource returns some list of movies from DB
     * When:
     * - init is called
     * Then should:
     * - call getMovies in localDataSource to get some list of movies from DB
     * - not call setMoviesList in viewInterface
     * - call showPlaceholder in viewInterface with true as isVisible
     */
    @Test
    fun check_init_error() {
        //Given
        val testException = Exception()

        every { viewInterface.showPlaceholder(true) } just Runs

        every { localDataSource.getMovies() } returns Single.error(testException)

        every { viewInterface.showProgress(true) } just Runs

        every { viewInterface.showProgress(false) } just Runs

        every { viewInterface.setFloatingActionButtonImage(any()) } just Runs

        //When
        watchlistPresenter.init()

        //Then
        verifyOrder {
            viewInterface.showProgress(true)
            localDataSource.getMovies()
            viewInterface.showPlaceholder(true)
            viewInterface.showProgress(false)
        }

        verify(exactly = 0) { viewInterface.setMoviesList(any()) }
    }

    /**
     * Given:
     * - selectedMovies is not empty
     * When:
     * - init is called
     * Then should:
     * - call setFloatingActionButtonImage in viewInterface with android.R.drawable.ic_delete as resId
     */
    @Test
    fun check_init_fab() {
        //Given
        val movies = listOf(
            Movie(title = "Harry Potter and the Goblet of Fire"),
            Movie(title = "Harry Potter and the Chamber of Secrets")
        )

        every { viewInterface.showPlaceholder(false) } just Runs

        every { localDataSource.getMovies() } returns Single.just(movies)

        every { viewInterface.showProgress(true) } just Runs

        every { viewInterface.showProgress(false) } just Runs

        every { viewInterface.setMoviesList(movies) } just Runs

        every { viewInterface.setFloatingActionButtonImage(android.R.drawable.ic_delete) } just Runs

        watchlistPresenter.onMovieItemChecked(movies[0], true)

        //When
        watchlistPresenter.init()

        //Then
        verify {
            viewInterface.setFloatingActionButtonImage(android.R.drawable.ic_delete)
        }
    }

    /**
     * Given:
     * - selectedMovies is empty
     * When:
     * - init is called
     * Then should:
     * - call setFloatingActionButtonImage in viewInterface with android.R.drawable.ic_input_add as resId
     */
    @Test
    fun check_init_fab_emptyList() {
        //Given
        val movies = listOf(
            Movie(title = "Harry Potter and the Goblet of Fire"),
            Movie(title = "Harry Potter and the Chamber of Secrets")
        )

        every { viewInterface.showPlaceholder(false) } just Runs

        every { localDataSource.getMovies() } returns Single.just(movies)

        every { viewInterface.showProgress(true) } just Runs

        every { viewInterface.showProgress(false) } just Runs

        every { viewInterface.setMoviesList(movies) } just Runs

        every { viewInterface.setFloatingActionButtonImage(android.R.drawable.ic_input_add) } just Runs

        //When
        watchlistPresenter.init()

        //Then
        verify {
            viewInterface.setFloatingActionButtonImage(android.R.drawable.ic_input_add)
        }
    }

    /**
     * Given:
     * - getMovies in localDataSource throws exception
     * - selectedMovies is not empty
     * When:
     * - init is called
     * Then should:
     * - call setFloatingActionButtonImage in viewInterface with android.R.drawable.ic_delete as resId
     */
    @Test
    fun check_init_error_fab() {
        //Given
        val testException = Exception()

        every { viewInterface.showPlaceholder(true) } just Runs

        every { localDataSource.getMovies() } returns Single.error(testException)

        every { viewInterface.showProgress(true) } just Runs

        every { viewInterface.showProgress(false) } just Runs

        every { viewInterface.setFloatingActionButtonImage(android.R.drawable.ic_delete) } just Runs

        watchlistPresenter.onMovieItemChecked(Movie(title = "Fantastic Four"), true)

        //When
        watchlistPresenter.init()

        //Then
        verify {
            viewInterface.setFloatingActionButtonImage(android.R.drawable.ic_delete)
        }
    }

    /**
     * Given:
     * - getMovies in localDataSource throws exception
     * - selectedMovies is empty
     * When:
     * - init is called
     * Then should:
     * - call setFloatingActionButtonImage in viewInterface with android.R.drawable.ic_input_add as resId
     */
    @Test
    fun check_init_error_fab_emptyList() {
        //Given
        val testException = Exception()

        every { viewInterface.showPlaceholder(true) } just Runs

        every { localDataSource.getMovies() } returns Single.error(testException)

        every { viewInterface.showProgress(true) } just Runs

        every { viewInterface.showProgress(false) } just Runs

        every { viewInterface.setFloatingActionButtonImage(android.R.drawable.ic_input_add) } just Runs

        //When
        watchlistPresenter.init()

        //Then
        verify {
            viewInterface.setFloatingActionButtonImage(android.R.drawable.ic_input_add)
        }
    }

    /**
     * Given:
     * - selectedMovies is not empty
     * When:
     * - onMovieItemChecked is called
     * Then should:
     * - call setFloatingActionButtonImage in viewInterface with android.R.drawable.ic_delete as resId
     */
    @Test
    fun check_onMovieItemChecked() {
        //Given
        every { viewInterface.setFloatingActionButtonImage(android.R.drawable.ic_delete) } just Runs

        //When
        watchlistPresenter.onMovieItemChecked(Movie(title = "Fantastic Four"), true)

        //Then
        verify {
            viewInterface.setFloatingActionButtonImage(android.R.drawable.ic_delete)
        }
    }

    /**
     * Given:
     * - selectedMovies is empty
     * When:
     * - onMovieItemChecked is called
     * Then should:
     * - call setFloatingActionButtonImage in viewInterface with android.R.drawable.ic_input_add as resId
     */
    @Test
    fun check_onMovieItemChecked_emptyList() {
        //Given
        every { viewInterface.setFloatingActionButtonImage(android.R.drawable.ic_input_add) } just Runs

        //When
        watchlistPresenter.onMovieItemChecked(Movie(title = "Fantastic Four"), false)

        //Then
        verify {
            viewInterface.setFloatingActionButtonImage(android.R.drawable.ic_input_add)
        }
    }

    /**
     * Given:
     * - movies is not empty
     * When:
     * - checkMoviesList is called
     * Then should:
     * - call showPlaceholder in viewInterface with false as isVisible
     */
    @Test
    fun check_checkMoviesList() {
        //Given
        every { viewInterface.showPlaceholder(false) } just Runs

        //When
        watchlistPresenter.checkMoviesList(listOf(Movie(title = "Titanic")))

        //Then
        verify {
            viewInterface.showPlaceholder(false)
        }
    }

    /**
     * Given:
     * - movies is empty
     * When:
     * - checkMoviesList is called
     * Then should:
     * - call showPlaceholder in viewInterface with true as isVisible
     */
    @Test
    fun check_checkMoviesList_emptyList() {
        //Given
        every { viewInterface.showPlaceholder(true) } just Runs

        //When
        watchlistPresenter.checkMoviesList(emptyList())

        //Then
        verify {
            viewInterface.showPlaceholder(true)
        }
    }

    /**
     * Given:
     * - selectedMovies is not empty
     * When:
     * - onFloatingActionButtonClick is called
     * Then should:
     * - call showDeleteMoviesDialog in viewInterface
     */
    @Test
    fun check_onFloatingActionButtonClick() {
        //Given
        every { viewInterface.showDeleteMoviesDialog() } just Runs

        every { viewInterface.setFloatingActionButtonImage(any()) } just Runs

        watchlistPresenter.onMovieItemChecked(Movie(title = "Titanic"), true)

        //When
        watchlistPresenter.onFloatingActionButtonClick()

        //Then
        verify {
            viewInterface.showDeleteMoviesDialog()
        }
    }

    /**
     * Given:
     * - selectedMovies is empty
     * When:
     * - checkMoviesList is called
     * Then should:
     * - call goToSearchScreen in viewInterface with true as isVisible
     */
    @Test
    fun check_onFloatingActionButtonClick_emptyList() {
        //Given
        every { viewInterface.goToSearchScreen() } just Runs

        every { viewInterface.setFloatingActionButtonImage(any()) } just Runs

        //When
        watchlistPresenter.onFloatingActionButtonClick()

        //Then
        verify {
            viewInterface.goToSearchScreen()
        }
    }

    /**
     * Given:
     * - some list of movies as selectedMovies
     * When:
     * - deleteMovies is called with selectedMovies
     * Then should:
     * - call deleteMovies in localDataSource with selectedMovies to delete movies from DB
     * - call updateMovies in viewInterface with selectedMovies
     * - call setFloatingActionButtonImage in viewInterface with android.R.drawable.ic_input_add as resId
     */
    @Test
    fun check_deleteMovies() {
        //Given
        val movies = listOf(Movie(title = "The Mummy"))

        every { viewInterface.setFloatingActionButtonImage(any()) } just Runs

        every { localDataSource.deleteMovies(any()) } returns Completable.complete()

        every { viewInterface.updateMovies(any()) } just Runs

        every { viewInterface.showProgress(true) } just Runs

        every { viewInterface.showProgress(false) } just Runs

        every { viewInterface.showToast(any()) } just Runs

        watchlistPresenter.onMovieItemChecked(movies[0], true)

        //When
        watchlistPresenter.deleteMovies()

        //Then
        verifyOrder {
            viewInterface.setFloatingActionButtonImage(android.R.drawable.ic_delete)
            viewInterface.showProgress(true)
            localDataSource.deleteMovies(movies)
            viewInterface.updateMovies(movies)
            viewInterface.setFloatingActionButtonImage(android.R.drawable.ic_input_add)
            viewInterface.showProgress(false)
        }

    }

    /**
     * Given:
     * - deleteMovies in localDataSource throws exception
     * - some list of movies as selectedMovies
     * When:
     * - deleteMovies is called with selectedMovies
     * Then should:
     * - call showToast in viewInterface with R.string.general_error_message as resId
     */
    @Test
    fun check_deleteMovies_error() {
        //Given
        val movies = listOf(Movie(title = "The Mummy"))

        val testException = Exception()

        every { viewInterface.setFloatingActionButtonImage(android.R.drawable.ic_delete) } just Runs

        every { localDataSource.deleteMovies(any()) } returns Completable.error(testException)

        every { viewInterface.showProgress(true) } just Runs

        every { viewInterface.showProgress(false) } just Runs

        every { viewInterface.showToast(R.string.general_error_message) } just Runs

        watchlistPresenter.onMovieItemChecked(movies[0], true)

        //When
        watchlistPresenter.deleteMovies()

        //Then
        verifyOrder {
            viewInterface.setFloatingActionButtonImage(android.R.drawable.ic_delete)
            viewInterface.showProgress(true)
            localDataSource.deleteMovies(movies)
            viewInterface.showToast(R.string.general_error_message)
            viewInterface.showProgress(false)
        }

    }

    /**
     * Given:
     * - selectedMovies contains one element
     * When:
     * - deleteMovies is called with selectedMovies
     * Then should:
     * - call showToast in viewInterface with R.string.movie_deleted_message as resId
     */
    @Test
    fun check_deleteMovies_selectedMovies_singleElement() {
        //Given
        val movies = listOf(Movie(title = "The Mummy"))

        every { viewInterface.setFloatingActionButtonImage(any()) } just Runs

        every { localDataSource.deleteMovies(any()) } returns Completable.complete()

        every { viewInterface.updateMovies(any()) } just Runs

        every { viewInterface.showProgress(true) } just Runs

        every { viewInterface.showProgress(false) } just Runs

        every { viewInterface.showToast(R.string.movie_deleted_message) } just Runs

        watchlistPresenter.onMovieItemChecked(movies[0], true)

        //When
        watchlistPresenter.deleteMovies()

        //Then
        verifyOrder {
            viewInterface.showToast(R.string.movie_deleted_message)
        }
    }

    /**
     * Given:
     * - selectedMovies contains multiple elements
     * When:
     * - deleteMovies is called with selectedMovies
     * Then should:
     * - call showToast in viewInterface with R.string.movies_deleted_message as resId
     */
    @Test
    fun check_deleteMovies_selectedMovies_multipleElements() {
        //Given
        val movies = listOf(Movie(title = "The Mummy"), Movie(title = "The Mummy Returns"))

        every { viewInterface.setFloatingActionButtonImage(any()) } just Runs

        every { localDataSource.deleteMovies(any()) } returns Completable.complete()

        every { viewInterface.updateMovies(any()) } just Runs

        every { viewInterface.showProgress(true) } just Runs

        every { viewInterface.showProgress(false) } just Runs

        every { viewInterface.showToast(R.string.movies_deleted_message) } just Runs

        movies.forEach {
            watchlistPresenter.onMovieItemChecked(it, true)
        }

        //When
        watchlistPresenter.deleteMovies()

        //Then
        verifyOrder {
            viewInterface.showToast(R.string.movies_deleted_message)
        }
    }

}