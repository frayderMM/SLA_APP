package dev.esan.sla_app.ui.login

data class LoginState(
    val loading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)