package com.first.time.cobacobacoba.model

import com.firts.time.cobacobacoba.model.ResponseApi
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.Response

interface ApiInterface {
    @GET("top-headlines")
    suspend fun getTopHeadlines(
        @Query("country") country: String,
        @Query("page") p: Int,
        @Query("apiKey") apiKey: String
    ): Response<ResponseApi>

    @GET("everything")
    suspend fun getEverything(
        @Query("q") q: String,
        @Query("page") p: Int,
        @Query("apiKey") apiKey: String
    ): Response<ResponseApi>
}

