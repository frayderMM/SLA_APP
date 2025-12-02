package dev.esan.sla_app.data.remote.api

import dev.esan.sla_app.data.remote.dto.auth.ChangePasswordRequest
import dev.esan.sla_app.data.remote.dto.auth.LoginRequest
import dev.esan.sla_app.data.remote.dto.auth.LoginResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("/api/auth/login")
    suspend fun login(@Body body: LoginRequest): LoginResponse

    @POST("/api/auth/change-password")
    suspend fun changePassword(@Body body: ChangePasswordRequest)
}