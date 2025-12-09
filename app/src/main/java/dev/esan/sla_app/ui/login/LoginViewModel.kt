package dev.esan.sla_app.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.auth0.android.jwt.JWT
import dev.esan.sla_app.data.datastore.DataStoreManager
import dev.esan.sla_app.data.model.AuthenticatedUser
import dev.esan.sla_app.data.remote.RetrofitClient
import dev.esan.sla_app.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException

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

                // Decodificar el token para obtener los datos del usuario
                val jwt = JWT(response.token)
                val user = AuthenticatedUser(
                    token = response.token,
                    id = jwt.subject ?: "",
                    email = jwt.getClaim("email").asString() ?: "",
                    role = jwt.getClaim("rol").asString() ?: "Usuario",
                    nombre = jwt.getClaim("nombre").asString()
                )

                // Guardar el objeto de usuario completo en el DataStore
                dataStore.saveUser(user)

                // Inyectar el token en el interceptor para la sesión actual
                RetrofitClient.authInterceptor.setToken(response.token)

                _state.value = LoginState(success = true)

            } catch (e: Exception) {
                when (e) {
                    is IOException -> {
                        _state.value = LoginState(error = "Error de red. Por favor, comprueba tu conexión a internet.")
                    }
                    else -> {
                        _state.value = LoginState(error = "Credenciales incorrectas. Por favor, vuelve a intentarlo.")
                    }
                }
            }
        }
    }
}