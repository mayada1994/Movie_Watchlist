package com.mayada1994.moviewatchlist.models

import com.mayada1994.moviewatchlist.db.MovieDao
import com.mayada1994.moviewatchlist.entities.Movie
import com.mayada1994.rules.RxImmediateSchedulerRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LocalDataSourceTest {

    @Rule @JvmField
    var testSchedulerRule = RxImmediateSchedulerRule()

    private val movieDao: MovieDao = mockk()

    private lateinit var localDataSource: LocalDataSource

    @Before
    fun setup() {
        localDataSource = LocalDataSource(movieDao)
    }

    @After
    fun clear() {
        unmockkAll()
    }

    @Test
    fun `When getMovies in movieDao is called, then should return some list of movies`() {
        //Given
        val movies = listOf(
            Movie(title = "Harry Potter and the Goblet of Fire"),
            Movie(title = "Fantastic Four"),
            Movie(title = "Titanic")
        )

        every { movieDao.getMovies() } returns Single.just(movies)

        //When
        val result = localDataSource.getMovies()

        //Then
        result.test().assertValue(movies)
        verify { movieDao.getMovies() }
    }

    @Test
    fun `When insertMovie in movieDao is called with some movie object, then should save object to DB`() {
        //Given
        val movie = Movie(title = "Harry Potter and the Goblet of Fire")

        every { movieDao.insertMovie(movie) } returns Completable.complete()

        //When
        val result = movieDao.insertMovie(movie)

        //Then
        result.test().assertComplete()
        verify { movieDao.insertMovie(movie) }
    }

    @Test
    fun `When deleteMovies in movieDao is called with list of movies, then should delete movies from DB`() {
        //Given
        val movies = listOf(
            Movie(title = "Harry Potter and the Goblet of Fire"),
            Movie(title = "Fantastic Four"),
            Movie(title = "Titanic")
        )

        every { movieDao.deleteMovies(movies) } returns Completable.complete()

        //When
        val result = movieDao.deleteMovies(movies)

        //Then
        result.test().assertComplete()
        verify { movieDao.deleteMovies(movies) }
    }

}