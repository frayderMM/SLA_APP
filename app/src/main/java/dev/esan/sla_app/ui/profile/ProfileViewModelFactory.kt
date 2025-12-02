package dev.esan.sla_app.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.esan.sla_app.data.datastore.DataStoreManager
import dev.esan.sla_app.data.repository.AuthRepository

class ProfileViewModelFactory(
    private val dataStore: DataStoreManager,
    private val authRepository: AuthRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(dataStore, authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}