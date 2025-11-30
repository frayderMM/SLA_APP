package dev.esan.sla_app.data.remote.dto.auth

data class LoginRequest(
    val email: String,
    val password: String
)