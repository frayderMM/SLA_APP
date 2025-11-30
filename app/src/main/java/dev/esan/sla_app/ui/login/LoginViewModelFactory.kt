package dev.esan.sla_app.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.esan.sla_app.data.datastore.DataStoreManager
import dev.esan.sla_app.data.repository.AuthRepository

class LoginViewModelFactory(
    private val repo: AuthRepository,
    private val dataStore: DataStoreManager
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(repo, dataStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}