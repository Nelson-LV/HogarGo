package com.hogargo.app.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hogargo.app.HogarGoApplication
import com.hogargo.app.R
import com.hogargo.app.data.AppViewModel
import com.hogargo.app.data.local.MemberEntity
import com.hogargo.app.ui.components.MemberAvatar
import com.hogargo.app.data.local.SavingsGoalEntity
import com.hogargo.app.data.HouseTask
import com.hogargo.app.data.getDisplayTitle
import com.hogargo.app.data.local.ExpenseCategory
import com.hogargo.app.ui.calendar.AddEventDialog
import com.hogargo.app.ui.calendar.CalendarViewModel
import com.hogargo.app.ui.finance.AddExpenseDialog
import com.hogargo.app.ui.finance.FinanceViewModel
import com.hogargo.app.ui.pet.PetViewModel
import com.hogargo.app.ui.pet.ZoriAvatar
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

private const val MaxHomeGoals = 3

@Composable
fun HomeScreen(
    appViewModel: AppViewModel,
    householdId: String,
    onNewTask: () -> Unit,
    onOpenFinance: () -> Unit = {},
    onOpenPet: () -> Unit = {},
    onOpenAbout: () -> Unit = {},
) {
    val application = LocalContext.current.applicationContext as HogarGoApplication
    val financeViewModel: FinanceViewModel = viewModel(
        key = "finance_$householdId",
        factory = FinanceViewModel.factory(application.financeRepository(householdId)),
    )
    val petViewModel: PetViewModel = viewModel(
        key = "pet_$householdId",
        factory = PetViewModel.factory(application.petRepository(householdId)),
    )
    val calendarViewModel: CalendarViewModel = viewModel(
        key = "calendar_$householdId",
        factory = CalendarViewModel.factory(application.calendarRepository(householdId)),
    )

    val uiState by appViewModel.uiState.collectAsState()
    val financeState by financeViewModel.uiState.collectAsState()
    val petState by petViewModel.uiState.collectAsState()
    var showNewEventDialog by rememberSaveable { mutableStateOf(false) }
    var showNewExpenseDialog by rememberSaveable { mutableStateOf(false) }
    var showMoreBottomSheet by rememberSaveable { mutableStateOf(false) }

    if (showNewEventDialog) {
        AddEventDialog(
            onDismiss = { showNewEventDialog = false },
            onConfirm = { title, date, time ->
                calendarViewModel.addEvent(title, date, time)
                showNewEventDialog = false
            },
        )
    }

    if (showNewExpenseDialog) {
        AddExpenseDialog(
            onDismiss = { showNewExpenseDialog = false },
            onConfirm = { title, category, amount, date ->
                financeViewModel.addExpense(title, category, amount, date)
                showNewExpenseDialog = false
            },
        )
    }

    if (showMoreBottomSheet) {
        MoreBottomSheet(
            members = uiState.members,
            onOpenAbout = {
                showMoreBottomSheet = false
                onOpenAbout()
            },
            onDismiss = { showMoreBottomSheet = false },
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
    ) {
        Box(Modifier.height(16.dp))
        Text(
            text = uiState.currentMember?.name
                ?.let { stringResource(R.string.home_greeting_named, it) }
                ?: stringResource(R.string.home_greeting),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            text = stringResource(R.string.home_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Box(Modifier.height(16.dp))

        StreakChip(streakDays = uiState.streakDays)

        Box(Modifier.height(16.dp))

        ZoriRestingCard(equippedIds = petState.wardrobe.filter { it.equipped }.map { it.id }.toSet(), onClick = onOpenPet)

        Box(Modifier.height(16.dp))

        ZoriAdviceCard(
            adviceText = uiState.dailyAdvice,
            isLoading = uiState.isLoadingAdvice,
            onRefresh = { appViewModel.fetchDailyAdvice() },
        )

        Box(Modifier.height(16.dp))

        NextTaskCard(
            nextTask = uiState.nextTask,
            onToggleCompleted = { taskId -> appViewModel.toggleTaskCompleted(taskId) },
        )

        Box(Modifier.height(16.dp))

        val goals = financeState.savingsGoals
        if (goals.isEmpty()) {
            SavingsGoalCard(goal = null, onClick = onOpenFinance)
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                goals.take(MaxHomeGoals).forEach { goal ->
                    SavingsGoalCard(goal = goal, onClick = onOpenFinance)
                }
                if (goals.size > MaxHomeGoals) {
                    Text(
                        text = stringResource(R.string.home_more_goals, goals.size - MaxHomeGoals),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable(onClick = onOpenFinance),
                    )
                }
            }
        }

        Box(Modifier.height(16.dp))

        QuickActionsGrid(
            onNewTask = onNewTask,
            onNewExpense = { showNewExpenseDialog = true },
            onNewEvent = { showNewEventDialog = true },
            onMore = { showMoreBottomSheet = true },
        )

        Box(Modifier.height(24.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MoreBottomSheet(
    members: List<MemberEntity>,
    onOpenAbout: () -> Unit,
    onDismiss: () -> Unit,
) {
    var showMembersDialog by rememberSaveable { mutableStateOf(false) }

    if (showMembersDialog) {
        HouseholdMembersDialog(members = members, onDismiss = { showMembersDialog = false })
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(),
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = stringResource(R.string.home_quick_more),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            Box(Modifier.height(4.dp))

            MoreOptionRow(
                icon = Icons.Outlined.Info,
                title = stringResource(R.string.more_about_title),
                subtitle = stringResource(R.string.more_about_subtitle),
                onClick = onOpenAbout,
            )

            MoreOptionRow(
                icon = Icons.Outlined.People,
                title = "Integrantes del Hogar",
                subtitle = "Ver los miembros registrados de la familia",
                onClick = {
                    showMembersDialog = true
                },
            )
        }
    }
}

@Composable
private fun MoreOptionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(22.dp),
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun HouseholdMembersDialog(members: List<MemberEntity>, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Outlined.People, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text("Integrantes del Hogar", style = MaterialTheme.typography.titleLarge)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "Miembros registrados en la familia:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    members.forEach { member ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            MemberAvatar(
                                name = member.name,
                                size = 56.dp,
                                borderColor = MaterialTheme.colorScheme.primaryContainer,
                            )
                            Text(
                                text = member.name,
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(top = 6.dp),
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(android.R.string.ok))
            }
        },
    )
}

@Composable
private fun StreakChip(streakDays: Int) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(
            imageVector = Icons.Outlined.LocalFireDepartment,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
        )
        Column {
            Text(
                text = stringResource(R.string.home_streak_title),
                style = MaterialTheme.typography.labelLarge,
            )
            Text(
                text = stringResource(R.string.home_streak_subtitle, streakDays, (streakDays / 7 + 1) * 7),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ZoriRestingCard(equippedIds: Set<String>, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        shape = RoundedCornerShape(24.dp),
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.home_zori_resting_title),
                    style = MaterialTheme.typography.titleLarge,
                )
                Text(
                    text = stringResource(R.string.home_zori_resting_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp, bottom = 12.dp),
                )
                Text(
                    text = stringResource(R.string.home_start_day),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            ZoriAvatar(equippedIds = equippedIds, size = 110.dp)
        }
    }
}

