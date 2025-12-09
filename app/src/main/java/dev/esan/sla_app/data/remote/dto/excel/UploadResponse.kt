package dev.esan.sla_app.data.remote.dto.excel


data class UploadResponse(
    val message: String,
    val errores: List<String>
)
