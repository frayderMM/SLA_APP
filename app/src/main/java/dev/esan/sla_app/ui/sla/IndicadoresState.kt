package dev.esan.sla_app.ui.sla

import dev.esan.sla_app.data.remote.dto.sla.SlaIndicadorDto

data class IndicadoresState(
    val loading: Boolean = true,
    val data: List<SlaIndicadorDto> = emptyList(),
    val error: String? = null
)