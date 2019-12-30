package com.other.paging.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.other.paging.data.GithubRepository
import com.other.paging.model.Repo
import com.other.paging.model.RepoSearchResult

class MainViewModel(private val repository: GithubRepository) : ViewModel() {

    companion object {
        private const val VISIBLE_THRESHOLD = 5
    }

    private val queryLiveData = MutableLiveData<String>()

    // SOS: when queryLiveData's value changes, repoResult's value is set to the expr in the lambda.
    private val repoResult: LiveData<RepoSearchResult> = Transformations.map(queryLiveData) {
        repository.search(it)
    }

    // SOS: first note that the lambda now returns a LiveData, NOT a plain value. When repoResult's
    // value changes, repos is set to track the LiveData in the lambda, ie its value is set to it.data's
    // value and when it.data changes, so does it. When at a later time repoResult changes again,
    // it'll be because of a new RepoSearchResult and ofc it.data will refer to a NEW LiveData. What
    // switchMap will do is cancel repos' observation of the old LiveData and set it to track the new
    // LiveData.
    val repos: LiveData<List<Repo>> = Transformations.switchMap(repoResult) {
        it.data
    }
    val networkErrors: LiveData<String> = Transformations.switchMap(repoResult) {
        it.networkErrors
    }

    fun searchRepo(queryString: String) {
        queryLiveData.postValue(queryString)
    }

    fun listScrolled(visibleItemCount: Int, lastVisibleItemPosition: Int, totalItemCount: Int) {
        if (visibleItemCount + lastVisibleItemPosition + VISIBLE_THRESHOLD >= totalItemCount) {
            lastQueryValue()?.let {
                repository.requestMore(it)
            }
        }
    }

    fun lastQueryValue(): String? = queryLiveData.value
}
