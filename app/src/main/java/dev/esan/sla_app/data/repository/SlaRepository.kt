package dev.esan.sla_app.data.repository

import dev.esan.sla_app.data.remote.api.SlaApi

class SlaRepository(private val api: SlaApi) {

    suspend fun cargarIndicadores() =
        api.getIndicadores()
}