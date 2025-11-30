package dev.esan.sla_app.data.repository

import dev.esan.sla_app.data.remote.api.AlertasApi

class AlertasRepository(private val api: AlertasApi) {

    suspend fun cargarAlertas() = api.getAlertas()
}