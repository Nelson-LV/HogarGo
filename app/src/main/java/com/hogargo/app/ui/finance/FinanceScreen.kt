package com.hogargo.app.ui.finance

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.TrendingDown
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.hogargo.app.R
import com.hogargo.app.data.local.ExpenseCategory
import com.hogargo.app.data.local.ExpenseEntity
import com.hogargo.app.data.local.SavingsGoalEntity
import com.hogargo.app.ui.theme.BrandOrange
import com.hogargo.app.ui.theme.BrandOrangeDeep
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun FinanceScreen(viewModel: FinanceViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddExpense by rememberSaveable { mutableStateOf(false) }
    var showGoalDialog by rememberSaveable { mutableStateOf(false) }
    var showContributeDialog by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
    ) {
        Box(Modifier.size(16.dp))
        Text(stringResource(R.string.finance_title), style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
        Text(
            stringResource(R.string.finance_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Box(Modifier.size(12.dp))
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .clickable { showAddExpense = true }
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(Icons.Outlined.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurface)
            Text(stringResource(R.string.finance_new_expense), style = MaterialTheme.typography.titleMedium)
        }

        Box(Modifier.size(16.dp))
        SavingsGoalCard(
            goal = uiState.savingsGoal,
            onCreateGoal = { showGoalDialog = true },
            onContribute = { showContributeDialog = true },
        )

        Box(Modifier.size(16.dp))
        MonthOverviewCard(totalSpent = uiState.totalSpent, average = uiState.averageExpense)

        Box(Modifier.size(16.dp))
        ExpenseBreakdownCard(breakdown = uiState.breakdown, totalSpent = uiState.totalSpent)

        Box(Modifier.size(16.dp))
        RecentExpensesCard(expenses = uiState.expenses, onDelete = viewModel::deleteExpense)

        Box(Modifier.size(24.dp))
    }

    if (showAddExpense) {
        AddExpenseDialog(
            onDismiss = { showAddExpense = false },
            onConfirm = { title, category, amount, date ->
                viewModel.addExpense(title, category, amount, date)
                showAddExpense = false
            },
        )
    }
    if (showGoalDialog) {
        GoalDialog(
            onDismiss = { showGoalDialog = false },
            onConfirm = { title, target ->
                viewModel.createOrRenameGoal(title, target)
                showGoalDialog = false
            },
        )
    }
    if (showContributeDialog) {
        ContributeDialog(
            onDismiss = { showContributeDialog = false },
            onConfirm = { amount ->
                viewModel.contribute(amount)
                showContributeDialog = false
            },
        )
    }
}

private fun categoryColor(category: ExpenseCategory): Color = when (category) {
    ExpenseCategory.GROCERIES -> Color(0xFF8D4F11)
    ExpenseCategory.BILLS -> Color(0xFF904917)
    ExpenseCategory.PET -> Color(0xFFFEAC67)
    ExpenseCategory.LEISURE -> Color(0xFFFFDBC9)
    ExpenseCategory.OTHER -> Color(0xFFE4E3DB)
}

private fun categoryIcon(category: ExpenseCategory): ImageVector = when (category) {
    ExpenseCategory.GROCERIES -> Icons.Outlined.ShoppingCart
    ExpenseCategory.BILLS -> Icons.Outlined.Bolt
    ExpenseCategory.PET -> Icons.Outlined.Pets
    ExpenseCategory.LEISURE -> Icons.Outlined.Restaurant
    ExpenseCategory.OTHER -> Icons.Outlined.MoreHoriz
}

@Composable
private fun SavingsGoalCard(goal: SavingsGoalEntity?, onCreateGoal: () -> Unit, onContribute: () -> Unit) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        modifier = Modifier.fillMaxWidth(),
    ) {
        if (goal == null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(Icons.Outlined.Savings, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(
                    stringResource(R.string.finance_goal_empty_title),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
                )
                Button(onClick = onCreateGoal, shape = RoundedCornerShape(50)) {
                    Text(stringResource(R.string.finance_goal_empty_action))
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(goal.title, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                Text(
                    text = stringResource(R.string.finance_savings_progress, "$${"%.2f".format(goal.currentAmount)}", "$${"%.2f".format(goal.targetAmount)}"),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Box(Modifier.size(20.dp))
                SavingsJarGraphic(progress = if (goal.targetAmount > 0) (goal.currentAmount / goal.targetAmount).toFloat().coerceIn(0f, 1f) else 0f)
                Box(Modifier.size(16.dp))
                OutlinedButton(onClick = onContribute, shape = RoundedCornerShape(50)) {
                    Text(stringResource(R.string.finance_goal_contribute_button))
                }
            }
        }
    }
}

@Composable
private fun SavingsJarGraphic(progress: Float) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .width(64.dp)
                .height(12.dp)
                .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
        )
        Box(
            modifier = Modifier
                .width(80.dp)
                .height(96.dp)
                .clip(RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp, topStart = 8.dp, topEnd = 8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.BottomCenter,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height((96 * progress).dp)
                    .background(Brush.verticalGradient(colors = listOf(BrandOrange, BrandOrangeDeep))),
            )
        }
    }
}

