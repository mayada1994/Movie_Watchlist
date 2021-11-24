package com.mayada1994.moviewatchlist_mvc.models

import com.mayada1994.moviewatchlist_mvc.entities.Movie
import com.mayada1994.moviewatchlist_mvc.entities.TmbdResponse
import com.mayada1994.moviewatchlist_mvc.services.MoviesService
import com.mayada1994.rules.RxImmediateSchedulerRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import io.reactivex.Single
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RemoteDataSourceTest {

    @Rule
    @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    private val moviesService: MoviesService = mockk()

    private val apiKey = "API_KEY"

    private lateinit var remoteDataSource: RemoteDataSource

    @Before
    fun setup() {
        remoteDataSource = RemoteDataSource(moviesService, apiKey)
    }

    @After
    fun clear() {
        unmockkAll()
    }

    @Test
    fun `When searchMovie in moviesService is called with query Harry, then should return some list of movies that matches query from server`() {
        //Given
        val movies = listOf(
            Movie(title = "Harry Potter and the Goblet of Fire")
        )

        val query = "Harry"

        val tmbdResponse = TmbdResponse(results = movies)

        every { moviesService.searchMovie(apiKey, query) } returns Single.just(tmbdResponse)

        //When
        val result = remoteDataSource.searchMovie(query)

        //Then
        result.test().assertValue(tmbdResponse)
        verify { moviesService.searchMovie(apiKey, query) }
    }

    @Test
    fun `When getPopularMovies in moviesService is called with some page, then should return list of popular movies from server`() {
        //Given
        val movies = listOf(
            Movie(title = "Harry Potter and the Goblet of Fire"),
            Movie(title = "Fantastic Four"),
            Movie(title = "Titanic")
        )

        val tmbdResponse = TmbdResponse(results = movies)

        every { moviesService.getPopularMovies(apiKey) } returns Single.just(tmbdResponse)

        //When
        val result = remoteDataSource.getPopularMovies()

        //Then
        result.test().assertValue(tmbdResponse)
        verify { moviesService.getPopularMovies(apiKey) }
    }

    @Test
    fun `When getUpcomingMovies in moviesService is called with some page, then should return list of upcoming movies from server`() {
        //Given
        val movies = listOf(
            Movie(title = "The Addams Family 2")
        )

        val tmbdResponse = TmbdResponse(results = movies)

        every { moviesService.getUpcomingMovies(apiKey) } returns Single.just(tmbdResponse)

        //When
        val result = remoteDataSource.getUpcomingMovies()

        //Then
        result.test().assertValue(tmbdResponse)
        verify { moviesService.getUpcomingMovies(apiKey) }
    }

}