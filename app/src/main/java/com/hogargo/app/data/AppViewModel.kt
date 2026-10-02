package com.hogargo.app.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hogargo.app.data.local.HogarGoDatabase
import com.hogargo.app.data.local.HouseholdEntity
import com.hogargo.app.data.local.MemberEntity
import com.hogargo.app.data.local.TaskEntity
import com.hogargo.app.data.local.toHouseTask
import com.hogargo.app.data.local.toTaskEntity
import com.hogargo.app.data.network.AdviceRepository
import com.hogargo.app.data.notification.StreakNotificationHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
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
    val dailyAdvice: String = "Organizar tu hogar un poco cada día hace que la convivencia sea más feliz.",
    val isLoadingAdvice: Boolean = false,
    /** Everyone in the current household (including [currentMember]). */
    val members: List<MemberEntity> = emptyList(),
    /** The person using this device. */
    val currentMember: MemberEntity? = null,
    val household: HouseholdEntity? = null,
)

private data class HouseholdData(
    val tasks: List<TaskEntity>,
    val next: TaskEntity?,
    val members: List<MemberEntity>,
    val household: HouseholdEntity?,
)

/** Scoped to one household: it only ever reads and writes that household's rows. */
class AppViewModel(
    application: Application,
    private val householdId: String,
    private val memberId: String,
) : AndroidViewModel(application) {

    private val db = HogarGoDatabase.getInstance(application)
    private val taskDao = db.taskDao()
    private val memberDao = db.memberDao()
    private val householdDao = db.householdDao()
    private val adviceRepository = AdviceRepository()

    private val _dailyAdvice = MutableStateFlow("Organizar tu hogar un poco cada día hace que la convivencia sea más feliz.")
    private val _isLoadingAdvice = MutableStateFlow(false)

    init {
        fetchDailyAdvice()
    }

    private val householdData = combine(
        taskDao.getAllTasks(householdId),
        taskDao.getNextPendingTask(householdId),
        memberDao.observeByHousehold(householdId),
        householdDao.observeById(householdId),
    ) { tasks, next, members, household ->
        HouseholdData(tasks, next, members, household)
    }

    val uiState: StateFlow<AppUiState> = combine(
        householdData,
        _dailyAdvice,
        _isLoadingAdvice,
    ) { data, advice, isLoading ->
        val tasksList = data.tasks.map { it.toHouseTask() }
        AppUiState(
            tasks = tasksList,
            nextTask = data.next?.toHouseTask(),
            streakDays = calculateStreakDays(tasksList),
            dailyAdvice = advice,
            isLoadingAdvice = isLoading,
            members = data.members,
            currentMember = data.members.firstOrNull { it.id == memberId },
            household = data.household,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AppUiState(),
    )

    fun fetchDailyAdvice() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoadingAdvice.value = true
            val advice = adviceRepository.fetchRandomAdvice()
            _dailyAdvice.value = advice
            _isLoadingAdvice.value = false
        }
    }

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
        val resolverStr = dateFormat.format(cal.time)

        val lastCompletedDate = completedDates.last()

        if (lastCompletedDate != todayStr && lastCompletedDate != resolverStr) {
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
            val task = taskDao.getTaskById(householdId, taskId)
            if (task != null) {
                val newCompleted = !task.completed
                val completedDate = if (newCompleted) {
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                } else null
                taskDao.updateTaskCompleted(householdId, taskId, newCompleted, completedDate)
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
            taskDao.insertTask(newTask.toTaskEntity(householdId))
        }
    }
}
