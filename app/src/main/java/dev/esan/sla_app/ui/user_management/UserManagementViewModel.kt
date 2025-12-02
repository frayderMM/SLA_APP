package dev.esan.sla_app.ui.user_management

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.esan.sla_app.data.remote.dto.auth.RolDto
import dev.esan.sla_app.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserManagementViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow<UserManagementState>(UserManagementState.Idle)
    val state: StateFlow<UserManagementState> = _state.asStateFlow()

    private val _formState = MutableStateFlow(UserFormState())
    val formState: StateFlow<UserFormState> = _formState.asStateFlow()

    init {
        loadPredefinedRoles()
    }

    private fun loadPredefinedRoles() {
        // Roles predefinidos según la configuración de la API
        val rolesHardcoded = listOf(
            RolDto(id = 1, nombre = "Analista"),
            RolDto(id = 2, nombre = "General")
        )
        
        _formState.value = _formState.value.copy(
            roles = rolesHardcoded,
            rolId = rolesHardcoded[0].id,
            isLoadingRoles = false
        )
    }

    fun updateNombre(nombre: String) {
        _formState.value = _formState.value.copy(
            nombre = nombre,
            nombreError = null
        )
    }

    fun updateEmail(email: String) {
        _formState.value = _formState.value.copy(
            email = email,
            emailError = null
        )
    }

    fun updatePassword(password: String) {
        _formState.value = _formState.value.copy(
            password = password,
            passwordError = null
        )
    }

    fun updateConfirmPassword(confirmPassword: String) {
        _formState.value = _formState.value.copy(
            confirmPassword = confirmPassword,
            confirmPasswordError = null
        )
    }

    fun updateRol(rolId: Int) {
        _formState.value = _formState.value.copy(
            rolId = rolId,
            rolError = null
        )
    }

    fun registerUser() {
        if (!validateForm()) {
            return
        }

        viewModelScope.launch {
            _state.value = UserManagementState.Loading
            try {
                val response = authRepository.register(
                    nombre = _formState.value.nombre,
                    email = _formState.value.email,
                    password = _formState.value.password,
                    rolId = _formState.value.rolId
                )
                _state.value = UserManagementState.Success(
                    response.message ?: "Usuario registrado exitosamente"
                )
                resetForm()
            } catch (e: Exception) {
                _state.value = UserManagementState.Error(
                    e.message ?: "Error al registrar usuario"
                )
            }
        }
    }

    private fun validateForm(): Boolean {
        var isValid = true
        val currentState = _formState.value

        // Validar nombre
        if (currentState.nombre.isBlank()) {
            _formState.value = currentState.copy(nombreError = "El nombre es requerido")
            isValid = false
        }

        // Validar email
        if (currentState.email.isBlank()) {
            _formState.value = _formState.value.copy(emailError = "El correo es requerido")
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(currentState.email).matches()) {
            _formState.value = _formState.value.copy(emailError = "Correo inválido")
            isValid = false
        }

        // Validar contraseña
        if (currentState.password.isBlank()) {
            _formState.value = _formState.value.copy(passwordError = "La contraseña es requerida")
            isValid = false
        } else if (currentState.password.length < 6) {
            _formState.value = _formState.value.copy(
                passwordError = "La contraseña debe tener al menos 6 caracteres"
            )
            isValid = false
        }

        // Validar confirmación de contraseña
        if (currentState.confirmPassword.isBlank()) {
            _formState.value = _formState.value.copy(
                confirmPasswordError = "Confirme la contraseña"
            )
            isValid = false
        } else if (currentState.password != currentState.confirmPassword) {
            _formState.value = _formState.value.copy(
                confirmPasswordError = "Las contraseñas no coinciden"
            )
            isValid = false
        }

        // Validar rol
        if (currentState.rolId == 0) {
            _formState.value = _formState.value.copy(rolError = "Seleccione un rol")
            isValid = false
        }

        return isValid
    }

    private fun resetForm() {
        _formState.value = UserFormState(
            roles = _formState.value.roles,
            rolId = if (_formState.value.roles.isNotEmpty()) _formState.value.roles[0].id else 0
        )
    }

    fun resetState() {
        _state.value = UserManagementState.Idle
    }
}
