package dev.esan.sla_app.data.remote.dto.solicitudes

import com.google.gson.annotations.SerializedName
import dev.esan.sla_app.data.model.TipoSla

data class SolicitudDto(
    @SerializedName("id")
    val id: Int,

    @SerializedName("rol")
    val rol: String,

    // --- CORRECCIÓN ---
    // Se cambia el tipo de String a la data class TipoSla
    @SerializedName("tipoSla")
    val tipoSla: TipoSla,

    @SerializedName("fechaSolicitud")
    val fechaSolicitud: String,

    @SerializedName("fechaIngreso")
    val fechaIngreso: String?,

    @SerializedName("descripcion")
    val descripcion: String,

    @SerializedName("estado")
    val estado: String
)