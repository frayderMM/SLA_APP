package dev.esan.sla_app.data.remote.api

import dev.esan.sla_app.data.model.TipoSla
import retrofit2.http.GET

/**
 * Interfaz de la API para obtener los tipos de SLA.
 */
interface TiposSlaApi {

    @GET("api/tiposSla")
    suspend fun getTiposSla(): List<TipoSla>

}
