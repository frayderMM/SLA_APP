package dev.esan.sla_app.data.repository

import dev.esan.sla_app.data.model.CreateSolicitudDto
import dev.esan.sla_app.data.model.Solicitud
import dev.esan.sla_app.data.model.UpdateSolicitudDto
import dev.esan.sla_app.data.remote.api.SolicitudesApi
import dev.esan.sla_app.data.remote.api.TiposSlaApi
import java.io.IOException

class SolicitudesRepository(
    private val solicitudesApi: SolicitudesApi,
    private val tiposSlaApi: TiposSlaApi
) {

    // --- Endpoints de Solicitudes ---

    suspend fun getSolicitudes(): List<Solicitud> = solicitudesApi.getSolicitudes()

    suspend fun getSolicitudById(id: Int): Solicitud = solicitudesApi.getSolicitudById(id)

    suspend fun createSolicitud(solicitud: CreateSolicitudDto) {
        val response = solicitudesApi.createSolicitud(solicitud)
        if (!response.isSuccessful) {
            throw IOException("Error al crear la solicitud: ${response.code()} ${response.message()}")
        }
    }

    suspend fun updateSolicitud(id: Int, solicitud: UpdateSolicitudDto) {
        val response = solicitudesApi.updateSolicitud(id, solicitud)
        if (!response.isSuccessful) {
            throw IOException("Error al actualizar la solicitud: ${response.code()} ${response.message()}")
        }
    }

    suspend fun deleteSolicitud(id: Int) {
        val response = solicitudesApi.deleteSolicitud(id)
        if (!response.isSuccessful) {
            throw IOException("Error al eliminar la solicitud: ${response.code()} ${response.message()}")
        }
    }

    // --- Endpoints de TiposSla ---

    suspend fun getTiposSla() = tiposSlaApi.getTiposSla()
}
