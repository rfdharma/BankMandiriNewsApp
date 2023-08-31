package com.firts.time.cobacobacoba

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.first.time.cobacobacoba.adapter.AllNewsAdapter
import com.first.time.cobacobacoba.api.ApiClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var articlesAdapter: ArticlesAdapter
    private lateinit var allNewsAdapter: AllNewsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread.sleep(300)
        installSplashScreen()
        setContentView(R.layout.activity_main)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        val recyclerViewAllNews: RecyclerView = findViewById(R.id.recyclerViewAllNews)

        // Inisialisasi adapter dengan data awal berupa daftar kosong
        articlesAdapter = ArticlesAdapter(emptyList())
        allNewsAdapter = AllNewsAdapter(emptyList())

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = articlesAdapter

            // Access onItemClick directly
            articlesAdapter.onItemClick = { article ->
                val intent = Intent(this@MainActivity, DetailBeritaTerkini::class.java)
                intent.putExtra("ArticlesItem", article)
                startActivity(intent)
            }
        }

        recyclerViewAllNews.apply {
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
            adapter = allNewsAdapter

            allNewsAdapter.onItemClick = { article ->
                val intent = Intent(this@MainActivity, DetailAllNews::class.java)
                intent.putExtra("ArticlesItem", article)
                startActivity(intent)
            }
        }

        fetchNews()
        fetchAllNews()

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigationView.selectedItemId = R.id.bottom_home
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottom_home -> true
                R.id.bottom_search -> {
                    startActivity(Intent(applicationContext, SearchActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    finish()
                    true
                }
                R.id.bottom_save -> {
                    startActivity(Intent(applicationContext, BookmarkActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    finish()
                    true
                }
                else -> false
            }
        }

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

                    // Filter out articles with null values
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
                        // Update adapter with news data
                        articlesAdapter.articles = validArticles
                        articlesAdapter.notifyDataSetChanged()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
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