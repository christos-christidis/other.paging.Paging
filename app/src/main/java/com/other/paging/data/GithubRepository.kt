package com.other.paging.data

import android.util.Log
import androidx.paging.LivePagedListBuilder
import com.other.paging.api.GithubService
import com.other.paging.db.GithubLocalCache
import com.other.paging.model.RepoSearchResult

class GithubRepository(private val service: GithubService, private val cache: GithubLocalCache) {

    companion object {
        private const val DATABASE_PAGE_SIZE = 20
    }

    fun search(query: String): RepoSearchResult {
        Log.i("GithubRepository", "New query: $query")

        val dataSourceFactory = cache.reposByName(query)

        val boundaryCallback = RepoBoundaryCallback(query, service, cache)
        val networkErrors = boundaryCallback.networkErrors

        val data = LivePagedListBuilder(dataSourceFactory, DATABASE_PAGE_SIZE)
                .setBoundaryCallback(boundaryCallback)
                .build()

        return RepoSearchResult(data, networkErrors)
    }
}
