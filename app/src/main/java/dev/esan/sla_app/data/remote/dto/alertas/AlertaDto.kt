package dev.esan.sla_app.data.remote.dto.alertas

data class AlertaDto(
    val id: Int,
    val mensaje: String,
    val rol: String,
    val porcentaje: Double,
    val fecha: String
)