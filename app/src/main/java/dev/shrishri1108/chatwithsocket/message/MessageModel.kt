package dev.shrishri1108.chatwithsocket.message

data class MessageModel ( val messageId: String,
    val message: String,
    val from: String,
    val to: String,
    val updatedAt: String,
    )
