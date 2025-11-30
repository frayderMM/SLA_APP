package dev.esan.sla_app.data.repository

import dev.esan.sla_app.data.remote.api.SolicitudesApi
import dev.esan.sla_app.data.remote.dto.solicitudes.CrearSolicitudRequest
import dev.esan.sla_app.data.remote.dto.solicitudes.EditarSolicitudRequest
import dev.esan.sla_app.data.remote.dto.solicitudes.SolicitudDto
import dev.esan.sla_app.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.io.IOException

class SolicitudesRepository(
    private val api: SolicitudesApi
) {

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

    // --- LLAMADAS A LA API CORREGIDAS Y ESTANDARIZADAS ---
    suspend fun getSolicitudes(): Resource<List<SolicitudDto>> = safeApiCall { api.getSolicitudes() }

    suspend fun getSolicitudPorId(id: Int): Resource<SolicitudDto> = safeApiCall { api.getSolicitudPorId(id) }

    suspend fun crearSolicitud(body: CrearSolicitudRequest): Resource<SolicitudDto> = safeApiCall { api.crearSolicitud(body) }

    suspend fun actualizarSolicitud(id: Int, body: EditarSolicitudRequest): Resource<SolicitudDto> = safeApiCall { api.actualizarSolicitud(id, body) }

    suspend fun eliminarSolicitud(id: Int): Resource<Unit> = safeApiCall { api.eliminarSolicitud(id) }
}