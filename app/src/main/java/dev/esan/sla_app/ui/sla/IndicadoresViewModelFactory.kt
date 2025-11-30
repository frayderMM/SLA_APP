package dev.esan.sla_app.ui.sla

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.esan.sla_app.data.repository.SlaRepository

class IndicadoresViewModelFactory(
    private val repo: SlaRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(IndicadoresViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return IndicadoresViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}