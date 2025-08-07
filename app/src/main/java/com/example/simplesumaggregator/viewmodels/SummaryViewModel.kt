package com.example.simplesumaggregator.viewmodels

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import com.example.simplesumaggregator.Entry

class SummaryViewModel(entries: SnapshotStateList<Entry>) : ViewModel() {
    private val _byGroupAndItemId =
        entries.groupBy { it.groupId }
            .mapValues { (_, entries) ->
                entries.groupBy { it.itemId }
                    .mapValues { (_, entries) -> entries.sumOf { it.quantity } }
            }

    private val _byItemId =
        entries.groupBy { it.itemId }
            .mapValues { (_, entries) -> entries.sumOf { it.quantity } }

    val byGroupAndItemId get() = _byGroupAndItemId
    val byItemId get() = _byItemId
}