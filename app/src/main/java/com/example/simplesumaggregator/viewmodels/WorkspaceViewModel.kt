package com.example.simplesumaggregator.viewmodels

import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.example.simplesumaggregator.Entry
import com.example.simplesumaggregator.MessageBuilder

class WorkspaceViewModel(entries: List<Entry> = listOf()) : ViewModel() {
    private val _entries = entries.toMutableStateList()
    private val _maxIdLength = 10

    val entries get() = _entries

    /**
     * Adds a new entry to the workspace.
     *
     * @param groupId The ID of the group the entry belongs to (optional).
     * @param itemId The ID of the item.
     * @param quantity The quantity of the item.
     * @return An error message if validation fails, otherwise null.
     */
    fun addEntry(groupId: String, itemId: String, quantity: String): String? {
        val entryGroupId = groupId.trim().ifBlank { null }
        val entryItemId = itemId.trim()
        val entryQuantity = quantity.trim().toIntOrNull()

        val errorBuilder = MessageBuilder()
        if (entryGroupId != null && entryGroupId.length > _maxIdLength) errorBuilder.addMessage("Group ID cannot be longer than $_maxIdLength characters!")
        if (entryItemId.isBlank()) errorBuilder.addMessage("Item ID cannot be blank!")
        if (entryItemId.length > _maxIdLength) errorBuilder.addMessage("Item ID cannot be longer than $_maxIdLength characters!")
        if (entryQuantity == null || entryQuantity <= 0) errorBuilder.addMessage("Quantity must be a positive number!")
        if (errorBuilder.hasMessages) return errorBuilder.build()

        _entries.add(Entry(entryGroupId, itemId, quantity.toInt()))
        return null
    }
}