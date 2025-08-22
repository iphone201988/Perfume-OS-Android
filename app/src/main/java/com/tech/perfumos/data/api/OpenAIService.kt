package com.tech.perfumos.data.api

import com.tech.perfumos.data.model.OpenAIRequest
import com.tech.perfumos.data.model.OpenAIResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.Call

interface OpenAIService {
    @POST("v1/chat/completions")
    fun getPerfumeName(
        @Header("Authorization") token: String,
        @Body request: OpenAIRequest
    ): Call<OpenAIResponse>
}