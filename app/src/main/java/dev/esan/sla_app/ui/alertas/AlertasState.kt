package dev.esan.sla_app.ui.alertas

import dev.esan.sla_app.data.remote.dto.alertas.AlertaDto

data class AlertasState(
    val loading: Boolean = true,
    val data: List<AlertaDto> = emptyList(),
    val filteredData: List<AlertaDto> = emptyList(),
    val filterSeverity: String? = null, // "High", "Medium", "Low"
    val filterRole: String? = null,
    val availableRoles: List<String> = emptyList(),
    val error: String? = null
)