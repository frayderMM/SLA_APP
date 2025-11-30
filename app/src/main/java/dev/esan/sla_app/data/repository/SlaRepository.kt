package dev.esan.sla_app.data.repository

import dev.esan.sla_app.data.remote.api.SlaApi
import dev.esan.sla_app.data.remote.api.TiposSlaApi

/**
 * Repositorio para la pantalla de Indicadores.
 * Ahora también gestiona los tipos de SLA para los filtros.
 */
class SlaRepository(
    private val slaApi: SlaApi,
    private val tiposSlaApi: TiposSlaApi // 🔥 1. AÑADIR NUEVA DEPENDENCIA
) {

    suspend fun cargarIndicadores() =
        slaApi.getIndicadores()

    // 🔥 2. AÑADIR NUEVA FUNCIÓN
    suspend fun getTiposSla() =
        tiposSlaApi.getTiposSla()
}