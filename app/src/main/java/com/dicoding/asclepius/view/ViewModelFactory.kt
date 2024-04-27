package com.dicoding.asclepius.view

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.dicoding.asclepius.data.CancerRepository
import com.dicoding.asclepius.di.Injection
import com.dicoding.asclepius.utils.SettingPreferences

class ViewModelFactory private constructor(
    private val userRepository: CancerRepository,
    private val pref: SettingPreferences
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T =
        with(modelClass) {
            when {
                isAssignableFrom(MainViewModel::class.java) -> MainViewModel(userRepository, pref)
                isAssignableFrom(HistoryViewModel::class.java) -> HistoryViewModel(userRepository)
                isAssignableFrom(ResultViewModel::class.java) -> ResultViewModel(userRepository)

                else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        } as T

    companion object : ViewModelProvider.Factory {
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context, pref: SettingPreferences): ViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ViewModelFactory(Injection.provideRepository(context), pref)
            }.also { instance = it }
    }
}