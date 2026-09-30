package com.hogargo.app.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hogargo.app.data.local.AppDatabase
import com.hogargo.app.data.local.toHouseTask
import com.hogargo.app.data.local.toTaskEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class AppUiState(
    val tasks: List<HouseTask> = emptyList(),
    val nextTask: HouseTask? = null,
    val petState: PetState = InitialPetState,
    val wardrobe: List<PetWardrobeItem> = WardrobeItems,
)

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val taskDao = db.taskDao()

    private val _petState = MutableStateFlow(InitialPetState)
    private val _wardrobe = MutableStateFlow(WardrobeItems)

    val uiState: StateFlow<AppUiState> = combine(
        taskDao.getAllTasks(),
        taskDao.getNextPendingTask(),
        _petState,
        _wardrobe,
    ) { taskEntities, nextEntity, petState, wardrobe ->
        AppUiState(
            tasks = taskEntities.map { it.toHouseTask() },
            nextTask = nextEntity?.toHouseTask(),
            petState = petState,
            wardrobe = wardrobe,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AppUiState(),
    )

    fun toggleTaskCompleted(taskId: String) {
        viewModelScope.launch {
            val task = taskDao.getTaskById(taskId)
            if (task != null) {
                taskDao.updateTaskCompleted(taskId, !task.completed)
            }
        }
    }

    fun addNewTask(
        titleText: String,
        category: TaskCategory,
        coinReward: Int,
        assigneeId: String?,
    ) {
        viewModelScope.launch {
            val newTask = HouseTask(
                id = UUID.randomUUID().toString(),
                titleText = titleText,
                category = category,
                dueTime = "6 PM",
                completed = false,
                assigneeId = assigneeId,
                coinReward = coinReward,
            )
            taskDao.insertTask(newTask.toTaskEntity())
        }
    }

    fun toggleWardrobeEquipped(itemId: String) {
        _wardrobe.update { items ->
            items.map { item ->
                if (item.id == itemId && item.unlocked) item.copy(equipped = !item.equipped) else item
            }
        }
    }

    fun feedPet() {
        _petState.update { state ->
            state.copy(satiety = (state.satiety + 0.1f).coerceAtMost(1f))
        }
    }

    fun playWithPet() {
        _petState.update { state ->
            state.copy(happiness = (state.happiness + 0.1f).coerceAtMost(1f))
        }
    }
}
