package com.jeerovan.icon.traces

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class IconItem(
    val name: String,
    val resId: Int,
    val foregroundResId: Int
)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val allIcons = mutableListOf<IconItem>()
    private val _visibleIcons = MutableStateFlow<List<IconItem>>(emptyList())
    val visibleIcons = _visibleIcons.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()


    init {
        viewModelScope.launch {
            // 1. Load ALL icons in background
            allIcons.addAll(IconRepository.getIcons(getApplication()))

            // 2. Publish only the first 100 to UI immediately so it renders FAST
            _visibleIcons.value = allIcons.take(100)
        }
    }
    // Call this when user scrolls to end
    fun loadNextPage() {
        val currentSize = _visibleIcons.value.size
        val nextChunk = allIcons.drop(currentSize).take(100)
        if (nextChunk.isNotEmpty()) {
            _visibleIcons.value += nextChunk
        }
    }
}