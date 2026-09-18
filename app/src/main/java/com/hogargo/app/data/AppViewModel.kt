package com.hogargo.app.data

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class AppUiState(
    val tasks: List<HouseTask> = InitialTasks,
    val petState: PetState = InitialPetState,
    val wardrobe: List<PetWardrobeItem> = WardrobeItems,
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

    fun toggleWardrobeEquipped(itemId: String) {
        _uiState.update { state ->
            state.copy(
                wardrobe = state.wardrobe.map { item ->
                    if (item.id == itemId && item.unlocked) item.copy(equipped = !item.equipped) else item
                },
            )
        }
    }

    fun feedPet() {
        _uiState.update { state ->
            state.copy(petState = state.petState.copy(satiety = (state.petState.satiety + 0.1f).coerceAtMost(1f)))
        }
    }

    fun playWithPet() {
        _uiState.update { state ->
            state.copy(petState = state.petState.copy(happiness = (state.petState.happiness + 0.1f).coerceAtMost(1f)))
        }
    }
}
