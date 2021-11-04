package com.mayada1994.moviewatchlist_mvvm.entities

import org.junit.Assert.assertEquals
import org.junit.Test

class MovieTest {

    @Test
    fun `Given movie with 2003-05-30 as releaseDate, when getReleaseYearFromDate is called, then should return 2003`() {
        //Given
        val movie = Movie(title = "Finding Nemo", releaseDate = "2003-05-30")

        //When
        val result = movie.getReleaseYearFromDate()

        //Then
        assertEquals("2003", result)
    }

    @Test
    fun `Given movie with 2003 as releaseDate, when getReleaseYearFromDate is called, then should return 2003`() {
        //Given
        val movie = Movie(title = "FindingNemo", releaseDate = "2003")

        //When
        val result = movie.getReleaseYearFromDate()

        //Then
        assertEquals("2003", result)
    }

    @Test
    fun `Given movie with empty string as releaseDate, when getReleaseYearFromDate is called, then should return empty string`() {
        //Given
        val movie = Movie(title = "FindingNemo", releaseDate = "")

        //When
        val result = movie.getReleaseYearFromDate()

        //Then
        assertEquals("", result)
    }

    @Test
    fun `Given movie with null as releaseDate, when getReleaseYearFromDate is called, then should return null`() {
        //Given
        val movie = Movie(title = "FindingNemo")

        //When
        val result = movie.getReleaseYearFromDate()

        //Then
        assertEquals(null, result)
    }

    @Test
    fun `Given movie with The Dog in the Manger as title and Собака на сене as original title, when getMovieTitle is called, then should return The Dog in the Manger`() {
        //Given
        val movie = Movie(title = "The Dog in the Manger", originalTitle = "Собака на сене")

        //When
        val result = movie.getMovieTitle()

        //Then
        assertEquals("The Dog in the Manger", result)
    }

    @Test
    fun `Given movie with The Dog in the Manger as title and null as original title, when getMovieTitle is called, then should return The Dog in the Manger`() {
        //Given
        val movie = Movie(title = "The Dog in the Manger")

        //When
        val result = movie.getMovieTitle()

        //Then
        assertEquals("The Dog in the Manger", result)
    }

    @Test
    fun `Given movie with null as title and Собака на сене as original title, when getMovieTitle is called, then should return The Dog in the Manger`() {
        //Given
        val movie = Movie(originalTitle = "Собака на сене")

        //When
        val result = movie.getMovieTitle()

        //Then
        assertEquals("Собака на сене", result)
    }

    @Test
    fun `Given movie with null as title and null as original title, when getMovieTitle is called, then should return The Dog in the Manger`() {
        //Given
        val movie = Movie()

        //When
        val result = movie.getMovieTitle()

        //Then
        assertEquals(null, result)
    }

}