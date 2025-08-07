package com.example.simplesumaggregator.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.simplesumaggregator.Entry
import com.example.simplesumaggregator.viewmodels.SummaryViewModel

@Composable
fun SummaryView(
    viewModel: SummaryViewModel,
    onBackClick: () -> Unit = {}
) {
    var state by remember { mutableStateOf(true) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
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
                    IconButton(
                        onClick = { state = !state },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            Icons.Filled.Menu,
                            contentDescription = "Toggle View",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(64.dp)
                        )
                    }
                }
            }

            HorizontalDivider(thickness = 4.dp, color = MaterialTheme.colorScheme.primary)

            if (state) {
                GroupAndItemIdSummary(viewModel.byGroupAndItemId)
            } else {
                ItemIdSummary(viewModel.byItemId)
            }
        }
    }
}

@Composable
fun GroupAndItemIdSummary(entries: Map<String?, Map<String, Int>>) {
    Column(modifier = Modifier.padding(8.dp)) {
        Text(
            "Group and Item ID Summary",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        HorizontalDivider()
        for (entry in entries) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .padding(top = 8.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val itemModifier = Modifier.weight(1f)
                EntryItemField(text = entry.key ?: "", modifier = Modifier.weight(0.5f))
                VerticalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer)
                Column(modifier = itemModifier) {
                    for (item in entry.value) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(IntrinsicSize.Min)
                        ) {
                            EntryItemField(text = item.key, modifier = itemModifier)
                            VerticalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer)
                            EntryItemField(text = item.value.toString(), modifier = itemModifier)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ItemIdSummary(entry: Map<String, Int>) {
    Column(modifier = Modifier.padding(8.dp)) {
        Text(
            "Item ID Summary",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        HorizontalDivider()
        for (item in entry) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .padding(top = 8.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val itemModifier = Modifier.weight(1f)
                EntryItemField(text = item.key, modifier = itemModifier)
                VerticalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer)
                EntryItemField(text = item.value.toString(), modifier = itemModifier)
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun SummaryViewPreview() {
    val entries = listOf(
        Entry(groupId = null, itemId = "Item 1", quantity = 5),
        Entry(groupId = "Group 2", itemId = "Item 2", quantity = 10),
        Entry(groupId = "Group 2", itemId = "Item 3", quantity = 100),
        Entry(groupId = "Group 3", itemId = "Item 3", quantity = 15),
        Entry(groupId = "Group 1234", itemId = "Item 12345", quantity = 11111111)
    ).toMutableStateList()

    MaterialTheme {
        SummaryView(viewModel = viewModel { SummaryViewModel(entries) })
    }
}