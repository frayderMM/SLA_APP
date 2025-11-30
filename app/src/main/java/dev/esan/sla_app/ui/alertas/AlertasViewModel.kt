package dev.esan.sla_app.ui.alertas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.esan.sla_app.data.repository.AlertasRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AlertasViewModel(
    private val repo: AlertasRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AlertasState())
    val state: StateFlow<AlertasState> = _state.asStateFlow()

    init {
        cargarAlertas()
    }

    fun cargarAlertas() {
        viewModelScope.launch {
            try {
                _state.value = AlertasState(loading = true)
                val result = repo.cargarAlertas()
                _state.value = AlertasState(loading = false, data = result)
            } catch (e: Exception) {
                _state.value = AlertasState(loading = false, error = e.message)
            }
        }
    }
}