package com.firts.time.cobacobacoba.activity.news

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.first.time.cobacobacoba.api.ApiClient
import com.firts.time.cobacobacoba.R
import com.firts.time.cobacobacoba.adapter.AllNewsAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AllNewsActivity : AppCompatActivity() {
    private lateinit var allNewsAdapter: AllNewsAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_news)

        recyclerView = findViewById(R.id.recyclerViewAllNews)
        allNewsAdapter = AllNewsAdapter(emptyList())

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@AllNewsActivity)
            adapter = allNewsAdapter
        }

        allNewsAdapter.onItemClick = { article ->
            val intent = Intent(this@AllNewsActivity, DetailTopNews::class.java)
            intent.putExtra("ArticlesItem", article)
            startActivity(intent)
        }

        val itemButton = findViewById<FloatingActionButton>(R.id.floatingActionButton)
        itemButton.setOnClickListener {
            finish()
        }

        // Panggil fungsi untuk mengambil data berita
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
}
