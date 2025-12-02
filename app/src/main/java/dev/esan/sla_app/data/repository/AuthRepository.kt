package dev.esan.sla_app.data.repository

import dev.esan.sla_app.data.remote.api.AuthApi
import dev.esan.sla_app.data.remote.dto.auth.ChangePasswordRequest
import dev.esan.sla_app.data.remote.dto.auth.LoginRequest
import dev.esan.sla_app.data.remote.dto.auth.RegisterRequest

class AuthRepository(private val api: AuthApi) {

    suspend fun login(email: String, password: String) =
        api.login(LoginRequest(email, password))

    suspend fun changePassword(currentPassword: String, newPassword: String) {
        api.changePassword(ChangePasswordRequest(currentPassword, newPassword))
    }

    suspend fun register(nombre: String, email: String, password: String, rolId: Int) =
        api.register(RegisterRequest(nombre, email, password, rolId))
}