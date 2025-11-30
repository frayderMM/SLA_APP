package dev.esan.sla_app.data.remote.dto.insight

data class InsightIndicadoresDto(
    val tipoSla: String,
    val total: Int,
    val cumple: Int,
    val noCumple: Int,
    val porcentajeCumplimiento: Double,
    val promedioDias: Double
)