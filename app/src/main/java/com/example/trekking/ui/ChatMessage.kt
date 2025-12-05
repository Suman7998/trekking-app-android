package com.example.trekking.ui

sealed class ChatMessage {
    data class User(val text: String) : ChatMessage()
    data class Bot(val text: String) : ChatMessage()
}
