package dev.esan.sla_app.ui.excel

import okhttp3.ResponseBody

sealed class ExportState {
    object Idle : ExportState()
    object Loading : ExportState()
    data class Success(val body: ResponseBody) : ExportState()
    data class Error(val message: String) : ExportState()
}
