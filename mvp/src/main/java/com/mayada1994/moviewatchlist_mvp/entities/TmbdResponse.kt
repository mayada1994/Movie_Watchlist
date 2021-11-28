package com.mayada1994.moviewatchlist_mvp.entities

import com.google.gson.annotations.SerializedName
import com.mayada1994.moviewatchlist_mvp.entities.Movie

data class TmbdResponse(
    @SerializedName("page") val page : Int = 0,
    @SerializedName("results") val results : List<Movie> = emptyList(),
    @SerializedName("total_pages") val totalPages : Int = 0,
    @SerializedName("total_results") val totalResults : Int = 0
)
