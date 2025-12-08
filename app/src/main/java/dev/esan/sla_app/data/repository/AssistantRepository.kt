package dev.esan.sla_app.data.repository

import dev.esan.sla_app.data.remote.api.AssistantApi
import dev.esan.sla_app.data.remote.dto.ChatRequest
import dev.esan.sla_app.data.remote.dto.ChatResponse
import retrofit2.Response

class AssistantRepository(
    private val api: AssistantApi
) {
    suspend fun sendMessage(request: ChatRequest): Response<ChatResponse> {
        return api.sendMessage(request)
    }
}