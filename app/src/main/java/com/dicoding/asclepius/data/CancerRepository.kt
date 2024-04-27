package com.dicoding.asclepius.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.dicoding.asclepius.data.local.entity.HistoryEntity
import com.dicoding.asclepius.data.local.room.HistoryDao
import com.dicoding.asclepius.data.remote.response.ArticleResponse
import com.dicoding.asclepius.data.remote.response.Source
import com.dicoding.asclepius.data.remote.response.TopHeadlinesResponse
import com.dicoding.asclepius.data.remote.retrofit.ApiService
import com.dicoding.asclepius.utils.AppExecutors
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CancerRepository(
    private val apiService: ApiService,
    private val historyDao: HistoryDao,
    private val appExecutors: AppExecutors
) {

    fun getTopHeadlines(): LiveData<Result<List<ArticleResponse>>> {
        val result = MediatorLiveData<Result<List<ArticleResponse>>>()

        result.value = Result.Loading
        val client = apiService.topHeadlines()
        client.enqueue(object : Callback<TopHeadlinesResponse> {
            override fun onResponse(
                call: Call<TopHeadlinesResponse>, response: Response<TopHeadlinesResponse>
            ) {
                if (response.isSuccessful) {
                    val newsData = response.body()
                    val newsList = ArrayList<ArticleResponse>()
                    appExecutors.diskIO.execute {
                        newsData?.articles?.forEach { article ->
                            Log.d("Recycler View Article:", article?.description.toString())

                            val singleArticle = ArticleResponse(
                                publishedAt = article?.publishedAt,
                                author = article?.author,
                                urlToImage = article?.urlToImage,
                                description = article?.description,
                                source = Source(
                                    name = article?.source?.name, id = article?.source?.id
                                ),
                                title = article?.title,
                                url = article?.url,
                                content = article?.content
                            )
                            newsList.add(singleArticle)
                        }
                    }
                    result.value = Result.Success(newsList)
                }
            }

            override fun onFailure(call: Call<TopHeadlinesResponse>, t: Throwable) {
                result.value = Result.Error(t.message.toString())
            }
        })
        return result
    }

    fun setHistory(scanned: HistoryEntity) {
        appExecutors.diskIO.execute {
            historyDao.insertHistory(scanned)
        }
    }

    fun deleteAllHistory() {
        appExecutors.diskIO.execute {
            historyDao.deleteAll()
        }
    }

    fun getAllHistory(): LiveData<List<HistoryEntity>> {
        return historyDao.getAllHistory()
    }

    companion object {
        @Volatile
        private var instance: CancerRepository? = null
        fun getInstance(
            apiService: ApiService, historyDao: HistoryDao, appExecutors: AppExecutors
        ): CancerRepository = instance ?: synchronized(this) {
            instance ?: CancerRepository(apiService, historyDao, appExecutors)
        }.also { instance = it }
    }
}