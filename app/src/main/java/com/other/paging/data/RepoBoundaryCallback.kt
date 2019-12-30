package com.other.paging.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import com.other.paging.api.GithubService
import com.other.paging.api.searchRepos
import com.other.paging.db.GithubLocalCache
import com.other.paging.model.Repo

class RepoBoundaryCallback(
        private val query: String,
        private val service: GithubService,
        private val cache: GithubLocalCache) : PagedList.BoundaryCallback<Repo>() {

    companion object {
        private const val NETWORK_PAGE_SIZE = 50
    }

    // keep the last requested page. When the request is successful, increment the page number.
    private var _lastRequestedPage = 1

    private val _networkErrors = MutableLiveData<String>()
    val networkErrors: LiveData<String>
        get() = _networkErrors

    // avoid triggering multiple requests in the same time
    private var _isRequestInProgress = false

    private fun requestAndSaveData(query: String) {
        if (_isRequestInProgress) return

        Log.i("WTF", "requesting page $_lastRequestedPage")
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

    override fun onZeroItemsLoaded() {
        Log.i("WTF", "onZeroItemsLoaded")
        requestAndSaveData(query)
    }

    override fun onItemAtEndLoaded(itemAtEnd: Repo) {
        Log.i("WTF", "onItemAtEndLoaded")
        requestAndSaveData(query)
    }
}