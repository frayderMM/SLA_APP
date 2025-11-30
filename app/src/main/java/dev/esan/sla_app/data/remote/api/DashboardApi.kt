package dev.esan.sla_app.data.remote.api

import dev.esan.sla_app.data.remote.dto.insight.InsightHistoricoDto
import dev.esan.sla_app.data.remote.dto.insight.InsightIndicadoresDto
import dev.esan.sla_app.data.remote.dto.insight.InsightRegresionDto
import retrofit2.http.GET
import retrofit2.http.Query

interface DashboardApi {

    @GET("api/dashboard/indicadores")
    suspend fun getIndicadores(
        @Query("tipoSla") tipoSla: String
    ): InsightIndicadoresDto

    @GET("api/dashboard/historico")
    suspend fun getHistorico(
        @Query("tipoSla") tipoSla: String
    ): InsightHistoricoDto

    @GET("api/dashboard/regresion")
    suspend fun getRegresion(
        @Query("tipoSla") tipoSla: String
    ): InsightRegresionDto
}
