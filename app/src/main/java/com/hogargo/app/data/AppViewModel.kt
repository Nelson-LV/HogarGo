package com.hogargo.app.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hogargo.app.data.local.AppDatabase
import com.hogargo.app.data.local.toHouseTask
import com.hogargo.app.data.local.toTaskEntity
import com.hogargo.app.data.notification.StreakNotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

data class AppUiState(
    val tasks: List<HouseTask> = emptyList(),
    val nextTask: HouseTask? = null,
    val streakDays: Int = 4,
    val petState: PetState = InitialPetState,
    val wardrobe: List<PetWardrobeItem> = WardrobeItems,
)

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val taskDao = db.taskDao()

    private val _petState = MutableStateFlow(InitialPetState)
    private val _wardrobe = MutableStateFlow(WardrobeItems)

    init {
        viewModelScope.launch {
            if (taskDao.getTaskCount() == 0) {
                taskDao.insertTasks(InitialTasks.map { it.toTaskEntity() })
            }
        }
    }

    val uiState: StateFlow<AppUiState> = combine(
        taskDao.getAllTasks(),
        taskDao.getNextPendingTask(),
        _petState,
        _wardrobe,
    ) { taskEntities, nextEntity, petState, wardrobe ->
        val tasksList = taskEntities.map { it.toHouseTask() }
        val streak = calculateStreakDays(tasksList)
        AppUiState(
            tasks = tasksList,
            nextTask = nextEntity?.toHouseTask(),
            streakDays = streak,
            petState = petState,
            wardrobe = wardrobe,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AppUiState(),
    )

    private fun calculateStreakDays(tasks: List<HouseTask>): Int {
        val completedDates = tasks
            .filter { it.completed && !it.completedDate.isNullOrBlank() }
            .mapNotNull { it.completedDate }
            .sorted()

        if (completedDates.isEmpty()) return 0

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val cal = Calendar.getInstance()
        val todayStr = dateFormat.format(cal.time)

        cal.add(Calendar.DAY_OF_YEAR, -1)
        val yesterdayStr = dateFormat.format(cal.time)

        val lastCompletedDate = completedDates.last()

        if (lastCompletedDate != todayStr && lastCompletedDate != yesterdayStr) {
            return 0
        }

        val uniqueCompletedDays = completedDates.toSet()
        return uniqueCompletedDays.size
    }

    fun triggerStreakWarningNotification() {
        val currentState = uiState.value
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todayStr = dateFormat.format(Date())
        val hasCompletedToday = currentState.tasks.any { it.completed && it.completedDate == todayStr }

        if (!hasCompletedToday && currentState.streakDays > 0) {
            StreakNotificationHelper.showStreakWarningNotification(getApplication(), currentState.streakDays)
        }
    }

    fun toggleTaskCompleted(taskId: String) {
        viewModelScope.launch {
            val task = taskDao.getTaskById(taskId)
            if (task != null) {
                val newCompleted = !task.completed
                val completedDate = if (newCompleted) {
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                } else null
                taskDao.updateTaskCompleted(taskId, newCompleted, completedDate)
            }
        }
    }

    fun addNewTask(
        titleText: String,
        category: TaskCategory,
        coinReward: Int,
        assigneeId: String?,
        dueTime: String? = null,
    ) {
        viewModelScope.launch {
            val newTask = HouseTask(
                id = UUID.randomUUID().toString(),
                titleText = titleText,
                category = category,
                dueTime = dueTime ?: "18:00",
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
