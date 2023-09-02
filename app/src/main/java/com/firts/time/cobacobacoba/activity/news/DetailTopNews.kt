package com.firts.time.cobacobacoba.activity.news

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.first.time.cobacobacoba.api.ApiClient
import com.firts.time.cobacobacoba.R
import com.firts.time.cobacobacoba.adapter.TopNewsAdapter
import com.firts.time.cobacobacoba.model.ArticlesItem
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DetailTopNews : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_berita_terkini)

        val itemButton = findViewById<FloatingActionButton>(R.id.floatingActionButton)
        itemButton.setOnClickListener {
            finish()
        }

        val articlesItem = intent.getParcelableExtra<ArticlesItem>("ArticlesItem")
        if (articlesItem != null) {
            val titleTextView: TextView = findViewById(R.id.titleDetail)
            val authorTextView: TextView = findViewById(R.id.authorTextView)
            val contentTextView: TextView = findViewById(R.id.content)
            val newsImage: ImageView = findViewById(R.id.imageView)
            val publishTextView: TextView = findViewById(R.id.publish_news)
            val descriptionTextView: TextView = findViewById(R.id.desc)
            val linkNews: TextView = findViewById(R.id.urlnews)

            titleTextView.text = articlesItem.title
            authorTextView.text = articlesItem.author
            contentTextView.text = articlesItem.content
            publishTextView.text = articlesItem.getFormattedPublishedAt()
            descriptionTextView.text = articlesItem.description

            Glide.with(this)
                .load(articlesItem.urlToImage)
                .transform(RoundedCorners(20))
                .error(R.drawable.news)
                .into(newsImage)

            linkNews.setOnClickListener {
                val url = articlesItem.url
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)
                startActivity(intent)
            }
        }
    }

    private fun ArticlesItem.getFormattedPublishedAt(): String {
        val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
        val publishedAtLocalDateTime = LocalDateTime.parse(publishedAt, dateTimeFormatter)

        val formattedDateTimeFormatter = DateTimeFormatter.ofPattern("dd MMMM, yyyy")
        return publishedAtLocalDateTime.format(formattedDateTimeFormatter)
    }
}
