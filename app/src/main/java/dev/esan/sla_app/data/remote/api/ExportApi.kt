package dev.esan.sla_app.data.remote.api

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Streaming

interface ExportApi {

    /**
     * Endpoint para descargar el archivo Excel de solicitudes.
     * La anotación @Streaming ayuda a manejar archivos grandes de manera eficiente.
     */
    @Streaming
    @GET("api/exportar/solicitudes") // URL consistente con la acción
    suspend fun exportarSolicitudes(): Response<ResponseBody> // Nombre y tipo de retorno corregidos

}