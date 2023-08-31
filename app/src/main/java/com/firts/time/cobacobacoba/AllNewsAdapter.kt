package com.first.time.cobacobacoba.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firts.time.cobacobacoba.R
import com.firts.time.cobacobacoba.model.ArticlesItem
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class AllNewsAdapter(var newsList: List<ArticlesItem>) :
    RecyclerView.Adapter<AllNewsAdapter.NewsViewHolder>() {

    var onItemClick : ((ArticlesItem) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_berita, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val newsItem = newsList[position]
        val sourceName = newsItem.source?.name
        holder.bind(newsItem)
    }

    override fun getItemCount(): Int {
        return newsList.size
    }

    inner class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val newsImageView: ImageView = itemView.findViewById(R.id.newsImageView)
        private val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        private val publishNewsTextView: TextView = itemView.findViewById(R.id.publish_news)
        private val nameTextView: TextView = itemView.findViewById(R.id.authorTextViewAll)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick?.invoke(newsList[position])
                }
            }
        }
        fun bind(newsItem: ArticlesItem) {
            titleTextView.text = newsItem.title
            publishNewsTextView.text = newsItem.getFormattedPublishedAt()
            nameTextView.text = newsItem.source?.name

            // Load image using Glide library
            Glide.with(itemView)
                .load(newsItem.urlToImage)
                .placeholder(R.drawable.news) // You can set a placeholder image
                .into(newsImageView)

        }
    }

    private fun ArticlesItem.getFormattedPublishedAt(): String? {
        val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")

        // Check if publishedAt is null
        if (publishedAt == null) {
            return null
        }

        return try {
            val publishedAtLocalDateTime = LocalDateTime.parse(publishedAt, dateTimeFormatter)

            val formattedDateTimeFormatter = DateTimeFormatter.ofPattern("dd MMMM, yyyy")
            publishedAtLocalDateTime.format(formattedDateTimeFormatter)
        } catch (e: DateTimeParseException) {
            null // Handle invalid date format
        }
    }
}