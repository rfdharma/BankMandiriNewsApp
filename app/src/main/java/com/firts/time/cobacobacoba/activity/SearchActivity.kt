package com.firts.time.cobacobacoba.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firts.time.cobacobacoba.adapter.AllNewsAdapter
import com.first.time.cobacobacoba.api.ApiClient
import com.firts.time.cobacobacoba.R
import com.firts.time.cobacobacoba.activity.news.DetailAllNews
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchActivity : AppCompatActivity() {

    private lateinit var allNewsAdapter: AllNewsAdapter
    private var currentPage = 1
    private var isLoading = false
    private var currentQuery: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerViewAllNews)
        allNewsAdapter = AllNewsAdapter(emptyList())
        recyclerView.adapter = allNewsAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        allNewsAdapter.onItemClick = { article ->
            val intent = Intent(this@SearchActivity, DetailAllNews::class.java)
            intent.putExtra("ArticlesItem", article)
            startActivity(intent)
        }

        val searchView = findViewById<SearchView>(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    currentQuery = query
                    currentPage = 1
                    fetchAllNews(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // You can implement real-time filtering if needed
                return true
            }
        })

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        bottomNavigationView.selectedItemId = R.id.bottom_search
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottom_home -> {
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    finish()
                    true
                }
                R.id.bottom_search -> true

                R.id.bottom_user -> {
                    startActivity(Intent(applicationContext, ProfileActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    finish()
                    true
                }
                else -> false
            }
        }

        // Implement infinite scrolling here
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
                    currentQuery?.let { fetchAllNews(it) }
                }
            }
        })
    }

    private fun fetchAllNews(query: String) {
        val apiKey = "63a860ab3e8548b9bdcf5769dfb50a9d"

        val apiService = ApiClient.apiService
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.getEverything(query, currentPage, apiKey)
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
                        if (currentPage == 1) {
                            allNewsAdapter.newsList = validArticles
                        } else {
                            allNewsAdapter.newsList += validArticles
                        }
                        allNewsAdapter.notifyDataSetChanged()
                        isLoading = false
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
