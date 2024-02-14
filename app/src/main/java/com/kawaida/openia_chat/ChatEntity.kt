package com.kawaida.openia_chat

data class ChatRequest(
    val model: String,
    val temperature: Int,
    val messages: List<Message>
)

data class Message(
    val role: String,
    val content: String
)

data class ChatResponse(
    val choices: List<Choice>
)

data class Choice(
    val message: MessageContent
)

data class MessageContent(
    val content: String
)

data class ChatMessage(
    val content: String,
    val isUserMessage: Boolean
)

