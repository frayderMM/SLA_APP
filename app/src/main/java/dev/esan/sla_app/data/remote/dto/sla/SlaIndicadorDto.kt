package dev.esan.sla_app.data.remote.dto.sla

data class SlaIndicadorDto(
    val id: Int,
    val rol: String,
    val tipoSla: String,
    val fechaSolicitud: String,
    val fechaIngreso: String,
    val dias: Int,
    val resultado: String
)