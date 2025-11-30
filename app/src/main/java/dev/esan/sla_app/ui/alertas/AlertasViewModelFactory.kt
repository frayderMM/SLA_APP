package dev.esan.sla_app.ui.alertas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.esan.sla_app.data.repository.AlertasRepository

class AlertasViewModelFactory(
    private val repo: AlertasRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlertasViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AlertasViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}