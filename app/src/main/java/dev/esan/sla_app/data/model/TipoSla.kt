package dev.esan.sla_app.data.model

import com.google.gson.annotations.SerializedName

// Esta clase representa el objeto anidado "tipoSla" que viene del backend.
data class TipoSla(
    @SerializedName("idTipoSla")
    val id: Int,

    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("tiempoRespuestaHoras")
    val tiempoRespuesta: Int
)
