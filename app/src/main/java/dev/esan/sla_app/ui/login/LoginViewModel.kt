package dev.esan.sla_app.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.esan.sla_app.data.datastore.DataStoreManager
import dev.esan.sla_app.data.remote.RetrofitClient
import dev.esan.sla_app.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repo: AuthRepository,
    private val dataStore: DataStoreManager
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                _state.value = LoginState(loading = true)

                val response = repo.login(email, password)

                // --- SOLUCIÓN DEL PUNTO #1 DE TU PLAN ---
                // 1. Guardar el token en DataStore para persistencia.
                dataStore.saveToken(response.token)

                // 2. Inyectar el token en el interceptor para la sesión actual.
                RetrofitClient.authInterceptor.setToken(response.token)
                // --- FIN DE LA SOLUCIÓN ---

                _state.value = LoginState(success = true)

            } catch (e: Exception) {
                _state.value = LoginState(error = "Credenciales incorrectas o error de red")
            }
        }
    }
}