package dev.esan.sla_app.ui.solicitudes

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.esan.sla_app.data.repository.ExcelUploadRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class ExcelState {
    object Idle : ExcelState()
    object Loading : ExcelState()
    data class Success(val message: String, val errores: List<String>) : ExcelState()
    data class Error(val message: String) : ExcelState()
}

class ImportExcelViewModel(
    private val repo: ExcelUploadRepository
) : ViewModel() {

    private val _state = MutableStateFlow<ExcelState>(ExcelState.Idle)
    val state: StateFlow<ExcelState> = _state

    fun upload(uri: Uri) {
        viewModelScope.launch {
            _state.value = ExcelState.Loading
            val result = repo.upload(uri)
            result.onSuccess {
                _state.value = ExcelState.Success(it.message, it.errores)
            }.onFailure {
                _state.value = ExcelState.Error(it.message ?: "Error desconocido")
            }
        }
    }

    fun reset() {
        _state.value = ExcelState.Idle
    }
}
