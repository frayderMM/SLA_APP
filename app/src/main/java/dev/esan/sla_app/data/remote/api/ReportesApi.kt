package dev.esan.sla_app.data.remote.api

import okhttp3.ResponseBody
import retrofit2.http.GET

interface ReportesApi {

    // Se usa ResponseBody para manejar la descarga de archivos
    @GET("/api/reportes/pdf")
    suspend fun descargarPdf(): ResponseBody
}