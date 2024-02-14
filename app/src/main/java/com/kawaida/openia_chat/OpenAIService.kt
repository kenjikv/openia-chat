package com.kawaida.openia_chat

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface OpenAIService {
    @Headers("Authorization: Bearer AQUI_PONER_TU_TOKEN")
    @POST("v1/chat/completions")
    fun sendMessage(@Body body: ChatRequest): Call<ChatResponse>
}