package dev.esan.sla_app.ui.pdf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.esan.sla_app.data.repository.ReportesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import java.io.File

class PdfViewModel(
    private val repo: ReportesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PdfState())
    val state: StateFlow<PdfState> = _state.asStateFlow()

    fun descargarPdf(onReady: (File) -> Unit) {
        viewModelScope.launch {
            try {
                _state.value = PdfState(loading = true)

                // La llamada de red se ejecuta en un hilo de fondo gracias a Retrofit
                val response = repo.descargarPdf()

                // La escritura en disco debe moverse explícitamente a un hilo de I/O
                val file = withContext(Dispatchers.IO) {
                    guardarEnDisco(response)
                }

                _state.value = PdfState(success = true)
                onReady(file)

            } catch (e: Exception) {
                _state.value = PdfState(error = e.message)
            }
        }
    }

    private fun guardarEnDisco(body: ResponseBody): File {
        // Crea un archivo temporal en el directorio de caché de la app
        val file = File.createTempFile("reporte_sla_", ".pdf")
        // .use asegura que los streams se cierren automáticamente
        file.outputStream().use { output ->
            body.byteStream().copyTo(output)
        }
        return file
    }
}