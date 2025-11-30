package dev.esan.sla_app.data.remote.api

import dev.esan.sla_app.data.remote.dto.sla.SlaIndicadorDto
import retrofit2.http.GET

interface SlaApi {

    @GET("/api/sla/indicadores")
    suspend fun getIndicadores(): List<SlaIndicadorDto>
}