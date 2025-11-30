package dev.esan.sla_app.data.remote.dto.solicitudes

data class CrearSolicitudRequest(
    val rol: String,
    val tipoSla: String,
    val fechaSolicitud: String,
    val descripcion: String
)