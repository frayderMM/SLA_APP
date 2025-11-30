package dev.esan.sla_app.data.remote.dto.insight

data class InsightHistoricoDto(
    val tipoSla: String,
    val historico: List<InsightHistoricoItem>
)

data class InsightHistoricoItem(
    val periodo: String,
    val porcentaje: Double
)
