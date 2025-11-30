package dev.esan.sla_app.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.esan.sla_app.data.datastore.DataStoreManager
import dev.esan.sla_app.data.model.AuthenticatedUser
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val dataStore: DataStoreManager
) : ViewModel() {

    private val _userProfileState = MutableStateFlow<AuthenticatedUser?>(null)
    val userProfileState: StateFlow<AuthenticatedUser?> = _userProfileState

    private val _logoutState = MutableSharedFlow<Boolean>()
    val logoutState = _logoutState.asSharedFlow()

    init {
        // ✨ Cargar el usuario desde el DataStore decodificado
        viewModelScope.launch {
            dataStore.getAuthenticatedUserFlow().collect { user ->
                _userProfileState.value = user
            }
        }
    }

    fun onLogoutClicked() {
        viewModelScope.launch {
            dataStore.clearToken()
            _logoutState.emit(true)
        }
    }
}