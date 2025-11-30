package dev.esan.sla_app.ui.pdf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.esan.sla_app.data.repository.ReportesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import okhttp3.ResponseBody

// --- Estado para la descarga de PDF ---
sealed interface PdfDownloadState {
    object Idle : PdfDownloadState // Estado inicial o inactivo
    object Loading : PdfDownloadState // Descargando
    data class Success(val body: ResponseBody) : PdfDownloadState // Éxito, con los bytes del archivo
    data class Error(val message: String) : PdfDownloadState // Fallo
}

class PdfViewModel(private val repository: ReportesRepository) : ViewModel() {

    private val _downloadState = MutableStateFlow<PdfDownloadState>(PdfDownloadState.Idle)
    val downloadState: StateFlow<PdfDownloadState> = _downloadState

    /**
     * Inicia la descarga del reporte en PDF.
     */
    fun downloadReport(tipoSla: String?) {
        // Evitar múltiples descargas simultáneas
        if (_downloadState.value == PdfDownloadState.Loading) return

        viewModelScope.launch {
            _downloadState.value = PdfDownloadState.Loading
            try {
                val response = repository.downloadPdfReport(tipoSla)

                if (response.isSuccessful && response.body() != null) {
                    _downloadState.value = PdfDownloadState.Success(response.body()!!)
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                    _downloadState.value = PdfDownloadState.Error("Error en la descarga: $errorBody")
                }

            } catch (e: Exception) {
                _downloadState.value = PdfDownloadState.Error("Fallo en la conexión: ${e.message}")
            }
        }
    }

    /**
     * Reinicia el estado a Idle. Se debe llamar después de que la UI maneje el estado de éxito o error.
     */
    fun resetDownloadState() {
        _downloadState.value = PdfDownloadState.Idle
    }
}
