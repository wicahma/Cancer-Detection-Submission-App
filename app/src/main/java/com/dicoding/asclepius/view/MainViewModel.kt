package com.dicoding.asclepius.view

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.asclepius.data.CancerRepository
import com.dicoding.asclepius.data.local.entity.HistoryEntity
import com.dicoding.asclepius.utils.SettingPreferences
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: CancerRepository, private val pref: SettingPreferences
) : ViewModel() {

    private val _isDarkMode: MutableLiveData<Boolean> = MutableLiveData(false)
    val isDarkModeActive: LiveData<Boolean> = _isDarkMode

    private val _textResult: MutableLiveData<String> = MutableLiveData("")
    val textResult: LiveData<String> = _textResult

    private val _percentageResult: MutableLiveData<String> = MutableLiveData("")
    val percentageResult: LiveData<String> = _percentageResult

    fun getThemeSettings(): LiveData<Boolean> {
        return pref.getThemeSetting().asLiveData()
    }

    fun saveThemeSetting(isDarkModeActive: Boolean) {
        viewModelScope.launch {
            pref.saveThemeSetting(isDarkModeActive)
        }
    }

    fun saveResultScan(history: HistoryEntity) = repository.setHistory(history)

    fun changeDarkmode(value: Boolean?) {
        _isDarkMode.value = !value!!
    }

    fun changePercentage(value: String) {
        _percentageResult.value = value
    }

    fun changeResult(value: String) {
        _textResult.value = value
    }
}