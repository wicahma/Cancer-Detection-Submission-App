package com.dicoding.asclepius.data.remote.retrofit

import com.dicoding.asclepius.data.remote.response.TopHeadlinesResponse
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("top-headlines?category=health&pageSize=20")
    fun topHeadlines(): Call<TopHeadlinesResponse>
}