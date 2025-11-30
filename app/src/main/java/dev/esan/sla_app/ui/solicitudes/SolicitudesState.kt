package dev.esan.sla_app.ui.solicitudes

import dev.esan.sla_app.data.remote.dto.solicitudes.SolicitudDto

data class SolicitudesState(
    val loading: Boolean = true,
    val data: List<SolicitudDto> = emptyList(),
    val error: String? = null
)