package dev.esan.sla_app.ui.user_management

import dev.esan.sla_app.data.remote.dto.auth.RolDto

sealed class UserManagementState {
    object Idle : UserManagementState()
    object Loading : UserManagementState()
    data class Success(val message: String) : UserManagementState()
    data class Error(val message: String) : UserManagementState()
}

data class UserFormState(
    val nombre: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val rolId: Int = 0,
    val roles: List<RolDto> = emptyList(),
    val isLoadingRoles: Boolean = false,
    val nombreError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val rolError: String? = null
)
