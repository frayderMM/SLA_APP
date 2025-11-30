package dev.esan.sla_app.ui.profile

data class ProfileState(
    val nombre: String = "",
    val email: String = "",
    val rol: String = "",
    val loading: Boolean = true
)