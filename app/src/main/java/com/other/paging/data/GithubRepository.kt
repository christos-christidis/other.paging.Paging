package com.other.paging.data

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.other.paging.api.GithubService
import com.other.paging.api.searchRepos
import com.other.paging.db.GithubLocalCache
import com.other.paging.model.RepoSearchResult

// Repository class that works with local and remote data sources.
class GithubRepository(private val service: GithubService, private val cache: GithubLocalCache) {

    // keep the last requested page. When the request is successful, increment the page number.
    private var _lastRequestedPage = 1

    private val _networkErrors = MutableLiveData<String>()

    // avoid triggering multiple requests in the same time
    private var _isRequestInProgress = false

    fun search(query: String): RepoSearchResult {
        Log.i("GithubRepository", "New query: $query")
        _lastRequestedPage = 1
        requestAndSaveData(query)

        // SOS: this gets LiveData from the Dao. So, even though the actual db insertion will happen
        // long after this method (search) returns, when it does, the Dao will re-run the query, data
        // will be updated and our list will also be updated!
        val data = cache.reposByName(query)

        return RepoSearchResult(data, _networkErrors)
    }

    fun requestMore(query: String) {
        requestAndSaveData(query)
    }

    private fun requestAndSaveData(query: String) {
        if (_isRequestInProgress) return

        _isRequestInProgress = true
        searchRepos(service, query, _lastRequestedPage, NETWORK_PAGE_SIZE, { repos ->
            cache.insert(repos) {
                _lastRequestedPage++
                _isRequestInProgress = false
            }
        }, { error ->
            _networkErrors.postValue(error)
            _isRequestInProgress = false
        })
    }

    companion object {
        private const val NETWORK_PAGE_SIZE = 50
    }
}
