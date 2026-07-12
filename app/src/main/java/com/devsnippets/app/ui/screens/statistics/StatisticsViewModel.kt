package com.devsnippets.app.ui.screens.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devsnippets.app.domain.repository.SnippetRepository
import com.devsnippets.app.domain.repository.SnippetStatistics
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val repository: SnippetRepository
) : ViewModel() {

    private val _statistics = MutableStateFlow(
        SnippetStatistics(totalSnippets = 0, favoriteCount = 0, pinnedCount = 0, languageBreakdown = emptyMap())
    )
    val statistics: StateFlow<SnippetStatistics> = _statistics.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _isLoading.value = true
            _statistics.value = repository.getStatistics()
            _isLoading.value = false
        }
    }
}
