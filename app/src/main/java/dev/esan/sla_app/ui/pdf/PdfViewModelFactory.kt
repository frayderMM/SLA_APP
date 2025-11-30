package dev.esan.sla_app.ui.pdf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.esan.sla_app.data.repository.ReportesRepository

class PdfViewModelFactory(
    private val repo: ReportesRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PdfViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PdfViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}