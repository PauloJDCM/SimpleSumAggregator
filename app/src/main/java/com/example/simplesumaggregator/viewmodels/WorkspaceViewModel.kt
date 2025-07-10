package com.example.simplesumaggregator.viewmodels

import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.example.simplesumaggregator.Entry
import com.example.simplesumaggregator.MessageBuilder

class WorkspaceViewModel(entries: List<Entry> = listOf()) : ViewModel() {
    private val _entries = entries.toMutableStateList()

    val entries get() = _entries

    /**
     * Adds a new entry to the workspace.
     *
     * @param groupId The ID of the group the entry belongs to (optional).
     * @param itemId The ID of the item.
     * @param quantity The quantity of the item.
     * @return An error message if validation fails, otherwise null.
     */
    fun addEntry(groupId: String, itemId: String, quantity: String) : String? {
        val entryGroupId = groupId.trim().ifBlank { null }
        val entryItemId = itemId.trim()
        val entryQuantity = quantity.trim().toIntOrNull()

        val errorBuilder = MessageBuilder()
        if (entryItemId.isBlank()) errorBuilder.addMessage("Item ID cannot be blank!")
        if (entryQuantity == null) errorBuilder.addMessage("Quantity must be a number!")
        if (errorBuilder.hasMessages) return errorBuilder.build()

        _entries.add(Entry(entryGroupId, itemId, quantity.toInt()))
        return null
    }
}