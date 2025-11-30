package dev.esan.sla_app.data.remote.api

import dev.esan.sla_app.data.remote.dto.alertas.AlertaDto
import retrofit2.http.GET

interface AlertasApi {

    @GET("/api/alertas")
    suspend fun getAlertas(): List<AlertaDto>
}