package com.tech.perfumos.data.model

data class OpenAIResponse(
    val choices: ArrayList<Choice>
)

data class Choice(
    val message: Message
)