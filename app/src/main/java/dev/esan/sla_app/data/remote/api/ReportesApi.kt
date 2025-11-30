package dev.esan.sla_app.data.remote.api

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Streaming

interface ReportesApi {

    /**
     * Descarga el reporte de SLA en formato PDF.
     * Se usa @Streaming para manejar respuestas grandes (archivos) de manera eficiente,
     * evitando cargar todo el archivo en memoria a la vez.
     */
    @Streaming
    @GET("api/Reportes/pdf")
    suspend fun downloadPdfReport(
        @Query("tipoSla") tipoSla: String? // El tipo de SLA es opcional
    ): Response<ResponseBody>
}
