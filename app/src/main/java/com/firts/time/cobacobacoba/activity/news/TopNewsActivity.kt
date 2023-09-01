package com.firts.time.cobacobacoba.activity.news

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.first.time.cobacobacoba.api.ApiClient
import com.firts.time.cobacobacoba.R
import com.firts.time.cobacobacoba.adapter.AllNewsAdapter
import com.firts.time.cobacobacoba.adapter.TopNewsAdapter
import com.firts.time.cobacobacoba.model.ArticlesItem
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TopNewsActivity : AppCompatActivity() {
    private lateinit var TopNewsAdapter: TopNewsAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_top_news)

        recyclerView = findViewById(R.id.recyclerView)
        TopNewsAdapter = TopNewsAdapter(emptyList())

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@TopNewsActivity)
            adapter = TopNewsAdapter
        }

        TopNewsAdapter.onItemClick = { article ->
            val intent = Intent(this@TopNewsActivity, DetailTopNews::class.java)
            intent.putExtra("ArticlesItem", article)
            startActivity(intent)
        }

        val itemButton = findViewById<FloatingActionButton>(R.id.floatingActionButton)
        itemButton.setOnClickListener {
            finish()
        }

        // Panggil fungsi untuk mengambil data berita
        fetchNews()
    }

    private fun fetchNews() {
        val apiKey = "63a860ab3e8548b9bdcf5769dfb50a9d"
        val country = "us"

        val apiService = ApiClient.apiService
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.getTopHeadlines(country, apiKey)
                if (response.isSuccessful) {
                    val articles = response.body()?.articles ?: emptyList()
                    withContext(Dispatchers.Main) {
                        // Perbarui adapter dengan data berita
                        TopNewsAdapter.articles = articles
                        TopNewsAdapter.notifyDataSetChanged()
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