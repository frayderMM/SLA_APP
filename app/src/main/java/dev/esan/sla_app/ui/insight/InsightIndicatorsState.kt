package dev.esan.sla_app.ui.insight

import dev.esan.sla_app.data.remote.dto.insight.*

data class InsightIndicatorsState(
    val loading: Boolean = false,
    val indicadores: InsightIndicadoresDto? = null,
    val historico: List<InsightPoint>? = null,
    val regresion: InsightRegresionDto? = null,
    val error: String? = null
)
