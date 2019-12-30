package com.other.paging

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.other.paging.api.GithubService
import com.other.paging.data.GithubRepository
import com.other.paging.db.GithubLocalCache
import com.other.paging.db.RepoDatabase
import com.other.paging.ui.ViewModelFactory
import java.util.concurrent.Executors

// Provides objects that can be passed as parameters in the constructors and then replaced for
// testing, where needed.
object Injection {

    private fun provideCache(context: Context): GithubLocalCache {
        val database = RepoDatabase.getInstance(context)
        return GithubLocalCache(database.reposDao(), Executors.newSingleThreadExecutor())
    }

    private fun provideGithubRepository(context: Context): GithubRepository {
        return GithubRepository(GithubService.create(), provideCache(context))
    }

    fun provideViewModelFactory(context: Context): ViewModelProvider.Factory {
        return ViewModelFactory(provideGithubRepository(context))
    }
}
