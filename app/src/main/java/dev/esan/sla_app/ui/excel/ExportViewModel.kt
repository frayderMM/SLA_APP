package dev.esan.sla_app.ui.excel



import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.esan.sla_app.data.remote.repository.ExportExcelRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ExportExcelViewModel(
    private val repository: ExportExcelRepository
) : ViewModel() {

    private val _downloadState = MutableStateFlow<ExportState>(ExportState.Idle)
    val downloadState = _downloadState.asStateFlow()

    fun exportExcel(year: Int = java.time.LocalDate.now().year) {
        viewModelScope.launch {
            _downloadState.value = ExportState.Loading

            try {
                val response = repository.downloadExcel(year)

                if (response.isSuccessful && response.body() != null) {
                    _downloadState.value = ExportState.Success(response.body()!!)
                } else {
                    _downloadState.value = ExportState.Error("Error ${response.code()}")
                }

            } catch (e: Exception) {
                _downloadState.value = ExportState.Error("Error: ${e.message}")
            }
        }
    }

    fun reset() {
        _downloadState.value = ExportState.Idle
    }
}
