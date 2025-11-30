package dev.esan.sla_app.data.repository

import dev.esan.sla_app.data.remote.api.AuthApi
import dev.esan.sla_app.data.remote.dto.auth.LoginRequest

class AuthRepository(private val api: AuthApi) {

    suspend fun login(email: String, password: String) =
        api.login(LoginRequest(email, password))
}