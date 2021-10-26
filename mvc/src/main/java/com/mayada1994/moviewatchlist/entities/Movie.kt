package com.mayada1994.moviewatchlist.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "movies")
data class Movie(
    @PrimaryKey @SerializedName("id") val id: Int? = null,
    @SerializedName("original_title") val originalTitle: String? = null,
    @SerializedName("overview") val overview: String? = null,
    @SerializedName("poster_path") val posterPath: String? = null,
    @SerializedName("release_date") val releaseDate: String? = null,
    @SerializedName("title") val title: String? = null
) {
    fun getMovieTitle(): String? {
        return title ?: originalTitle
    }

    fun getReleaseYearFromDate(): String? {
        return releaseDate?.split("-")?.firstOrNull()
    }
}
