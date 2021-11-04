package com.mayada1994.moviewatchlist_mvi.repositories

import com.mayada1994.moviewatchlist_mvi.db.MovieDao
import com.mayada1994.moviewatchlist_mvi.entities.Movie
import com.mayada1994.moviewatchlist_mvi.entities.TmbdResponse
import com.mayada1994.moviewatchlist_mvi.services.MoviesService
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

class MoviesRepositoryTest {

    @Rule @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    private val movieDao: MovieDao = mockk()

    private val moviesService: MoviesService = mockk()

    private val apiKey = "API_KEY"

    private lateinit var moviesRepository: MoviesRepository

    @Before
    fun setup() {
        moviesRepository = MoviesRepository(movieDao, moviesService, apiKey)
    }

    @After
    fun clear() {
        unmockkAll()
    }

    //region Local

    @Test
    fun `When getMovies in moviesRepository is called, then should return some list of movies`() {
        //Given
        val movies = listOf(
            Movie(title = "Harry Potter and the Goblet of Fire"),
            Movie(title = "Fantastic Four"),
            Movie(title = "Titanic")
        )

        every { movieDao.getMovies() } returns Single.just(movies)

        //When
        val result = moviesRepository.getMovies()

        //Then
        result.test().assertValue(movies)
        verify { movieDao.getMovies() }
    }

    @Test
    fun `When insertMovie in moviesRepository is called with some movie object, then should save object to DB`() {
        //Given
        val movie = Movie(title = "Harry Potter and the Goblet of Fire")

        every { movieDao.insertMovie(movie) } returns Single.just(1L)

        //When
        val result = moviesRepository.insertMovie(movie)

        //Then
        result.test().assertComplete()
        verify { movieDao.insertMovie(movie) }
    }

    @Test
    fun `When deleteMovies in moviesRepository is called with list of movies, then should delete movies from DB`() {
        //Given
        val movies = listOf(
            Movie(title = "Harry Potter and the Goblet of Fire"),
            Movie(title = "Fantastic Four"),
            Movie(title = "Titanic")
        )

        every { movieDao.deleteMovies(movies) } returns Single.just(Unit)

        //When
        val result = moviesRepository.deleteMovies(movies)

        //Then
        result.test().assertComplete()
        verify { movieDao.deleteMovies(movies) }
    }

    //endregion

    //region Remote

    @Test
    fun `When searchMovie in moviesRepository is called with query Harry, then should return some list of movies that matches query from server`() {
        //Given
        val movies = listOf(
            Movie(title = "Harry Potter and the Goblet of Fire")
        )

        val query = "Harry"

        val tmbdResponse = TmbdResponse(results = movies)

        every { moviesService.searchMovie(apiKey, query, 1) } returns Single.just(tmbdResponse)

        //When
        val result = moviesRepository.searchMovie(query, 1)

        //Then
        result.test().assertValue(tmbdResponse)
        verify { moviesService.searchMovie(apiKey, query, 1) }
    }

    @Test
    fun `When getPopularMovies in moviesRepository is called with some page, then should return list of popular movies from server`() {
        //Given
        val movies = listOf(
            Movie(title = "Harry Potter and the Goblet of Fire"),
            Movie(title = "Fantastic Four"),
            Movie(title = "Titanic")
        )

        val tmbdResponse = TmbdResponse(results = movies)

        every { moviesService.getPopularMovies(apiKey, 1) } returns Single.just(tmbdResponse)

        //When
        val result = moviesRepository.getPopularMovies(1)

        //Then
        result.test().assertValue(tmbdResponse)
        verify { moviesService.getPopularMovies(apiKey, 1) }
    }

    @Test
    fun `When getUpcomingMovies in moviesRepository is called with some page, then should return list of upcoming movies from server`() {
        //Given
        val movies = listOf(
            Movie(title = "The Addams Family 2")
        )

        val tmbdResponse = TmbdResponse(results = movies)

        every { moviesService.getUpcomingMovies(apiKey, 1) } returns Single.just(tmbdResponse)

        //When
        val result = moviesRepository.getUpcomingMovies(1)

        //Then
        result.test().assertValue(tmbdResponse)
        verify { moviesService.getUpcomingMovies(apiKey, 1) }
    }

    //endregion

}