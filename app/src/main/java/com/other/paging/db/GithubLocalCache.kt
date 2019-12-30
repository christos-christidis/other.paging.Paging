package com.other.paging.db

import android.util.Log
import androidx.paging.DataSource
import com.other.paging.model.Repo
import java.util.concurrent.Executor

// Class that handles the DAO local data source. This ensures that methods are triggered on the
// correct executor.
class GithubLocalCache(private val repoDao: RepoDao, private val ioExecutor: Executor) {

    // Insert a list of repos in the database, on a background thread.
    fun insert(repos: List<Repo>, insertFinished: () -> Unit) {
        ioExecutor.execute {
            Log.i("GithubLocalCache", "inserting ${repos.size} repos")
            repoDao.insert(repos)
            insertFinished()
        }
    }

    fun reposByName(name: String): DataSource.Factory<Int, Repo> {
        // appending '%' so we can allow other characters to be before and after the query string
        val query = "%${name.replace(' ', '%')}%"
        return repoDao.reposByName(query)
    }
}
