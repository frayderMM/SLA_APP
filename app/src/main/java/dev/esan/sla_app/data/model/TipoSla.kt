package dev.esan.sla_app.data.model

/**
 * Representa un Tipo de SLA devuelto por la API.
 * Ejemplo: { "id": 1, "codigo": "SLA1", "nombre": "SLA Nivel 1", ... }
 */
data class TipoSla(
    val id: Int,
    val codigo: String,
    val nombre: String,
    val descripcion: String,
    val tiempoRespuesta: Int
)
