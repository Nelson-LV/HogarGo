package com.hogargo.app.ui.newtask

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hogargo.app.R
import com.hogargo.app.data.AppViewModel
import com.hogargo.app.data.FamilyMembers
import com.hogargo.app.data.TaskCategory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewTaskScreen(appViewModel: AppViewModel, onBack: () -> Unit) {
    val context = LocalContext.current
    val calendar = remember { Calendar.getInstance() }

    var title by rememberSaveable { mutableStateOf("") }
    var selectedCategory by rememberSaveable { mutableStateOf(TaskCategory.KITCHEN) }
    var reward by rememberSaveable { mutableFloatStateOf(30f) }
    var selectedAssignee by rememberSaveable { mutableStateOf<String?>(FamilyMembers.first().id) }
    var customAssigneeName by rememberSaveable { mutableStateOf("") }

    var dueFormattedDate by rememberSaveable {
        mutableStateOf(context.getString(R.string.new_task_today))
    }
    var dueTimeText by rememberSaveable { mutableStateOf("18:00") }

    var showDatePickerDialog by rememberSaveable { mutableStateOf(false) }
    var showTimePickerDialog by rememberSaveable { mutableStateOf(false) }
    var showCustomAssigneeDialog by rememberSaveable { mutableStateOf(false) }

    val isOtherSelected = selectedAssignee == "other" ||
        (selectedAssignee != null && FamilyMembers.none { it.id == selectedAssignee })

    if (showDatePickerDialog) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePickerDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val selectedCal = Calendar.getInstance().apply {
                                timeInMillis = millis + TimeZone.getDefault().getOffset(millis)
                            }
                            val todayCal = Calendar.getInstance()
                            val isToday = (selectedCal[Calendar.YEAR] == todayCal[Calendar.YEAR]) &&
                                (selectedCal[Calendar.DAY_OF_YEAR] == todayCal[Calendar.DAY_OF_YEAR])
                            dueFormattedDate = if (isToday) {
                                context.getString(R.string.new_task_today)
                            } else {
                                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(selectedCal.time)
                            }
                        }
                        showDatePickerDialog = false
                    },
                ) {
                    Text(stringResource(android.R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePickerDialog = false }) {
                    Text(stringResource(android.R.string.cancel))
                }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePickerDialog) {
        val timePickerState = rememberTimePickerState(
            initialHour = calendar[Calendar.HOUR_OF_DAY],
            initialMinute = calendar[Calendar.MINUTE],
            is24Hour = true,
        )
        AlertDialog(
            onDismissRequest = { showTimePickerDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        dueTimeText = String.format(Locale.getDefault(), "%02d:%02d", timePickerState.hour, timePickerState.minute)
                        showTimePickerDialog = false
                    },
                ) {
                    Text(stringResource(android.R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePickerDialog = false }) {
                    Text(stringResource(android.R.string.cancel))
                }
            },
            text = {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    TimePicker(
                        state = timePickerState,
                        colors = TimePickerDefaults.colors(
                            clockDialColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                            selectorColor = MaterialTheme.colorScheme.primary,
                            periodSelectorSelectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        ),
                    )
                }
            },
        )
    }

    if (showCustomAssigneeDialog) {
        var inputName by rememberSaveable { mutableStateOf(customAssigneeName) }
        AlertDialog(
            onDismissRequest = { showCustomAssigneeDialog = false },
            title = { Text(stringResource(R.string.new_task_assignee_other)) },
            text = {
                OutlinedTextField(
                    value = inputName,
                    onValueChange = { inputName = it },
                    placeholder = { Text("Nombre del encargado (ej. Tía, Primo)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val trimmed = inputName.trim()
                        if (trimmed.isNotBlank()) {
                            customAssigneeName = trimmed
                            selectedAssignee = trimmed
                        } else {
                            selectedAssignee = "Otro"
                        }
                        showCustomAssigneeDialog = false
                    },
                ) {
                    Text(stringResource(android.R.string.ok))
                }
            },
            dismissButton = {
                TextButton(onClick = { showCustomAssigneeDialog = false }) {
                    Text(stringResource(android.R.string.cancel))
                }
            },
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
    ) {
        Box(Modifier.size(16.dp))

        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        ) {
            Column(Modifier.padding(20.dp)) {
                Text(stringResource(R.string.new_task_question), style = MaterialTheme.typography.labelLarge)
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text(stringResource(R.string.new_task_hint)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                )
            }
        }

        Box(Modifier.size(16.dp))
        Text(stringResource(R.string.new_task_category_label), style = MaterialTheme.typography.labelLarge)
        Box(Modifier.size(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TaskCategory.entries.forEach { category ->
                CategoryChip(
                    label = stringResource(category.labelRes),
                    selected = category == selectedCategory,
                    onClick = { selectedCategory = category },
                    modifier = Modifier.weight(1f),
                )
            }
        }

        Box(Modifier.size(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            LabeledClickableField(
                label = stringResource(R.string.new_task_date_label),
                value = dueFormattedDate,
                icon = Icons.Outlined.CalendarToday,
                onClick = { showDatePickerDialog = true },
                modifier = Modifier.weight(1f),
            )
            LabeledClickableField(
                label = stringResource(R.string.new_task_time_label),
                value = dueTimeText,
                icon = Icons.Outlined.Schedule,
                onClick = { showTimePickerDialog = true },
                modifier = Modifier.weight(1f),
            )
        }

        Box(Modifier.size(16.dp))
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        ) {
            Column(Modifier.padding(20.dp)) {
                Text(stringResource(R.string.new_task_reward_label), style = MaterialTheme.typography.labelLarge)
                Slider(
                    value = reward,
                    onValueChange = { reward = it },
                    valueRange = 0f..100f,
                    steps = 9,
                )
                Text(
                    text = stringResource(R.string.new_task_reward_value, reward.toInt()),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    textAlign = TextAlign.Center,
                )
            }
        }

        Box(Modifier.size(16.dp))
        Text(stringResource(R.string.new_task_assignee_label), style = MaterialTheme.typography.labelLarge)
        Box(Modifier.size(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            FamilyMembers.forEach { member ->
                AssigneeAvatar(
                    label = stringResource(member.nameRes),
                    avatarRes = member.avatarRes,
                    selected = member.id == selectedAssignee,
                    onClick = { selectedAssignee = member.id },
                )
            }
            AddAssigneeButton(
                label = if (customAssigneeName.isNotBlank() && isOtherSelected) customAssigneeName else stringResource(R.string.new_task_assignee_other),
                selected = isOtherSelected,
                onClick = {
                    showCustomAssigneeDialog = true
                },
            )
        }

        Box(Modifier.size(32.dp))

        Button(
            onClick = {
                if (title.isNotBlank()) {
                    appViewModel.addNewTask(
                        titleText = title.trim(),
                        category = selectedCategory,
                        coinReward = reward.toInt(),
                        assigneeId = selectedAssignee,
                        dueTime = "$dueFormattedDate • $dueTimeText",
                    )
                }
                onBack()
            },
            enabled = title.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Icon(Icons.Outlined.AutoAwesome, contentDescription = null, modifier = Modifier.size(20.dp))
            Text(
                text = stringResource(R.string.new_task_create),
                modifier = Modifier.padding(start = 8.dp),
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}

@Composable
private fun CategoryChip(label: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainer)
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun LabeledClickableField(
    label: String,
    value: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(label, style = MaterialTheme.typography.labelLarge)
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Composable
private fun AssigneeAvatar(label: String, avatarRes: Int, selected: Boolean, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable(onClick = onClick)) {
        Image(
            painter = painterResource(avatarRes),
            contentDescription = label,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .border(3.dp, if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHighest, CircleShape),
        )
        Text(label, style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(top = 8.dp))
    }
}

@Composable
private fun AddAssigneeButton(label: String, selected: Boolean, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable(onClick = onClick)) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .border(
                    3.dp,
                    if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                    CircleShape,
                )
                .background(if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Outlined.Add,
                contentDescription = null,
                tint = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.outline,
            )
        }
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}
