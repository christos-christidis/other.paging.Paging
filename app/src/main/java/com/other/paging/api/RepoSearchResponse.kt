package com.other.paging.api

import com.other.paging.model.Repo
import com.google.gson.annotations.SerializedName

// Data class to hold the response from the API calls.
data class RepoSearchResponse(
        @SerializedName("total_count") val total: Int = 0,
        @SerializedName("items") val items: List<Repo> = emptyList(),
        val nextPage: Int? = null
)
