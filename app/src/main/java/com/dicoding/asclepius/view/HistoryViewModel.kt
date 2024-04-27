package com.dicoding.asclepius.view

import androidx.lifecycle.ViewModel
import com.dicoding.asclepius.data.CancerRepository

class HistoryViewModel(private val repository: CancerRepository) : ViewModel() {

    fun getAllHistory() = repository.getAllHistory()

    fun deleteAllHistory() = repository.deleteAllHistory()
}