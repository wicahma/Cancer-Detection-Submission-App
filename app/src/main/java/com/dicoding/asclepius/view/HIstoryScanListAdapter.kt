package com.dicoding.asclepius.view

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.asclepius.data.local.entity.HistoryEntity
import com.dicoding.asclepius.databinding.HistoryCardBinding


class HistoryScanListAdapter :
    ListAdapter<HistoryEntity, HistoryScanListAdapter.HistoryViewHolder>(DIFF_CALLBACK) {
    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }


    class HistoryViewHolder(
        val binding: HistoryCardBinding,
        private val context: Context,
        private val onItemClickCallback: OnItemClickCallback
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(history: HistoryEntity) {
            binding.tvResult.text = history.predictionResult
            binding.tvPercentage.text = history.confidenceScore
            binding.imgArticle.setImageURI(Uri.parse(history.imageName))
            binding.root.setOnClickListener { view ->
                onItemClickCallback.onItemClicked(
                    history, view
                )
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): HistoryViewHolder {
        val binding = HistoryCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding, parent.context, onItemClickCallback)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val article = getItem(position)
        holder.bind(article)

    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<HistoryEntity>() {
            override fun areItemsTheSame(oldItem: HistoryEntity, newItem: HistoryEntity): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: HistoryEntity,
                newItem: HistoryEntity
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(data: HistoryEntity, view: View)
    }

}