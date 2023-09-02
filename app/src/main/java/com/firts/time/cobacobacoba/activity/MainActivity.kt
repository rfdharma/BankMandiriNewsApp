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
import com.first.time.cobacobacoba.api.ApiClient
import com.firts.time.cobacobacoba.activity.news.DetailAllNews
import com.firts.time.cobacobacoba.activity.news.DetailTopNews
import com.firts.time.cobacobacoba.R
import com.firts.time.cobacobacoba.activity.news.AllNewsActivity
import com.firts.time.cobacobacoba.activity.news.TopNewsActivity
import com.firts.time.cobacobacoba.adapter.AllNewsAdapter
import com.firts.time.cobacobacoba.adapter.TopNewsAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var topNewsAdapter: TopNewsAdapter
    private lateinit var allNewsAdapter: AllNewsAdapter

    private var currentPageTopNews = 1
    private var currentPageAllNews = 1
    private var isLoadingTopNews = false
    private var isLoadingAllNews = false
    private var lastVisibleItemPosition = 0

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

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val layoutManager = layoutManager as LinearLayoutManager
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

                    if (!isLoadingTopNews && (visibleItemCount + lastVisibleItemPosition) >= totalItemCount - 5) {
                        // Load more data when the user is near the end
                        isLoadingTopNews = true
                        currentPageTopNews++
                        fetchNews()
                    }
                }
            })

            // Handle item click
            topNewsAdapter.onItemClick = { article ->
                val intent = Intent(this@MainActivity, DetailTopNews::class.java)
                intent.putExtra("ArticlesItem", article)
                startActivity(intent)
            }
        }

        recyclerViewAllNews.apply {
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
            adapter = allNewsAdapter

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val layoutManager = layoutManager as LinearLayoutManager
                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

                    if (!isLoadingAllNews && (visibleItemCount + lastVisibleItemPosition) >= totalItemCount - 5) {
                        // Load more data when the user is near the end
                        isLoadingAllNews = true
                        currentPageAllNews++
                        fetchAllNews()
                    }
                }
            })

            // Handle item click
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
                R.id.bottom_user -> {
                    startActivity(Intent(applicationContext, ProfileActivity::class.java))
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
                val response = apiService.getTopHeadlines(country, currentPageTopNews, apiKey)
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
                        // Append the new data to the adapter
                        topNewsAdapter.articles += validArticles
                        topNewsAdapter.notifyDataSetChanged()
                        isLoadingTopNews = false
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
                val response = apiService.getEverything(q, currentPageAllNews, apiKey)
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
                        // Append the new data to the adapter
                        allNewsAdapter.newsList += validArticles
                        allNewsAdapter.notifyDataSetChanged()
                        isLoadingAllNews = false
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
