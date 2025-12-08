package dev.esan.sla_app.data.remote.api

import dev.esan.sla_app.data.remote.dto.ChatRequest
import dev.esan.sla_app.data.remote.dto.ChatResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AssistantApi {

    @POST("api/chat")
    suspend fun sendMessage(
        @Body request: ChatRequest
    ): Response<ChatResponse>
}