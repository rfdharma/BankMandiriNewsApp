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

    private lateinit var topNewsAdapter: TopNewsAdapter
    private lateinit var recyclerView: RecyclerView
    private var currentPage = 1
    private var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_top_news)

        recyclerView = findViewById(R.id.recyclerView)
        topNewsAdapter = TopNewsAdapter(emptyList())

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@TopNewsActivity)
            adapter = topNewsAdapter
        }

        topNewsAdapter.onItemClick = { article ->
            val intent = Intent(this@TopNewsActivity, DetailTopNews::class.java)
            intent.putExtra("ArticlesItem", article)
            startActivity(intent)
        }

        val itemButton = findViewById<FloatingActionButton>(R.id.floatingActionButton)
        itemButton.setOnClickListener {
            finish()
        }

        // Implementasi infinite scrolling
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItem = layoutManager.findFirstVisibleItemPosition()

                if (!isLoading && (visibleItemCount + firstVisibleItem) >= totalItemCount - 5) {
                    isLoading = true
                    currentPage++
                    fetchNews()
                }
            }
        })

        // Panggil fungsi untuk mengambil data berita
        fetchNews()
    }

    private fun fetchNews() {
        val apiKey = "63a860ab3e8548b9bdcf5769dfb50a9d"
        val country = "us"

        val apiService = ApiClient.apiService
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.getTopHeadlines(country, currentPage, apiKey)
                if (response.isSuccessful) {
                    val articles = response.body()?.articles ?: emptyList()
                    withContext(Dispatchers.Main) {
                        if (currentPage == 1) {
                            topNewsAdapter.articles = articles
                        } else {
                            topNewsAdapter.articles += articles
                        }
                        topNewsAdapter.notifyDataSetChanged()
                        isLoading = false
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
