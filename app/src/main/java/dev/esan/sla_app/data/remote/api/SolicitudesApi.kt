package dev.esan.sla_app.data.remote.api

import dev.esan.sla_app.data.model.CreateSolicitudDto
import dev.esan.sla_app.data.model.Solicitud
import dev.esan.sla_app.data.model.UpdateSolicitudDto
import retrofit2.Response
import retrofit2.http.*

/**
 * Interfaz de la API para el CRUD de Solicitudes.
 */
interface SolicitudesApi {

    @GET("api/solicitudes")
    suspend fun getSolicitudes(): List<Solicitud>

    @GET("api/solicitudes/{id}")
    suspend fun getSolicitudById(@Path("id") id: Int): Solicitud

    @POST("api/solicitudes")
    suspend fun createSolicitud(@Body solicitud: CreateSolicitudDto): Response<Solicitud>

    @PUT("api/solicitudes/{id}")
    suspend fun updateSolicitud(@Path("id") id: Int, @Body solicitud: UpdateSolicitudDto): Response<Unit>

    @DELETE("api/solicitudes/{id}")
    suspend fun deleteSolicitud(@Path("id") id: Int): Response<Unit>
}
