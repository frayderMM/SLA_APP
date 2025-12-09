package dev.esan.sla_app.ui.solicitudes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.esan.sla_app.data.repository.ExcelUploadRepository

class ImportExcelViewModelFactory(
    private val repo: ExcelUploadRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ImportExcelViewModel(repo) as T
    }
}

