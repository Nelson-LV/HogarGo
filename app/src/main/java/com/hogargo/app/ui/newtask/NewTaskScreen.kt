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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hogargo.app.R
import com.hogargo.app.data.FamilyMembers
import com.hogargo.app.data.TaskCategory

@Composable
fun NewTaskScreen(onBack: () -> Unit) {
    var title by rememberSaveable { mutableStateOf("") }
    var selectedCategory by rememberSaveable { mutableStateOf(TaskCategory.KITCHEN) }
    var reward by rememberSaveable { mutableFloatStateOf(30f) }
    var selectedAssignee by rememberSaveable { mutableStateOf(FamilyMembers.first().id) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
    ) {
        Box(Modifier.size(16.dp))

        Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)) {
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
            LabeledStaticField(stringResource(R.string.new_task_date_label), stringResource(R.string.new_task_today), Modifier.weight(1f))
            LabeledStaticField(stringResource(R.string.new_task_time_label), "18:00", Modifier.weight(1f))
        }

        Box(Modifier.size(16.dp))
        Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)) {
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
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
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
            AddAssigneeButton(label = stringResource(R.string.new_task_assignee_other))
        }

        Box(Modifier.size(32.dp))

        Button(
            onClick = onBack,
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
private fun LabeledStaticField(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(label, style = MaterialTheme.typography.labelLarge)
            Text(value, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 8.dp))
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
private fun AddAssigneeButton(label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .border(2.dp, MaterialTheme.colorScheme.outline, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Outlined.Add, contentDescription = null, tint = MaterialTheme.colorScheme.outline)
        }
        Text(label, style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(top = 8.dp))
    }
}
