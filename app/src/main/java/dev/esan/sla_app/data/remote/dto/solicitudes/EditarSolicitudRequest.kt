package dev.esan.sla_app.data.remote.dto.solicitudes

data class EditarSolicitudRequest(
    val tipoSla: String,
    val fechaIngreso: String?,
    val descripcion: String,
    val estado: String
)