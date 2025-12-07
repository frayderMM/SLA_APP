package dev.esan.sla_app.ui.solicitudes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.esan.sla_app.data.model.CreateSolicitudDto
import dev.esan.sla_app.data.model.Solicitud
import dev.esan.sla_app.data.model.TipoSla
import dev.esan.sla_app.data.model.UpdateSolicitudDto
import dev.esan.sla_app.data.repository.SolicitudesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant

data class SolicitudesListState(
    val isLoading: Boolean = false,
    val solicitudes: List<Solicitud> = emptyList(),
    val slaFilter: String = "Todos",
    val startDate: Long? = null, // <-- Filtro de fecha de inicio
    val endDate: Long? = null,   // <-- Filtro de fecha de fin
    val error: String? = null
)

data class FormState(
    val isLoading: Boolean = false,
    val tiposSla: List<TipoSla> = emptyList(),
    val error: String? = null,
    val navigateBack: Boolean = false
)

class SolicitudesViewModel(private val repository: SolicitudesRepository) : ViewModel() {

    private val _listState = MutableStateFlow(SolicitudesListState())
    val listState: StateFlow<SolicitudesListState> = _listState.asStateFlow()

    private val _formState = MutableStateFlow(FormState())
    val formState: StateFlow<FormState> = _formState.asStateFlow()

    private var allSolicitudes: List<Solicitud> = emptyList()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            loadTiposSla()
            loadSolicitudes()
        }
    }

    fun loadSolicitudes() {
        viewModelScope.launch {
            _listState.update { it.copy(isLoading = true, error = null) }
            try {
                allSolicitudes = repository.getSolicitudes()
                filterAndEnrichSolicitudes()
            } catch (e: Exception) {
                _listState.update { it.copy(isLoading = false, error = "Error al cargar solicitudes: ${e.message}") }
            }
        }
    }

    fun loadTiposSla() {
        viewModelScope.launch {
            _formState.update { it.copy(isLoading = true, error = null) }
            try {
                val tiposSla = repository.getTiposSla()
                _formState.update { it.copy(isLoading = false, tiposSla = tiposSla) }
            } catch (e: Exception) {
                _formState.update { it.copy(isLoading = false, error = "Error al cargar tipos de SLA: ${e.message}") }
            }
        }
    }

    fun onSlaFilterChanged(newSlaName: String) {
        _listState.update { it.copy(slaFilter = newSlaName) }
        filterAndEnrichSolicitudes()
    }

    fun onDateRangeSelected(startDate: Long?, endDate: Long?) {
        _listState.update { it.copy(startDate = startDate, endDate = endDate) }
        filterAndEnrichSolicitudes()
    }

    private fun filterAndEnrichSolicitudes() {
        val state = _listState.value
        val tiposSla = _formState.value.tiposSla

        val slaFilteredList = if (state.slaFilter == "Todos") {
            allSolicitudes
        } else {
            val slaId = tiposSla.find { it.nombre == state.slaFilter }?.id
            if (slaId != null) allSolicitudes.filter { it.tipoSlaId == slaId } else allSolicitudes
        }

        val dateFilteredList = if (state.startDate != null && state.endDate != null) {
            slaFilteredList.filter { solicitud ->
                try {
                    val solicitudDate = Instant.parse(solicitud.fechaSolicitud).toEpochMilli()
                    solicitudDate >= state.startDate && solicitudDate <= state.endDate
                } catch (e: Exception) {
                    false
                }
            }
        } else {
            slaFilteredList
        }

        // --- 🔥 ORDENAMIENTO AÑADIDO ---
        val sortedList = dateFilteredList.sortedByDescending { it.fechaSolicitud }

        val tiposSlaMap = tiposSla.associateBy { it.id }
        val enrichedList = sortedList.map { solicitud ->
            solicitud.copy(tipoSlaNombre = tiposSlaMap[solicitud.tipoSlaId]?.nombre)
        }

        _listState.update { it.copy(isLoading = false, solicitudes = enrichedList) }
    }

    fun createSolicitud(rol: String, fechaSolicitud: String, fechaIngreso: String, tipoSlaId: Int) {
        viewModelScope.launch {
            _formState.update { it.copy(isLoading = true, error = null) }
            try {
                repository.createSolicitud(CreateSolicitudDto(rol, fechaSolicitud, fechaIngreso, tipoSlaId))
                loadSolicitudes()
                _formState.update { it.copy(isLoading = false, navigateBack = true) }
            } catch (e: Exception) {
                _formState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun updateSolicitud(id: Int, rol: String, fechaSolicitud: String, fechaIngreso: String, tipoSlaId: Int) {
        viewModelScope.launch {
            _formState.update { it.copy(isLoading = true, error = null) }
            try {
                repository.updateSolicitud(id, UpdateSolicitudDto(rol, fechaSolicitud, fechaIngreso, tipoSlaId))
                loadSolicitudes()
                _formState.update { it.copy(isLoading = false, navigateBack = true) }
            } catch (e: Exception) {
                _formState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun deleteSolicitud(id: Int) {
        viewModelScope.launch {
            _listState.update { it.copy(isLoading = true, error = null) }
            try {
                repository.deleteSolicitud(id)
                loadSolicitudes()
            } catch (e: Exception) {
                _listState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun onDoneNavigating() {
        _formState.update { it.copy(navigateBack = false, error = null) }
    }
}