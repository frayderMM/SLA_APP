package dev.esan.sla_app.data.remote.api

import dev.esan.sla_app.data.remote.dto.ResendEmailRequest
import dev.esan.sla_app.data.remote.dto.ResendEmailResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ResendApi {
    @POST("emails")
    suspend fun sendEmail(@Body request: ResendEmailRequest): Response<ResendEmailResponse>
}
