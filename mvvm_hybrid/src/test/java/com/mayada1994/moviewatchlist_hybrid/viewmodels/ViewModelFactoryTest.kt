package com.mayada1994.moviewatchlist_hybrid.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.ViewModel
import com.mayada1994.moviewatchlist_hybrid.repositories.MoviesRepository
import com.mayada1994.rules.RxImmediateSchedulerRule
import io.mockk.mockk
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class ViewModelFactoryTest {

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private val moviesRepository: MoviesRepository = mockk()

    private lateinit var viewModelFactory: ViewModelFactory

    @Before
    fun setup() {
        viewModelFactory = ViewModelFactory(moviesRepository)
    }

    @After
    fun clear() {
        unmockkAll()
    }

    @Test
    fun `When create in viewModelFactory called with MainViewModel as class, then should return instance of MainViewModel`() {
        //Given
        val mainViewModel = MainViewModel()

        //When
        val result = viewModelFactory.create(MainViewModel::class.java)

        //Then
        assertEquals(mainViewModel.javaClass, result.javaClass)
    }

    @Test
    fun `When create in viewModelFactory called with MoviesViewModel as class, then should return instance of MoviesViewModel`() {
        //Given
        val moviesViewModel = MoviesViewModel(moviesRepository)

        //When
        val result = viewModelFactory.create(MoviesViewModel::class.java)

        //Then
        assertEquals(moviesViewModel.javaClass, result.javaClass)
    }

    @Test
    fun `When create in viewModelFactory called with SearchViewModel as class, then should return instance of SearchViewModel`() {
        //Given
        val searchViewModel = SearchViewModel(moviesRepository)

        //When
        val result = viewModelFactory.create(SearchViewModel::class.java)

        //Then
        assertEquals(searchViewModel.javaClass, result.javaClass)
    }

    @Test
    fun `When create in viewModelFactory called with WatchlistViewModel as class, then should return instance of WatchlistViewModel`() {
        //Given
        val watchlistViewModel = WatchlistViewModel(moviesRepository)

        //When
        val result = viewModelFactory.create(WatchlistViewModel::class.java)

        //Then
        assertEquals(watchlistViewModel.javaClass, result.javaClass)
    }

    @Test
    fun `When create in viewModelFactory called with other class, then should throw RuntimeException`() {
        //Given
        class TestViewModel: ViewModel()

        val testException = RuntimeException("Unable to create ${TestViewModel::class.java}")

        try {
            //When
            val result = viewModelFactory.create(TestViewModel::class.java)

            //Then
            assertEquals(testException.javaClass, result.javaClass)
        } catch (e: Exception) {
        }
    }

}