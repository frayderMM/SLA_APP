package dev.esan.sla_app.data.repository

import dev.esan.sla_app.data.remote.api.ExportApi
import dev.esan.sla_app.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.IOException

class ExportRepository(private val api: ExportApi) {

    private suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): Resource<T> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiCall()
                if (response.isSuccessful) {
                    response.body()?.let {
                        Resource.Success(it)
                    } ?: Resource.Error("Respuesta exitosa pero cuerpo vacío.")
                } else {
                    val errorMsg = response.errorBody()?.string() ?: "Sin mensaje de error."
                    Resource.Error("Error ${response.code()}: $errorMsg")
                }
            } catch (e: IOException) {
                Resource.Error("Error de red. Revisa tu conexión a internet.")
            } catch (e: Exception) {
                Resource.Error("Error inesperado: ${e.message}")
            }
        }
    }

    suspend fun exportarSolicitudes(): Resource<ResponseBody> = safeApiCall { api.exportarSolicitudes() }

}