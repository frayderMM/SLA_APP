package dev.esan.sla_app.data.model

/**
 * Representa una Solicitud devuelta por la API en la lista GET.
 */
data class Solicitud(
    val id: Int,
    val rol: String,
    val fechaSolicitud: String, // Se mantiene como String para simplicidad
    val fechaIngreso: String,
    val tipoSlaId: Int,
    val tipoSlaNombre: String? // La API podría devolver datos adicionales
)

/**
 * DTO para crear una nueva solicitud.
 */
data class CreateSolicitudDto(
    val rol: String,
    val fechaSolicitud: String,
    val fechaIngreso: String,
    val tipoSlaId: Int
)

/**
 * DTO para actualizar una solicitud existente.
 */
data class UpdateSolicitudDto(
    val rol: String,
    val fechaSolicitud: String,
    val fechaIngreso: String,
    val tipoSlaId: Int
)
