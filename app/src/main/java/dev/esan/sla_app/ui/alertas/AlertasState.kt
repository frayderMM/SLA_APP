package dev.esan.sla_app.ui.alertas

import dev.esan.sla_app.data.remote.dto.alertas.AlertaDto

data class AlertasState(
    val loading: Boolean = true,
    val data: List<AlertaDto> = emptyList(),
    val error: String? = null
)