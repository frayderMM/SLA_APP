package dev.esan.sla_app.ui.pdf

data class PdfState(
    val loading: Boolean = false,
    val success: Boolean = false,
    val error: String? = null
)
