package dev.esan.sla_app.ui.alertas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.esan.sla_app.data.repository.AlertasRepository
import dev.esan.sla_app.data.remote.dto.alertas.AlertaDto
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
                _state.value = _state.value.copy(loading = true)
                val result = repo.cargarAlertas()
                val roles = result.map { it.rol }.distinct().sorted()
                
                _state.value = _state.value.copy(
                    loading = false,
                    data = result,
                    availableRoles = roles,
                    filteredData = applyFilter(result, _state.value.filterSeverity, _state.value.filterRole)
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(loading = false, error = e.message)
            }
        }
    }

    fun setFilter(severity: String?) {
        val currentSeverity = _state.value.filterSeverity
        val newSeverity = if (currentSeverity == severity) null else severity // Toggle off if same
        
        _state.value = _state.value.copy(
            filterSeverity = newSeverity,
            filteredData = applyFilter(_state.value.data, newSeverity, _state.value.filterRole)
        )
    }

    fun setRoleFilter(role: String?) {
        val currentRole = _state.value.filterRole
        val newRole = if (currentRole == role) null else role // Toggle off if same
        
        _state.value = _state.value.copy(
            filterRole = newRole,
            filteredData = applyFilter(_state.value.data, _state.value.filterSeverity, newRole)
        )
    }

    private fun applyFilter(list: List<AlertaDto>, severity: String?, role: String?): List<AlertaDto> {
        var result = list
        
        if (severity != null) {
            result = result.filter { alerta ->
                val s = when {
                    alerta.porcentaje < 40 -> "High"
                    alerta.porcentaje < 70 -> "Medium"
                    else -> "Low"
                }
                s == severity
            }
        }

        if (role != null) {
            result = result.filter { it.rol == role }
        }

        return result
    }
}