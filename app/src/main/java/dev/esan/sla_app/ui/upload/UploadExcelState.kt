package dev.esan.sla_app.ui.upload

data class UploadExcelState(
    val loading: Boolean = false,
    val success: Boolean = false,
    val error: String? = null
)
