package dev.esan.sla_app.ui.upload

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.esan.sla_app.data.repository.ExcelUploadRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class UploadExcelViewModel(
    private val repo: ExcelUploadRepository
) : ViewModel() {

    private val _state = MutableStateFlow(UploadExcelState())
    val state: StateFlow<UploadExcelState> = _state.asStateFlow()

    fun uploadExcel(filePart: MultipartBody.Part) {
        viewModelScope.launch {
            try {
                _state.value = UploadExcelState(loading = true)
                repo.uploadExcel(filePart)
                _state.value = UploadExcelState(success = true)
            } catch (e: Exception) {
                _state.value = UploadExcelState(error = e.message)
            }
        }
    }
}