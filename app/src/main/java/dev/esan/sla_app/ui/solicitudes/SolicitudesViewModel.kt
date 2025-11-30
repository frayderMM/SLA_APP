package dev.esan.sla_app.ui.solicitudes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.esan.sla_app.data.remote.dto.solicitudes.CrearSolicitudRequest
import dev.esan.sla_app.data.remote.dto.solicitudes.EditarSolicitudRequest
import dev.esan.sla_app.data.repository.SolicitudesRepository
import dev.esan.sla_app.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SolicitudesViewModel(
    private val repo: SolicitudesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SolicitudesState())
    val state: StateFlow<SolicitudesState> = _state.asStateFlow()

    init {
        cargarSolicitudes()
    }

    fun cargarSolicitudes() {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null)

            // --- CORRECCIÓN: Se usa el método correcto "getSolicitudes()" del repositorio ---
            when (val result = repo.getSolicitudes()) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(loading = false, data = result.data ?: emptyList())
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(loading = false, error = result.message)
                }
                is Resource.Loading -> {
                    _state.value = _state.value.copy(loading = true)
                }
            }
        }
    }

    fun crearSolicitud(req: CrearSolicitudRequest, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null)
            // --- CORRECCIÓN: Se usa el método correcto "crearSolicitud()" del repositorio ---
            when (val result = repo.crearSolicitud(req)) {
                is Resource.Success -> {
                    cargarSolicitudes()
                    onSuccess()
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(loading = false, error = result.message)
                }
                is Resource.Loading -> { /* ya estamos en loading */ }
            }
        }
    }

    fun editarSolicitud(id: Int, req: EditarSolicitudRequest, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null)
            // --- CORRECCIÓN: Se usa el método correcto "actualizarSolicitud()" del repositorio ---
            when (val result = repo.actualizarSolicitud(id, req)) {
                is Resource.Success -> {
                    cargarSolicitudes()
                    onSuccess()
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(loading = false, error = result.message)
                }
                is Resource.Loading -> { /* ya estamos en loading */ }
            }
        }
    }

    fun eliminarSolicitud(id: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(loading = true, error = null)
            // --- CORRECCIÓN: Se usa el método correcto "eliminarSolicitud()" del repositorio ---
            when (val result = repo.eliminarSolicitud(id)) {
                is Resource.Success -> {
                    cargarSolicitudes()
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(loading = false, error = result.message)
                }
                is Resource.Loading -> { /* ya estamos en loading */ }
            }
        }
    }
}