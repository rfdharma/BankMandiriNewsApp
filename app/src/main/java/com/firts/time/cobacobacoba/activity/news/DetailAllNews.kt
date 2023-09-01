package com.firts.time.cobacobacoba.activity.news

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.firts.time.cobacobacoba.adapter.AllNewsAdapter
import com.first.time.cobacobacoba.api.ApiClient
import com.firts.time.cobacobacoba.R
import com.firts.time.cobacobacoba.model.ArticlesItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DetailAllNews : AppCompatActivity() {

    private lateinit var allNewsAdapter: AllNewsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_all_news)

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
            authorTextView.text = articlesItem.source?.name
            contentTextView.text = articlesItem.content
            publishTextView.text = articlesItem.getFormattedPublishedAt()
            descriptionTextView.text = articlesItem.description


            Glide.with(this)
                .load(articlesItem.urlToImage)
                .transform(RoundedCorners(20))
                .error(R.drawable.news)
                .into(newsImage)

            linkNews.setOnClickListener {
                val url = articlesItem?.url
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)
//                linkNews.text = articlesItem.url
                startActivity(intent)
            }

        }
        fetchAllNews()
    }

    private fun fetchAllNews() {
        val apiKey = "63a860ab3e8548b9bdcf5769dfb50a9d"
        val q = "indonesia"

        val apiService = ApiClient.apiService
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.getEverything(q, apiKey)
                if (response.isSuccessful) {
                    val articles = response.body()?.articles ?: emptyList()

                    val validArticles = articles.filter { article ->
                        article.publishedAt != null &&
                                article.author != null &&
                                article.urlToImage != null &&
                                article.description != null &&
                                article.source != null &&
                                article.title != null &&
                                article.url != null &&
                                article.content != null
                    }
                    withContext(Dispatchers.Main) {
                        // Update allNewsAdapter with news data
                        allNewsAdapter.newsList = validArticles
                        allNewsAdapter.notifyDataSetChanged()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
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