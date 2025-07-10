package com.example.simplesumaggregator.viewmodels

import androidx.compose.runtime.toMutableStateList
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import com.example.simplesumaggregator.Entry

class WorkspaceViewModel(entries: List<Entry> = listOf()) : ViewModel() {
    private val _entries = entries.toMutableStateList()

    val entries get() = _entries

    fun addEntry(groupId: String, itemId: String, quantity: String) {
        if (itemId.isNotBlank() && quantity.isNotBlank() && quantity.isDigitsOnly()) {
            val entryGroupId = if (groupId.isBlank()) null else groupId.trim()
            _entries.add(Entry(entryGroupId, itemId, quantity.toInt()))
        }
    }
}