package dev.esan.sla_app.data.remote.api

import dev.esan.sla_app.data.remote.dto.insight.InsightIndicadoresDto
import dev.esan.sla_app.data.remote.dto.insight.InsightHistoricoDto
import dev.esan.sla_app.data.remote.dto.insight.InsightRegresionDto
import retrofit2.http.GET
import retrofit2.http.Query

interface DashboardApi {

    @GET("dashboard/indicadores")
    suspend fun getIndicadores(
        @Query("tipoSla") tipoSla: String
    ): InsightIndicadoresDto

    @GET("dashboard/historico")
    suspend fun getHistorico(
        @Query("tipoSla") tipoSla: String
    ): InsightHistoricoDto

    @GET("dashboard/regresion")
    suspend fun getRegresion(
        @Query("tipoSla") tipoSla: String
    ): InsightRegresionDto
}
