package dev.esan.sla_app.ui.sla

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.esan.sla_app.data.repository.SlaRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// La definición de IndicadoresState ahora vive en su propio archivo (IndicadoresState.kt)

class IndicadoresViewModel(
    private val repo: SlaRepository
) : ViewModel() {

    private val _state = MutableStateFlow(IndicadoresState())
    val state: StateFlow<IndicadoresState> = _state.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _state.value = IndicadoresState(loading = true)
            try {
                coroutineScope {
                    val indicadoresDeferred = async { repo.cargarIndicadores() }
                    val tiposSlaDeferred = async { repo.getTiposSla() }

                    val indicadores = indicadoresDeferred.await()
                    val tiposSla = tiposSlaDeferred.await()

                    _state.value = IndicadoresState(data = indicadores, tiposSla = tiposSla)
                }
            } catch (e: Exception) {
                _state.value = IndicadoresState(error = "Error al cargar datos: ${e.message}")
            }
        }
    }

    fun cargarIndicadores() {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(loading = true)
                val response = repo.cargarIndicadores()
                _state.value = _state.value.copy(loading = false, data = response)
            } catch (e: Exception) {
                _state.value = _state.value.copy(loading = false, error = e.message)
            }
        }
    }
}