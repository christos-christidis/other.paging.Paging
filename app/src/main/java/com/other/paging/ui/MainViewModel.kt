package com.other.paging.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.other.paging.data.GithubRepository
import com.other.paging.model.Repo
import com.other.paging.model.RepoSearchResult

class MainViewModel(private val repository: GithubRepository) : ViewModel() {

    private val queryLiveData = MutableLiveData<String>()

    private val repoResult: LiveData<RepoSearchResult> = Transformations.map(queryLiveData) {
        repository.search(it)
    }

    val repos: LiveData<PagedList<Repo>> = Transformations.switchMap(repoResult) {
        it.data
    }
    val networkErrors: LiveData<String> = Transformations.switchMap(repoResult) {
        it.networkErrors
    }

    fun searchRepo(queryString: String) {
        queryLiveData.postValue(queryString)
    }

    fun lastQueryValue(): String? = queryLiveData.value
}
