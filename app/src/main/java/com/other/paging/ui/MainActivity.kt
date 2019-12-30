package com.other.paging.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.other.paging.Injection
import com.example.android.codelabs.paging.R
import com.other.paging.model.Repo
import kotlinx.android.synthetic.main.activity_search_repositories.*

class MainActivity : AppCompatActivity() {

    private lateinit var _viewModel: MainViewModel
    private val _adapter = ReposAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_repositories)

        _viewModel = ViewModelProvider(this, Injection.provideViewModelFactory(this))
                .get(MainViewModel::class.java)

        // add dividers between RecyclerView's row items
        val decoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        list.addItemDecoration(decoration)
        setupScrollListener()

        initAdapter()
        val query = savedInstanceState?.getString(LAST_SEARCH_QUERY) ?: DEFAULT_QUERY
        search_repo.setText(query)
        setupSearchListener()
        updateRepoListFromInput()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(LAST_SEARCH_QUERY, _viewModel.lastQueryValue())
    }

    private fun initAdapter() {
        list.adapter = _adapter
        _viewModel.repos.observe(this, Observer<List<Repo>> {
            Log.i("Activity", "list: ${it?.size}")
            showEmptyList(it?.size == 0)
            _adapter.submitList(it)
        })
        _viewModel.networkErrors.observe(this, Observer<String> {
            Toast.makeText(this, "\uD83D\uDE28 Wooops $it", Toast.LENGTH_LONG).show()
        })
    }

    private fun setupSearchListener() {
        search_repo.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                updateRepoListFromInput()
                true
            } else {
                false
            }
        }
    }

    private fun updateRepoListFromInput() {
        search_repo.text.trim().let {
            if (it.isNotEmpty()) {
                list.scrollToPosition(0)
                _viewModel.searchRepo(it.toString())
                _adapter.submitList(null)
            }
        }
    }

    private fun showEmptyList(show: Boolean) {
        if (show) {
            emptyList.visibility = View.VISIBLE
            list.visibility = View.GONE
        } else {
            emptyList.visibility = View.GONE
            list.visibility = View.VISIBLE
        }
    }

    private fun setupScrollListener() {
        val layoutManager = list.layoutManager as LinearLayoutManager
        list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalItemCount = layoutManager.itemCount
                val visibleItemCount = layoutManager.childCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                _viewModel.listScrolled(visibleItemCount, lastVisibleItem, totalItemCount)
            }
        })
    }

    companion object {
        private const val LAST_SEARCH_QUERY: String = "last_search_query"
        private const val DEFAULT_QUERY = "Android"
    }
}