@Composable
private fun ZoriAdviceCard(
    adviceText: String,
    isLoading: Boolean,
    onRefresh: () -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Lightbulb,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = "Consejo del día de Zori",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
                IconButton(onClick = onRefresh, enabled = !isLoading) {
                    Icon(
                        imageVector = Icons.Outlined.Refresh,
                        contentDescription = "Actualizar consejo",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }
            Box(Modifier.height(8.dp))
            if (isLoading) {
                Text(
                    text = "Cargando consejo en vivo desde la web...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                Text(
                    text = "\"$adviceText\"",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

@Composable
private fun NextTaskCard(
    nextTask: HouseTask?,
    onToggleCompleted: (String) -> Unit,
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        shape = RoundedCornerShape(24.dp),
    ) {
        Column(Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Outlined.Checklist, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(
                    text = stringResource(R.string.home_next_task),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            Row(
                modifier = Modifier
                    .padding(top = 12.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                if (nextTask != null) {
                    Checkbox(
                        checked = nextTask.completed,
                        onCheckedChange = { onToggleCompleted(nextTask.id) },
                    )
                    Column {
                        Text(text = nextTask.getDisplayTitle(), style = MaterialTheme.typography.titleMedium)
                        val subtitle = if (nextTask.minutes != null && nextTask.minutes > 0) {
                            "${stringResource(nextTask.category.labelRes)} • ${stringResource(R.string.tasks_minutes, nextTask.minutes)}"
                        } else {
                            "${stringResource(nextTask.category.labelRes)}${nextTask.dueTime?.let { " • $it" } ?: ""}"
                        }
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                } else {
                    Text(
                        text = stringResource(R.string.home_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 8.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun SavingsGoalCard(goal: SavingsGoalEntity?, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        shape = RoundedCornerShape(24.dp),
    ) {
        if (goal == null) {
            Column(Modifier.fillMaxWidth().padding(20.dp)) {
                Text(
                    text = stringResource(R.string.finance_goal_empty_title),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = stringResource(R.string.finance_goal_empty_action),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
            return@Card
        }
        val progress = if (goal.targetAmount > 0) (goal.currentAmount / goal.targetAmount).toFloat().coerceIn(0f, 1f) else 0f
        Column(Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = goal.title,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .height(12.dp)
                    .clip(RoundedCornerShape(50)),
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = stringResource(R.string.home_saved_amount, "$${"%.2f".format(goal.currentAmount)}"),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = stringResource(R.string.home_goal_amount, "$${"%.2f".format(goal.targetAmount)}"),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

private enum class QuickActionType { NEW_TASK, ADD_EXPENSE, EVENT, MORE }
private data class QuickAction(val type: QuickActionType, val label: String, val icon: ImageVector)

@Composable
private fun QuickActionsGrid(
    onNewTask: () -> Unit,
    onNewExpense: () -> Unit,
    onNewEvent: () -> Unit,
    onMore: () -> Unit,
) {
    val actions = listOf(
        QuickAction(QuickActionType.NEW_TASK, stringResource(R.string.home_quick_new_task), Icons.Outlined.Checklist),
        QuickAction(QuickActionType.ADD_EXPENSE, stringResource(R.string.home_quick_add_expense), Icons.AutoMirrored.Outlined.ReceiptLong),
        QuickAction(QuickActionType.EVENT, stringResource(R.string.home_quick_event), Icons.Outlined.CalendarMonth),
        QuickAction(QuickActionType.MORE, stringResource(R.string.home_quick_more), Icons.Outlined.MoreHoriz),
    )
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        actions.chunked(2).forEach { rowActions ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                rowActions.forEach { action ->
                    QuickActionCard(
                        action = action,
                        onClick = {
                            when (action.type) {
                                QuickActionType.NEW_TASK -> onNewTask()
                                QuickActionType.ADD_EXPENSE -> onNewExpense()
                                QuickActionType.EVENT -> onNewEvent()
                                QuickActionType.MORE -> onMore()
                            }
                        },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickActionCard(action: QuickAction, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        modifier = modifier.aspectRatio(1.6f),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(action.icon, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimaryContainer)
            }
            Text(
                text = action.label,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}
