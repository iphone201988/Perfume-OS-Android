package com.tech.perfumos.data.model

data class OpenAIRequest (
    //val model: String = "gpt-3.5-turbo",
    val model: String = "gpt-4o",
    val messages: List<Message>
)

data class Message(
    val role: String, // "user" or "system"
    val content: String
)