package dev.esan.sla_app.ui.insight

import dev.esan.sla_app.data.remote.dto.insight.*

data class InsightPanelState(
    val loading: Boolean = true,
    val tipoSla: String = "SLA1",
    val indicadores: InsightIndicadoresDto? = null,
    val historico: InsightHistoricoDto? = null,
    val regresion: InsightRegresionDto? = null,
    val error: String? = null
)
