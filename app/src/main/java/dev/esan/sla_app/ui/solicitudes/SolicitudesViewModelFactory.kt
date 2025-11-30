package dev.esan.sla_app.ui.solicitudes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.esan.sla_app.data.repository.SolicitudesRepository

class SolicitudesViewModelFactory(
    private val repo: SolicitudesRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SolicitudesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SolicitudesViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}