package dev.esan.sla_app.data.repository

import dev.esan.sla_app.data.remote.api.ReportesApi
import okhttp3.ResponseBody

class ReportesRepository(
    private val api: ReportesApi
) {

    suspend fun descargarPdf(): ResponseBody {
        return api.descargarPdf()
    }
}