package com.dicoding.asclepius.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dicoding.asclepius.BuildConfig
import com.dicoding.asclepius.data.remote.response.ArticleResponse
import com.dicoding.asclepius.databinding.ArticleCardBinding

class ArticleListAdapter :
    ListAdapter<ArticleResponse, ArticleListAdapter.ArticleViewHolder>(DIFF_CALLBACK) {
    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    class ArticleViewHolder(
        val binding: ArticleCardBinding,
        private val context: Context,
        private val onItemClickCallback: OnItemClickCallback
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(article: ArticleResponse) {
            binding.tvTitleArticle.text = article.title
            binding.tvAuthor.text = article.author
            article.description.let {
                if (it == null) binding.tvDesc.text = "" else binding.tvDesc.text = it
            }
            article.urlToImage.let {
                var url = BuildConfig.IMAGE_PLACEHOLDER

                if (it !== null) url = article.urlToImage.toString()
                Glide.with(context).load(url).into(binding.imgArticle)
            }
            binding.root.setOnClickListener { view ->
                onItemClickCallback.onItemClicked(
                    article, view
                )
            }
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): ArticleViewHolder {
        val binding = ArticleCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArticleViewHolder(binding, parent.context, onItemClickCallback)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = getItem(position)
        holder.bind(article)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ArticleResponse>() {
            override fun areItemsTheSame(
                oldItem: ArticleResponse, newItem: ArticleResponse
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ArticleResponse, newItem: ArticleResponse
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: ArticleResponse, view: View)
    }
}