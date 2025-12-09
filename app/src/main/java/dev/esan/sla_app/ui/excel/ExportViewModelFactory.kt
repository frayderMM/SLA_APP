package dev.esan.sla_app.ui.excel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.esan.sla_app.data.remote.repository.ExportExcelRepository

class ExportExcelViewModelFactory(
    private val repository: ExportExcelRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ExportExcelViewModel(repository) as T
    }
}
