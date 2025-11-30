package dev.esan.sla_app.data.repository

import dev.esan.sla_app.data.remote.api.DashboardApi

class InsightRepository(
    private val api: DashboardApi
) {

    suspend fun getIndicadores(tipo: String) = api.getIndicadores(tipo)

    suspend fun getHistorico(tipo: String) = api.getHistorico(tipo)

    suspend fun getRegresion(tipo: String) = api.getRegresion(tipo)
}
