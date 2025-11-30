package dev.esan.sla_app.ui.sla

import dev.esan.sla_app.data.model.TipoSla
import dev.esan.sla_app.data.remote.dto.sla.SlaIndicadorDto

/**
 * Estado para la pantalla de Indicadores.
 * Contiene la lista de indicadores, la lista de tipos de SLA para el filtro,
 * el estado de carga y los posibles errores.
 */
data class IndicadoresState(
    val loading: Boolean = false,
    val data: List<SlaIndicadorDto> = emptyList(),
    val tiposSla: List<TipoSla> = emptyList(), // Esta propiedad es para el filtro dinámico
    val error: String? = null
)
