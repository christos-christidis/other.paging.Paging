package com.other.paging.ui

import android.content.Intent
import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.android.codelabs.paging.R
import com.other.paging.model.Repo

class RepoViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val _name: TextView = view.findViewById(R.id.repo_name)
    private val _description: TextView = view.findViewById(R.id.repo_description)
    private val _stars: TextView = view.findViewById(R.id.repo_stars)
    private val _language: TextView = view.findViewById(R.id.repo_language)
    private val _forks: TextView = view.findViewById(R.id.repo_forks)

    private var _repo: Repo? = null

    init {
        view.setOnClickListener {
            _repo?.url?.let { url ->
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                view.context.startActivity(intent)
            }
        }
    }

    fun bind(repo: Repo?) {
        if (repo == null) {
            val resources = itemView.resources
            _name.text = resources.getString(R.string.loading)
            _description.visibility = View.GONE
            _language.visibility = View.GONE
            _stars.text = resources.getString(R.string.unknown)
            _forks.text = resources.getString(R.string.unknown)
        } else {
            showRepoData(repo)
        }
    }

    private fun showRepoData(repo: Repo) {
        _repo = repo
        _name.text = repo.fullName

        // if the description is missing, hide the TextView
        var descriptionVisibility = View.GONE
        if (repo.description != null) {
            _description.text = repo.description
            descriptionVisibility = View.VISIBLE
        }
        _description.visibility = descriptionVisibility

        _stars.text = repo.stars.toString()
        _forks.text = repo.forks.toString()

        // if the language is missing, hide the label and the value
        var languageVisibility = View.GONE
        if (!repo.language.isNullOrEmpty()) {
            val resources = itemView.context.resources
            _language.text = resources.getString(R.string.language, repo.language)
            languageVisibility = View.VISIBLE
        }
        _language.visibility = languageVisibility
    }

    companion object {
        fun create(parent: ViewGroup): RepoViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.repo_view_item, parent, false)
            return RepoViewHolder(view)
        }
    }
}
