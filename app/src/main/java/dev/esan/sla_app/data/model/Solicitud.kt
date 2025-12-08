package dev.esan.sla_app.data.model

/**
 * Representa una Solicitud devuelta por la API en la lista GET.
 */
data class Solicitud(
    val id: Int,
    val rol: String,
    val fechaSolicitud: String?, // ✅ HECHO NULABLE
    val fechaIngreso: String?,   // ✅ HECHO NULABLE
    val tipoSlaId: Int,
    val tipoSlaNombre: String? // Este ya era nulable, lo que es correcto
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
