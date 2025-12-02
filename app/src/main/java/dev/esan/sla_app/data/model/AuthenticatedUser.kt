package dev.esan.sla_app.data.model

data class AuthenticatedUser(
    val token: String,
    val id: String,
    val email: String,
    val role: String,
    val nombre: String? // El nombre puede no estar en todos los tokens
)
