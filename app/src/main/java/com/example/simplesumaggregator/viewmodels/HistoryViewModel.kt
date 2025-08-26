package com.example.simplesumaggregator.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.example.simplesumaggregator.EntriesListState
import com.example.simplesumaggregator.Entry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Serializable
data class SavedWorkspace(
    val savedOn: String, val entries: List<Entry>
)

private const val SAVED_WORKSPACES_FILE_NAME = "recent_workspaces.json"

class HistoryViewModel(
    entries: SnapshotStateList<Entry>,
    maxWorkspaces: Int,
    listState: EntriesListState,
    appFolder: File
) : ViewModel() {
    private val _entries = entries
    private val _maxRecentWorkspaces = maxWorkspaces
    private val _savedWorkspacesList = loadSavedWorkspaces(appFolder).toMutableStateList()
    private var _state = listState
    private val _saveFolder = appFolder

    val savedWorkspacesList get() = _savedWorkspacesList
    val canSave get() = _savedWorkspacesList.isNotEmpty() && _state == EntriesListState.NOT_SAVED

    suspend fun saveCurrentWorkspace(): String? {
        if (_state == EntriesListState.SAVED) return "Workspace already saved!"
        if (_entries.isEmpty()) return "No entries to save!"

        val dateTime = LocalDateTime.now()

        val fileDateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")
        val fileName = "workspace_${dateTime.format(fileDateTimeFormatter)}"
        val fileToSave = File(_saveFolder, "${fileName}.json")

        return try {
            withContext(Dispatchers.IO) {
                val entriesJson = Json.encodeToString(_entries.toList())
                fileToSave.writeText(entriesJson)
            }

            val newSavedWorkspace = SavedWorkspace(
                savedOn = dateTime.toString(), entries = _entries.toList() // Ensure to save a copy
            )
            _savedWorkspacesList.add(0, newSavedWorkspace)
            if (_savedWorkspacesList.size > _maxRecentWorkspaces) {
                _savedWorkspacesList.removeAt(_savedWorkspacesList.lastIndex)
            }
            saveWorkspaceListFile()
            _state = EntriesListState.SAVED
            null
        } catch (e: SerializationException) {
            e.printStackTrace()
            "Error serializing entries!"
        } catch (e: Exception) {
            e.printStackTrace()
            "Error saving workspace file!"
        }
    }

    fun loadWorkspace(workspace: SavedWorkspace) {
        _entries.clear()
        _entries.addAll(workspace.entries)
        _state = EntriesListState.SAVED
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