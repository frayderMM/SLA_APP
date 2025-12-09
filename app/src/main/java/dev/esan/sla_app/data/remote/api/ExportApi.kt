package dev.esan.sla_app.data.remote.api

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ExportExcelApi {

    // Descargar Excel del año actual
    @GET("api/export/excel")
    suspend fun downloadExcelActual(): Response<ResponseBody>

    // Descargar Excel de un año específico
    @GET("api/export/excel/{year}")
    suspend fun downloadExcelByYear(
        @Path("year") year: Int
    ): Response<ResponseBody>
}
