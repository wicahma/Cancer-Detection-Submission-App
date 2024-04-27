package com.dicoding.asclepius.di

import android.content.Context
import com.dicoding.asclepius.data.CancerRepository
import com.dicoding.asclepius.data.local.room.CancerDatabase
import com.dicoding.asclepius.data.remote.retrofit.ApiConfig
import com.dicoding.asclepius.utils.AppExecutors

object Injection {
    fun provideRepository(context: Context): CancerRepository {
        val apiService = ApiConfig.getApiService()
        val database = CancerDatabase.getInstance(context)
        val historyDao = database.historyDao()
        val appExecutors = AppExecutors()
        return CancerRepository.getInstance(apiService, historyDao, appExecutors)
    }
}