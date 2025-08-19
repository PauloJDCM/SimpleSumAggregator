package com.example.simplesumaggregator.views

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.simplesumaggregator.Entry
import com.example.simplesumaggregator.viewmodels.WorkspaceViewModel

@Composable
fun WorkspaceView(
    viewModel: WorkspaceViewModel,
    onSummaryClick: () -> Unit = {},
    onHistoryClick: () -> Unit = {}
) {
    var groupId by remember { mutableStateOf("") }
    var itemId by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    val context = LocalContext.current


    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column {
            //region Navigation Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
            ) {
                IconButton(
                    onClick = { onHistoryClick() },
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "History",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(64.dp)
                    )
                }
                VerticalDivider(thickness = 4.dp, color = MaterialTheme.colorScheme.primary)
                IconButton(
                    onClick = { onSummaryClick() },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.List,
                        contentDescription = "History",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(64.dp)
                    )
                }
            }
            HorizontalDivider(thickness = 4.dp, color = MaterialTheme.colorScheme.primary)
            //endregion

            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                //region Add Entry Form
                Text("Add Entry", style = MaterialTheme.typography.headlineMedium)
                HorizontalDivider()
                OutlinedTextField(
                    value = groupId,
                    onValueChange = { groupId = it },
                    label = { Text("Group ID (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row {
                    OutlinedTextField(
                        value = itemId,
                        onValueChange = { itemId = it },
                        label = { Text("Item ID") },
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    OutlinedTextField(
                        value = quantity,
                        onValueChange = { quantity = it.trimEnd() },
                        label = { Text("Quantity") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        viewModel.addEntry(groupId, itemId, quantity)?.let { errorMessage ->
                            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
                        }
                        itemId = ""
                        quantity = ""
                    },
                    modifier = Modifier.width(320.dp)
                ) {
                    Text("Add Entry", fontSize = 24.sp)
                }
                //endregion

                Spacer(modifier = Modifier.height(16.dp))

                //region Entry List
                Text("Entries", style = MaterialTheme.typography.headlineMedium)
                HorizontalDivider()
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(viewModel.entries.reversed()) { entry ->
                        EntryItem(entry = entry, onDelete = { viewModel.removeEntry(entry) })
                    }
                }
                //endregion
            }
        }
    }
}

@Composable
fun EntryItem(entry: Entry, onDelete: () -> Unit) {
    var showMenu by remember { mutableStateOf(false) }

    val backgroundColor = if (showMenu) {
        MaterialTheme.colorScheme.tertiaryContainer
    } else {
        MaterialTheme.colorScheme.primaryContainer
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(top = 8.dp)
            .background(backgroundColor)
            .combinedClickable(
                onClick = { },
                onLongClick = { showMenu = true }
            ),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val itemModifier = Modifier.weight(1f)
        EntryItemField(text = entry.groupId ?: "", modifier = itemModifier)
        VerticalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer)
        EntryItemField(text = entry.itemId, modifier = itemModifier)
        VerticalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer)
        EntryItemField(text = entry.quantity.toString(), modifier = itemModifier)

        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
        ) {
            androidx.compose.material3.DropdownMenuItem(
                text = { Text("Delete") },
                onClick = {
                    onDelete()
                    showMenu = false
                },
                leadingIcon = {
                    Icon(Icons.Filled.Delete, contentDescription = "Delete")
                }
            )
        }
    }
}

@Composable
fun EntryItemField(text: String, modifier: Modifier) {
    Text(
        text = text,
        textAlign = TextAlign.Center,
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onPrimaryContainer,
        modifier = modifier.padding(4.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun WorkspaceViewPreview() {
    val entries = listOf(
        Entry(groupId = null, itemId = "Item 1", quantity = 5),
        Entry(groupId = "Group 2", itemId = "Item 2", quantity = 10),
        Entry(groupId = "Group 3", itemId = "Item 3", quantity = 15),
        Entry(groupId = "Group 1234", itemId = "Item 12345", quantity = 11111111)
    ).toMutableStateList()

    MaterialTheme {
        WorkspaceView(viewModel = viewModel { WorkspaceViewModel(entries) })
    }
}