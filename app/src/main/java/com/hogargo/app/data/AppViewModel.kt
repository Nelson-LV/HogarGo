package com.hogargo.app.data

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class AppUiState(
    val tasks: List<HouseTask> = InitialTasks,
)

/**
 * Holds in-memory sample data shared across screens. Survives configuration changes
 * (rotation) because it is a ViewModel; there is no real backend, so process death
 * intentionally resets it back to the sample data above.
 */
class AppViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState

    fun toggleTaskCompleted(taskId: String) {
        _uiState.update { state ->
            state.copy(
                tasks = state.tasks.map { task ->
                    if (task.id == taskId) task.copy(completed = !task.completed) else task
                },
            )
        }
    }

}
