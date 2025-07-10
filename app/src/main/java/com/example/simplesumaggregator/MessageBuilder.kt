package com.example.simplesumaggregator

class MessageBuilder {
    private val _messages = mutableListOf<String>()

    val hasMessages get() = _messages.isNotEmpty()

    fun addMessage(message: String) {
        _messages.add(message)
    }

    fun build(): String {
        return _messages.joinToString("\n")
    }
}