package com.dicoding.asclepius.view

import androidx.lifecycle.ViewModel
import com.dicoding.asclepius.data.CancerRepository

class ResultViewModel(private val repository: CancerRepository) : ViewModel() {
    fun getTopNews() = repository.getTopHeadlines()
}