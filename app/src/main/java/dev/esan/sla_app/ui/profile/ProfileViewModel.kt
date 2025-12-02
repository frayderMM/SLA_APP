package dev.esan.sla_app.ui.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.esan.sla_app.data.datastore.DataStoreManager
import dev.esan.sla_app.data.model.AuthenticatedUser
import dev.esan.sla_app.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val dataStore: DataStoreManager,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _userProfileState = MutableStateFlow<AuthenticatedUser?>(null)
    val userProfileState: StateFlow<AuthenticatedUser?> = _userProfileState

    private val _logoutState = MutableSharedFlow<Boolean>()
    val logoutState = _logoutState.asSharedFlow()

    private val _changePasswordResult = MutableSharedFlow<Result<Unit>>()
    val changePasswordResult = _changePasswordResult.asSharedFlow()

    init {
        viewModelScope.launch {
            dataStore.getAuthenticatedUserFlow().collect { user ->
                _userProfileState.value = user
            }
        }
    }

    fun onLogoutClicked() {
        viewModelScope.launch {
            dataStore.clearUser()
            _logoutState.emit(true)
        }
    }

    fun changePassword(currentPassword: String, newPassword: String) {
        viewModelScope.launch {
            try {
                authRepository.changePassword(currentPassword, newPassword)
                _changePasswordResult.emit(Result.success(Unit))
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error changing password", e)
                _changePasswordResult.emit(Result.failure(e))
            }
        }
    }
}