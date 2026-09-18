package com.hogargo.app.ui.tasks

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
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.hogargo.app.R
import com.hogargo.app.data.AppViewModel
import com.hogargo.app.data.HouseTask
import com.hogargo.app.data.familyMember
import com.hogargo.app.ui.theme.BrandBrownStrong
import com.hogargo.app.ui.theme.BrandOrange

@Composable
fun TasksScreen(appViewModel: AppViewModel, onProposeNewTask: () -> Unit) {
    val uiState by appViewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
    ) {
        Box(Modifier.size(16.dp))
        Text(text = stringResource(R.string.tasks_title), style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary)
        Text(
            text = stringResource(R.string.tasks_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Box(Modifier.size(16.dp))

        StreakBanner()

        Box(Modifier.size(16.dp))

        uiState.tasks.forEach { task ->
            TaskCard(task = task, onToggle = { appViewModel.toggleTaskCompleted(task.id) })
            Box(Modifier.size(16.dp))
        }

        OutlinedButton(
            onClick = onProposeNewTask,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
        ) {
            Icon(Icons.Outlined.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Text(
                text = stringResource(R.string.tasks_propose_new),
                modifier = Modifier.padding(start = 8.dp),
                style = MaterialTheme.typography.labelLarge,
            )
        }

        Box(Modifier.size(24.dp))
    }
}

@Composable
private fun StreakBanner() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(BrandOrange)
            .padding(17.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Outlined.LocalFireDepartment, contentDescription = null, tint = BrandBrownStrong)
        }
        Column {
            Text(
                text = stringResource(R.string.tasks_streak_label),
                style = MaterialTheme.typography.labelMedium,
                color = BrandBrownStrong,
            )
            Text(
                text = stringResource(R.string.tasks_streak_value, 4),
                style = MaterialTheme.typography.titleLarge,
                color = BrandBrownStrong,
            )
        }
    }
}

@Composable
private fun TaskCard(task: HouseTask, onToggle: () -> Unit) {
    val assignee = familyMember(task.assigneeId)
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (task.completed) MaterialTheme.colorScheme.surfaceContainerHigh else MaterialTheme.colorScheme.surface,
        ),
        border = if (!task.completed) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant) else null,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(
                text = stringResource(task.category.labelRes).uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .padding(horizontal = 8.dp, vertical = 2.dp),
            )
            Box(Modifier.size(8.dp))
            Row(verticalAlignment = Alignment.Top) {
                Checkbox(checked = task.completed, onCheckedChange = { onToggle() })
                Column(modifier = Modifier.weight(1f).padding(start = 4.dp)) {
                    Text(
                        text = stringResource(task.titleRes),
                        style = MaterialTheme.typography.titleLarge,
                        textDecoration = if (task.completed) TextDecoration.LineThrough else null,
                        color = if (task.completed) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                    )
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        if (task.completed && assignee != null) {
                            Text(
                                text = stringResource(R.string.tasks_completed_by, stringResource(assignee.nameRes)),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        } else {
                            Icon(Icons.Outlined.Schedule, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = task.dueTime?.let { stringResource(R.string.tasks_due_at, it) } ?: stringResource(R.string.tasks_unassigned),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(text = "${task.coinReward}", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    }
                    if (assignee != null) {
                        Box(Modifier.size(8.dp))
                        Image(
                            painter = painterResource(assignee.avatarRes),
                            contentDescription = stringResource(assignee.nameRes),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .border(2.dp, MaterialTheme.colorScheme.surfaceContainerHighest, CircleShape)
                                .alpha(if (task.completed) 0.7f else 1f),
                        )
                    }
                }
            }
        }
    }
}
