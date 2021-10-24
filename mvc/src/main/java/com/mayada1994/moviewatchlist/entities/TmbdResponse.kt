package com.mayada1994.moviewatchlist.entities

import com.google.gson.annotations.SerializedName

data class TmbdResponse(
    @SerializedName("page") var page : Int,
    @SerializedName("results") var results : List<Movie>,
    @SerializedName("total_pages") var totalPages : Int,
    @SerializedName("total_results") var totalResults : Int
)
