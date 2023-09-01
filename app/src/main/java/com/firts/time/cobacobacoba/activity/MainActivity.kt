package com.firts.time.cobacobacoba.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.TextView
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firts.time.cobacobacoba.adapter.AllNewsAdapter
import com.first.time.cobacobacoba.api.ApiClient
import com.firts.time.cobacobacoba.activity.news.DetailAllNews
import com.firts.time.cobacobacoba.activity.news.DetailTopNews
import com.firts.time.cobacobacoba.R
import com.firts.time.cobacobacoba.activity.news.AllNewsActivity
import com.firts.time.cobacobacoba.activity.news.TopNewsActivity
import com.firts.time.cobacobacoba.adapter.TopNewsAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var topNewsAdapter: TopNewsAdapter
    private lateinit var allNewsAdapter: AllNewsAdapter

    fun openAllNewsPage(view: View) {
        val textView = findViewById<TextView>(R.id.semuaberita)
        val text = textView.text.toString()

        // Membuat SpannableString dengan efek underline
        val spannable = SpannableString(text)
        spannable.setSpan(UnderlineSpan(), 0, text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        textView.text = spannable
        val intent = Intent(this, AllNewsActivity::class.java)
        startActivity(intent)
    }

    fun openTopNewsPage(view: View) {
        val textView = findViewById<TextView>(R.id.beritaterkini)
        val text = textView.text.toString()

        // Membuat SpannableString dengan efek underline
        val spannable = SpannableString(text)
        spannable.setSpan(UnderlineSpan(), 0, text.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        textView.text = spannable
        val intent = Intent(this, TopNewsActivity::class.java)
        startActivity(intent)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread.sleep(300)
        installSplashScreen()
        setContentView(R.layout.activity_main)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        val recyclerViewAllNews: RecyclerView = findViewById(R.id.recyclerViewAllNews)

        // Inisialisasi adapter dengan data awal berupa daftar kosong
        topNewsAdapter = TopNewsAdapter(emptyList())
        allNewsAdapter = AllNewsAdapter(emptyList())

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = topNewsAdapter

            // Access onItemClick directly
            topNewsAdapter.onItemClick = { article ->
                val intent = Intent(this@MainActivity, DetailTopNews::class.java)
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
                        topNewsAdapter.articles = validArticles
                        topNewsAdapter.notifyDataSetChanged()
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
