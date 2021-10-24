package com.mayada1994.moviewatchlist.entities

import androidx.room.Entity
import com.google.gson.annotations.SerializedName

@Entity(tableName = "movies")
data class Movie(
    @SerializedName("id") val id: Int?,
    @SerializedName("original_title") val originalTitle: String?,
    @SerializedName("overview") val overview: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("title") val title: String?
) {
    fun getMovieTitle(): String? {
        return title ?: originalTitle
    }

    fun getReleaseYearFromDate(): String? {
        return releaseDate?.split("-")?.firstOrNull()
    }
}
