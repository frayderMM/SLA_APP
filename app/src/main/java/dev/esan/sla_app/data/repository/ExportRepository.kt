package dev.esan.sla_app.data.remote.repository

import dev.esan.sla_app.data.datastore.DataStoreManager
import dev.esan.sla_app.data.remote.api.ExportExcelApi
import kotlinx.coroutines.flow.first
import okhttp3.ResponseBody
import retrofit2.Response

class ExportExcelRepository(
    private val api: ExportExcelApi,
    private val dataStore: DataStoreManager
) {

    suspend fun downloadExcel(year: Int): Response<ResponseBody> {

        // Si deseas forzar login antes de descargar:
        val token = dataStore.token.first()
        if (token.isNullOrBlank()) {
            return Response.error(401, ResponseBody.create(null, "Token vacío"))
        }

        // Año actual → usa downloadExcelActual()
        return if (year == java.time.LocalDate.now().year) {
            api.downloadExcelActual()
        } else {
            api.downloadExcelByYear(year)
        }
    }
}
