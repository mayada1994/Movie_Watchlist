package com.mayada1994.moviewatchlist_mvp.models

import com.mayada1994.moviewatchlist_mvp.entities.Movie
import com.mayada1994.moviewatchlist_mvp.entities.TmbdResponse
import com.mayada1994.rules.RxImmediateSchedulerRule
import com.mayada1994.moviewatchlist_mvp.services.MoviesService
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

        every { moviesService.searchMovie(apiKey, query, 1) } returns Single.just(tmbdResponse)

        //When
        val result = remoteDataSource.searchMovie(query, 1)

        //Then
        result.test().assertValue(tmbdResponse)
        verify { moviesService.searchMovie(apiKey, query, 1) }
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

        every { moviesService.getPopularMovies(apiKey, 1) } returns Single.just(tmbdResponse)

        //When
        val result = remoteDataSource.getPopularMovies(1)

        //Then
        result.test().assertValue(tmbdResponse)
        verify { moviesService.getPopularMovies(apiKey, 1) }
    }

    @Test
    fun `When getUpcomingMovies in moviesService is called with some page, then should return list of upcoming movies from server`() {
        //Given
        val movies = listOf(
            Movie(title = "The Addams Family 2")
        )

        val tmbdResponse = TmbdResponse(results = movies)

        every { moviesService.getUpcomingMovies(apiKey, 1) } returns Single.just(tmbdResponse)

        //When
        val result = remoteDataSource.getUpcomingMovies(1)

        //Then
        result.test().assertValue(tmbdResponse)
        verify { moviesService.getUpcomingMovies(apiKey, 1) }
    }

}