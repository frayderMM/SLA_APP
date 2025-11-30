package dev.esan.sla_app.data.repository

import dev.esan.sla_app.data.remote.api.ReportesApi
import okhttp3.ResponseBody
import retrofit2.Response

class ReportesRepository(private val api: ReportesApi) {

    /**
     * Llama a la API para descargar el reporte en PDF.
     * Pasa el tipo de SLA opcional a la API.
     */
    suspend fun downloadPdfReport(tipoSla: String?): Response<ResponseBody> {
        return api.downloadPdfReport(tipoSla)
    }
}
