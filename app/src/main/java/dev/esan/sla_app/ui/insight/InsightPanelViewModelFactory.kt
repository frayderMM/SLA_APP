package dev.esan.sla_app.ui.insight

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.esan.sla_app.data.repository.InsightRepository

class InsightPanelViewModelFactory(
    private val repo: InsightRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(InsightPanelViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InsightPanelViewModel(repo) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
