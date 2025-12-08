package dev.esan.sla_app.ui.assistant

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.esan.sla_app.data.repository.AssistantRepository

class AssistantViewModelFactory(
    private val repository: AssistantRepository,
    private val userId: Int,
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AssistantViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AssistantViewModel(repository, userId, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
