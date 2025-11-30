package dev.esan.sla_app.data.remote.dto.dashboard

data class DashboardItemDto(
    val mes: String,
    val rol: String,
    val total: Int,
    val cumplen: Int,
    val noCumplen: Int,
    val porcentaje: Double,
    val color: String
)