@Composable
private fun MonthOverviewCard(totalSpent: Double, average: Double) {
    Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)) {
        Column(Modifier.padding(24.dp)) {
            Text(stringResource(R.string.finance_this_month), style = MaterialTheme.typography.titleLarge)
            Box(Modifier.size(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                StatTile(Icons.AutoMirrored.Outlined.TrendingDown, stringResource(R.string.finance_spent), "$${"%.2f".format(totalSpent)}", Modifier.weight(1f))
                StatTile(Icons.Outlined.Savings, stringResource(R.string.finance_average), "$${"%.2f".format(average)}", Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun StatTile(icon: ImageVector, label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(label, style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(top = 8.dp))
        Text(value, style = MaterialTheme.typography.titleLarge)
    }
}

@Composable
private fun ExpenseBreakdownCard(breakdown: List<Pair<ExpenseCategory, Double>>, totalSpent: Double) {
    Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)) {
        Column(Modifier.padding(24.dp)) {
            Text(stringResource(R.string.finance_breakdown), style = MaterialTheme.typography.titleLarge)
            Box(Modifier.size(20.dp))
            if (breakdown.isEmpty()) {
                Text(
                    stringResource(R.string.finance_empty_expenses),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            breakdown.forEach { (category, amount) ->
                val fraction = if (totalSpent > 0) (amount / totalSpent).toFloat() else 0f
                Column(Modifier.padding(bottom = 20.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(stringResource(category.labelRes), style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("$${"%.2f".format(amount)}", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    LinearProgressIndicator(
                        progress = { fraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                            .height(16.dp)
                            .clip(RoundedCornerShape(50)),
                        color = categoryColor(category),
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun RecentExpensesCard(expenses: List<ExpenseEntity>, onDelete: (ExpenseEntity) -> Unit) {
    Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)) {
        Column(Modifier.padding(24.dp)) {
            Text(stringResource(R.string.finance_recent_expenses), style = MaterialTheme.typography.titleLarge)
            Box(Modifier.size(16.dp))
            if (expenses.isEmpty()) {
                Text(
                    stringResource(R.string.finance_empty_expenses),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            expenses.forEach { expense -> ExpenseRow(expense, onDelete = { onDelete(expense) }) }
        }
    }
}

@Composable
private fun ExpenseRow(expense: ExpenseEntity, onDelete: () -> Unit) {
    val formatter = remember(Locale.getDefault()) { DateTimeFormatter.ofPattern("d MMM", Locale.getDefault()) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(categoryColor(expense.category)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(categoryIcon(expense.category), contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
        }
        Column(Modifier.weight(1f)) {
            Text(expense.title, style = MaterialTheme.typography.titleMedium)
            Text(stringResource(expense.category.labelRes), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text("-$${"%.2f".format(expense.amount)}", style = MaterialTheme.typography.titleMedium)
            Text(expense.date.format(formatter), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        IconButton(onClick = onDelete) {
            Icon(Icons.Outlined.Delete, contentDescription = stringResource(R.string.finance_delete_expense_cd), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddExpenseDialog(onDismiss: () -> Unit, onConfirm: (String, ExpenseCategory, Double, LocalDate) -> Unit) {
    var title by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf(ExpenseCategory.GROCERIES) }
    var amountText by rememberSaveable { mutableStateOf("") }
    var date by rememberSaveable { mutableStateOf(LocalDate.now()) }
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var error by rememberSaveable { mutableStateOf<String?>(null) }

    val titleEmptyError = stringResource(R.string.finance_error_empty_title)
    val amountInvalidError = stringResource(R.string.finance_error_invalid_amount)
    val formatter = remember(Locale.getDefault()) {
        DateTimeFormatter.ofPattern("d MMM yyyy", Locale.getDefault())
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.finance_new_expense)) },
        text = {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { input -> amountText = input.filter { it.isDigit() || it == '.' || it == ',' }.take(12) },
                    label = { Text(stringResource(R.string.finance_expense_amount_label)) },
                    placeholder = { Text("0.00") },
                    prefix = { Text("$") },
                    textStyle = MaterialTheme.typography.headlineSmall,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                )
                Box(Modifier.size(12.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(stringResource(R.string.finance_expense_name_label)) },
                    placeholder = { Text(stringResource(R.string.finance_expense_name_hint)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Box(Modifier.size(16.dp))
                Text(
                    stringResource(R.string.finance_expense_category_label),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Box(Modifier.size(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    ExpenseCategory.entries.forEach { c ->
                        FilterChip(
                            selected = c == category,
                            onClick = { category = c },
                            label = { Text(stringResource(c.labelRes)) },
                            leadingIcon = {
                                Icon(categoryIcon(c), contentDescription = null, modifier = Modifier.size(18.dp))
                            },
                        )
                    }
                }
                Box(Modifier.size(12.dp))
                OutlinedTextField(
                    value = date.format(formatter),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.calendar_bill_due_date_label)) },
                    trailingIcon = {
                        TextButton(onClick = { showDatePicker = true }) { Text(stringResource(R.string.calendar_change_date)) }
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
                if (error != null) {
                    Text(error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 4.dp))
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val amount = amountText.replace(',', '.').toDoubleOrNull()
                error = when {
                    title.isBlank() -> titleEmptyError
                    amount == null || amount <= 0.0 -> amountInvalidError
                    else -> null
                }
                if (error == null && amount != null) {
                    onConfirm(title.trim(), category, amount, date)
                }
            }) {
                Text(stringResource(R.string.finance_add_button))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.finance_cancel_button)) }
        },
    )

    if (showDatePicker) {
        val state = rememberDatePickerState(
            initialSelectedDateMillis = date.atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli(),
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { millis ->
                        date = Instant.ofEpochMilli(millis).atZone(ZoneId.of("UTC")).toLocalDate()
                    }
                    showDatePicker = false
                }) { Text(stringResource(R.string.finance_add_button)) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text(stringResource(R.string.finance_cancel_button)) }
            },
        ) {
            DatePicker(state = state)
        }
    }
}

@Composable
private fun GoalDialog(onDismiss: () -> Unit, onConfirm: (String, Double) -> Unit) {
    var title by rememberSaveable { mutableStateOf("") }
    var targetText by rememberSaveable { mutableStateOf("") }
    var error by rememberSaveable { mutableStateOf<String?>(null) }

    val titleEmptyError = stringResource(R.string.finance_error_empty_title)
    val amountInvalidError = stringResource(R.string.finance_error_invalid_amount)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.finance_goal_create_title)) },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(stringResource(R.string.finance_goal_name_label)) },
                    placeholder = { Text(stringResource(R.string.finance_goal_name_hint)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Box(Modifier.size(12.dp))
                OutlinedTextField(
                    value = targetText,
                    onValueChange = { targetText = it },
                    label = { Text(stringResource(R.string.finance_goal_target_label)) },
                    placeholder = { Text("0.00") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                )
                if (error != null) {
                    Text(error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 4.dp))
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val target = targetText.toDoubleOrNull()
                error = when {
                    title.isBlank() -> titleEmptyError
                    target == null || target <= 0.0 -> amountInvalidError
                    else -> null
                }
                if (error == null && target != null) {
                    onConfirm(title.trim(), target)
                }
            }) {
                Text(stringResource(R.string.finance_goal_create_button))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.finance_cancel_button)) }
        },
    )
}

@Composable
private fun ContributeDialog(onDismiss: () -> Unit, onConfirm: (Double) -> Unit) {
    var amountText by rememberSaveable { mutableStateOf("") }
    var error by rememberSaveable { mutableStateOf<String?>(null) }
    val amountInvalidError = stringResource(R.string.finance_error_invalid_amount)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.finance_goal_contribute_title)) },
        text = {
            Column {
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text(stringResource(R.string.finance_goal_contribute_amount_label)) },
                    placeholder = { Text("0.00") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                )
                if (error != null) {
                    Text(error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 4.dp))
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val amount = amountText.toDoubleOrNull()
                error = if (amount == null || amount <= 0.0) amountInvalidError else null
                if (error == null && amount != null) {
                    onConfirm(amount)
                }
            }) {
                Text(stringResource(R.string.finance_goal_contribute_button))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.finance_cancel_button)) }
        },
    )
}
