package dev.esan.sla_app.data.remote.api

import dev.esan.sla_app.data.remote.dto.auth.ChangePasswordRequest
import dev.esan.sla_app.data.remote.dto.auth.LoginRequest
import dev.esan.sla_app.data.remote.dto.auth.LoginResponse
import dev.esan.sla_app.data.remote.dto.auth.RegisterRequest
import dev.esan.sla_app.data.remote.dto.auth.RegisterResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("/api/auth/login")
    suspend fun login(@Body body: LoginRequest): LoginResponse

    @POST("/api/auth/change-password")
    suspend fun changePassword(@Body body: ChangePasswordRequest)

    @POST("/api/auth/register")
    suspend fun register(@Body body: RegisterRequest): RegisterResponse
}