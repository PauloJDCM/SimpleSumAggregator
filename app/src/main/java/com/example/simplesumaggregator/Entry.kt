package com.example.simplesumaggregator

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable
import java.util.concurrent.ThreadLocalRandom

@Serializable
@Immutable
data class Entry(
    val groupId: String? = null,
    val itemId: String,
    val quantity: Int,
    val id: Long = ThreadLocalRandom.current().nextLong()
)
