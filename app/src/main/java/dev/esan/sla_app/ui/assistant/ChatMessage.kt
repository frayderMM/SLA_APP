package dev.esan.sla_app.ui.assistant

data class ChatMessage(
    val text: String,
    val isFromUser: Boolean,
    val isError: Boolean = false
)
