package com.example.simplesumaggregator.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.example.simplesumaggregator.EntriesListState
import com.example.simplesumaggregator.Entry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File
import java.time.LocalDateTime

@Serializable
data class SavedWorkspace(
    val savedOn: String, val entries: List<Entry>
)

private const val SAVED_WORKSPACES_FILE_NAME = "recent_workspaces.json"

class HistoryViewModel(
    entries: SnapshotStateList<Entry>,
    maxWorkspaces: Int,
    listState: MutableState<EntriesListState>,
    appFolder: File
) : ViewModel() {
    private val _entries = entries
    private val _maxSavedWorkspaces = maxWorkspaces
    private val _savedWorkspacesList = loadSavedWorkspaces(appFolder).toMutableStateList()
    private var _listState = listState
    private val _saveFolder = appFolder

    val savedWorkspacesList get() = _savedWorkspacesList
    val canSave get() = _entries.isNotEmpty() && _listState.value == EntriesListState.NOT_SAVED
    val maxSavedWorkspaces get() = _maxSavedWorkspaces

    suspend fun saveCurrentWorkspace(): String? {
        if (_listState.value == EntriesListState.SAVED) return "Workspace already saved!"
        if (_entries.isEmpty()) return "No entries to save!"

        return try {
            addCurrentWorkspaceToList()
            saveWorkspaceListFile()
            _listState.value = EntriesListState.SAVED
            null
        } catch (e: Exception) {
            e.printStackTrace()
            "Error saving workspace file!"
        }
    }

    fun loadWorkspace(workspace: SavedWorkspace) {
        _entries.clear()
        _entries.addAll(workspace.entries)
        _listState.value = EntriesListState.SAVED
    }

    private fun loadSavedWorkspaces(folder: File): List<SavedWorkspace> {
        val file = File(folder, SAVED_WORKSPACES_FILE_NAME)
        return if (file.exists()) {
            try {
                val jsonString = file.readText()
                Json.decodeFromString<List<SavedWorkspace>>(jsonString)
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        } else {
            emptyList()
        }
    }

    private fun addCurrentWorkspaceToList() {
        val aggregatedEntries = _entries.groupBy { it.groupId to it.itemId }
            .map { (key, entries) ->
                Entry(
                    groupId = key.first,
                    itemId = key.second,
                    quantity = entries.sumOf { it.quantity }
                )
            }

        val newSavedWorkspace = SavedWorkspace(
            savedOn = LocalDateTime.now().toString(),
            entries = aggregatedEntries
        )
        _savedWorkspacesList.add(0, newSavedWorkspace)
        if (_savedWorkspacesList.size > _maxSavedWorkspaces) {
            _savedWorkspacesList.removeAt(_savedWorkspacesList.lastIndex)
        }
    }

    private suspend fun saveWorkspaceListFile(): String? {
        val savedWorkspacesFile = File(_saveFolder, SAVED_WORKSPACES_FILE_NAME)
        return try {
            withContext(Dispatchers.IO) {
                // Serialize a copy for data integrity
                val serialized = Json.encodeToString(_savedWorkspacesList.toList())
                savedWorkspacesFile.writeText(serialized)
            }
            null
        } catch (e: Exception) {
            e.printStackTrace()
            "Error saving recent workspaces file!"
        }
    }
}