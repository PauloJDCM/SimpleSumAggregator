package com.example.simplesumaggregator

import kotlinx.serialization.Serializable

@Serializable
data class Entry(
    val groupId: String? = null,
    val itemId: String,
    val quantity: Int
)
