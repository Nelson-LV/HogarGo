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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

data class AppUiState(
    val tasks: List<HouseTask> = emptyList(),
    val nextTask: HouseTask? = null,
    val streakDays: Int = 0,
    val dailyAdvice: String = "Organizar tu hogar un poco cada día hace que la convivencia sea más feliz.",
    val isLoadingAdvice: Boolean = false,
    /** Everyone in the current household (including [currentMember]). */
    val members: List<MemberEntity> = emptyList(),
    /** People waiting for the admin to accept them (only shown to the admin). */
    val pendingMembers: List<MemberEntity> = emptyList(),
    /** The person using this device. */
    val currentMember: MemberEntity? = null,
    val household: HouseholdEntity? = null,
)

private data class HouseholdData(
    val tasks: List<TaskEntity>,
    val next: TaskEntity?,
    val members: List<MemberEntity>,
    val pending: List<MemberEntity>,
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
        memberDao.observePending(householdId),
        householdDao.observeById(householdId),
    ) { tasks, next, members, pending, household ->
        HouseholdData(tasks, next, members, pending, household)
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
            streakDays = StreakCalculator.streakDays(tasksList.filter { it.completed }.mapNotNull { it.completedDate }),
            dailyAdvice = advice,
            isLoadingAdvice = isLoading,
            members = data.members,
            pendingMembers = if (data.members.any { it.id == memberId && it.isAdmin }) data.pending else emptyList(),
            currentMember = data.members.firstOrNull { it.id == memberId },
            household = data.household,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AppUiState(),
    )

    // ---- Admin-only actions. The check runs against the database, not the UI, so nobody else can use them.

    private suspend fun isAdmin(): Boolean {
        val me = memberDao.findById(memberId) ?: return false
        return me.isAdmin && me.householdId == householdId && me.isApproved
    }

    fun renameHousehold(newName: String) {
        val name = newName.trim()
        if (name.isEmpty()) return
        viewModelScope.launch {
            if (isAdmin()) householdDao.updateName(householdId, name)
        }
    }

    /** Removes someone from the household. The admin can't be removed. */
    fun removeMember(targetId: String) {
        viewModelScope.launch {
            if (!isAdmin() || targetId == memberId) return@launch
            taskDao.clearAssignee(householdId, targetId)
            memberDao.deleteNonAdmin(householdId, targetId)
        }
    }

    fun approveMember(targetId: String) {
        viewModelScope.launch {
            if (isAdmin()) memberDao.approve(householdId, targetId)
        }
    }

    /** Declines a join request (only pending people can be rejected). */
    fun rejectMember(targetId: String) {
        viewModelScope.launch {
            if (!isAdmin()) return@launch
            val target = memberDao.findById(targetId) ?: return@launch
            if (target.householdId == householdId && !target.isApproved) {
                memberDao.deleteNonAdmin(householdId, targetId)
            }
        }
    }

    fun fetchDailyAdvice() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoadingAdvice.value = true
            val advice = adviceRepository.fetchRandomAdvice()
            _dailyAdvice.value = advice
            _isLoadingAdvice.value = false
        }
    }

    fun toggleTaskCompleted(taskId: String) {
        viewModelScope.launch {
            val task = taskDao.getTaskById(householdId, taskId)
            if (task != null) {
                val newCompleted = !task.completed
                val completedDate = if (newCompleted) {
                    StreakCalculator.today()
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
