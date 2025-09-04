package com.example.simplesumaggregator.views

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.simplesumaggregator.EntriesListState
import com.example.simplesumaggregator.Entry
import com.example.simplesumaggregator.viewmodels.HistoryViewModel
import com.example.simplesumaggregator.viewmodels.SavedWorkspace
import kotlinx.coroutines.launch
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun HistoryView(
    viewModel: HistoryViewModel,
    onBackClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val state by remember { derivedStateOf { viewModel.canSave } }

    val saveButtonBackgroundColor = if (state) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.inversePrimary
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column {
            //region Navigation Buttons
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(IntrinsicSize.Min)
                ) {
                    IconButton(
                        onClick = { onBackClick() },
                        modifier = Modifier.weight(1f),
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "History",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(64.dp)
                        )
                    }
                    VerticalDivider(thickness = 4.dp, color = MaterialTheme.colorScheme.primary)
                }
            }
            HorizontalDivider(thickness = 4.dp, color = MaterialTheme.colorScheme.primary)
            //endregion

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column {
                    Button(
                        onClick = {
                            scope.launch {
                                viewModel.saveCurrentWorkspace()?.let { errorMessage ->
                                    Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = saveButtonBackgroundColor
                        )
                    ) {
                        Text(
                            "Save Current Workspace",
                            style = MaterialTheme.typography.headlineSmall
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Recent Workspaces",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("${viewModel.savedWorkspacesList.size} / ${viewModel.maxSavedWorkspaces}", fontSize = 20.sp)
                }
                HorizontalDivider()
                Spacer(modifier = Modifier.height(4.dp))

                if (viewModel.savedWorkspacesList.isEmpty()) {
                    Text("No recent workspaces found.")
                } else {
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(viewModel.savedWorkspacesList, key = { it.savedOn }) { workspace ->
                            WorkspaceItem(workspaceInfo = workspace, onClick = {
                                viewModel.loadWorkspace(workspace)
                                onBackClick()
                            })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WorkspaceItem(workspaceInfo: SavedWorkspace, onClick: () -> Unit) {
    val contentColor = MaterialTheme.colorScheme.onPrimary

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .height(IntrinsicSize.Min)
            .clickable { onClick() }
            .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        WorkspaceItemField(
            "Saved",
            formatDateTimeString(workspaceInfo.savedOn),
            contentColor,
            Modifier.weight(.75f)
        )
        VerticalDivider(color = contentColor, thickness = 2.dp)
        WorkspaceItemField(
            "Entries",
            workspaceInfo.entries.size.toString(),
            contentColor,
            Modifier.weight(.25f)
        )
        VerticalDivider(color = contentColor, thickness = 2.dp)
        WorkspaceItemField(
            "Total",
            workspaceInfo.entries.sumOf { it.quantity }.toString(),
            contentColor,
            Modifier.weight(.25f)
        )
    }
}

@Composable
fun WorkspaceItemField(title: String, content: String, color: Color, modifier: Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = color)
        Text(content, fontSize = 18.sp, color = color)
    }
}

@Preview(showBackground = true)
@Composable
fun HistoryViewPreview() {
    val entries =
        listOf(
            Entry(groupId = null, itemId = "1", quantity = 5)
        ).toMutableStateList()

    MaterialTheme {
        HistoryView(viewModel = viewModel {
            HistoryViewModel(
                entries,
                10,
                mutableStateOf(EntriesListState.NOT_SAVED),
                File("")
            )
        })
    }
}

private fun formatDateTimeString(value: String): String {
    val dateTime = LocalDateTime.parse(value)
    val format = DateTimeFormatter.ofPattern("yyyy-MM-dd  HH:mm")
    return dateTime.format(format)
}