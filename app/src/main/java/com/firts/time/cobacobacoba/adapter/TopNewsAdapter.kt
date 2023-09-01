package com.firts.time.cobacobacoba.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.firts.time.cobacobacoba.R
import com.firts.time.cobacobacoba.model.ArticlesItem
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class TopNewsAdapter(var articles: List<ArticlesItem>) : RecyclerView.Adapter<TopNewsAdapter.ViewHolder>() {

    var onItemClick : ((ArticlesItem) -> Unit)? = null
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val publishTextView: TextView = itemView.findViewById(R.id.publish)
        val nameTextView: TextView = itemView.findViewById(R.id.namenews)
        val newsImage: ImageView = itemView.findViewById(R.id.newsImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_beritaterkini, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val article = articles[position]
        val sourceName = article.source?.name

        holder.titleTextView.text = article.title
        holder.nameTextView.text = sourceName
        holder.publishTextView.text = article.getFormattedPublishedAt()
//        holder.contentNews.text = article.content
        Glide.with(holder.itemView)
            .load(article.urlToImage)
            .transform(RoundedCorners(20))
            .placeholder(R.drawable.news) // Optional placeholder image while loading
            .error(R.drawable.news) // Optional image to display if loading fails
            .into(holder.newsImage)

        // Set click listener using proper lambda syntax
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(article)
        }
    }


    override fun getItemCount(): Int {
        return articles.size
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