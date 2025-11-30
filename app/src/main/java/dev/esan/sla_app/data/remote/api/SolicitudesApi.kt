package dev.esan.sla_app.data.remote.api

import dev.esan.sla_app.data.remote.dto.solicitudes.CrearSolicitudRequest
import dev.esan.sla_app.data.remote.dto.solicitudes.EditarSolicitudRequest
import dev.esan.sla_app.data.remote.dto.solicitudes.SolicitudDto
import retrofit2.Response
import retrofit2.http.*

interface SolicitudesApi {

    // --- NOMBRES DE FUNCIONES CORREGIDOS Y ESTANDARIZADOS ---

    @GET("solicitudes")
    suspend fun getSolicitudes(): Response<List<SolicitudDto>>

    @GET("solicitudes/{id}")
    suspend fun getSolicitudPorId(@Path("id") id: Int): Response<SolicitudDto>

    @POST("solicitudes")
    suspend fun crearSolicitud(@Body body: CrearSolicitudRequest): Response<SolicitudDto>

    @PUT("solicitudes/{id}")
    suspend fun actualizarSolicitud(@Path("id") id: Int, @Body body: EditarSolicitudRequest): Response<SolicitudDto>

    @DELETE("solicitudes/{id}")
    suspend fun eliminarSolicitud(@Path("id") id: Int): Response<Unit>

}