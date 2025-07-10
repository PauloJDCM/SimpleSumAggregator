package com.example.simplesumaggregator.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.simplesumaggregator.Entry
import com.example.simplesumaggregator.viewmodels.WorkspaceViewModel

@Composable
fun WorkspaceView(viewModel: WorkspaceViewModel, padding: PaddingValues = PaddingValues()) {
    var groupId by remember { mutableStateOf("") }
    var itemId by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Workspace", style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(24.dp))
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
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(16.dp))
                OutlinedTextField(
                    value = quantity,
                    onValueChange = { quantity = it },
                    label = { Text("Quantity") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    viewModel.addEntry(groupId, itemId, quantity)
                    itemId = ""
                    quantity = ""
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 64.dp)
            ) {
                Text("Add Entry", fontSize = 24.sp)
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text("Entries", style = MaterialTheme.typography.headlineMedium)
            HorizontalDivider()
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                items(viewModel.entries.reversed()) { entry ->
                    EntryItemView(entry)
                }
            }
        }
    }
}

@Composable
fun EntryItemView(entry: Entry) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .background(MaterialTheme.colorScheme.primaryContainer),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        val itemModifier = Modifier.weight(1f)
        EntryItem(text = entry.groupId ?: "", modifier = itemModifier)
        EntryItem(text = entry.itemId, modifier = itemModifier)
        EntryItem(text = entry.quantity.toString(), modifier = itemModifier)
    }
}

@Composable
fun EntryItem(text: String, modifier: Modifier) {
    Text(
        text = text,
        textAlign = TextAlign.Center,
        fontSize = 20.sp,
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
        Entry(groupId = "Group 3", itemId = "Item 3", quantity = 15)
    )

    MaterialTheme {
        WorkspaceView(viewModel = WorkspaceViewModel(entries))
    }
}