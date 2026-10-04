package com.hogargo.app.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.hogargo.app.R
import com.hogargo.app.data.local.BillEntity
import com.hogargo.app.data.getDisplayTitle
import com.hogargo.app.data.local.EventEntity
import com.hogargo.app.data.local.TaskEntity
import com.hogargo.app.data.local.toHouseTask
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun CalendarScreen(viewModel: CalendarViewModel) {
    var monthOffset by rememberSaveable { mutableIntStateOf(0) }
    val today = LocalDate.now()
    val shownMonth = YearMonth.from(today).plusMonths(monthOffset.toLong())
    val bills by viewModel.bills.collectAsState()
    val events by viewModel.events.collectAsState()
    val completedTasks by viewModel.completedTasks.collectAsState()
    var showAddBill by rememberSaveable { mutableStateOf(false) }
    var showAddEvent by rememberSaveable { mutableStateOf(false) }
    var selectedEpochDay by rememberSaveable { mutableLongStateOf(today.toEpochDay()) }
    val selectedDate = LocalDate.ofEpochDay(selectedEpochDay)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
    ) {
        Box(Modifier.size(16.dp))
        Text(
            stringResource(R.string.calendar_title),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )
        Text(
            stringResource(R.string.calendar_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
        )

        Box(Modifier.size(24.dp))
        MonthCard(
            shownMonth = shownMonth,
            today = today,
            bills = bills,
            events = events,
            completedTasks = completedTasks,
            selectedDate = selectedDate,
            onSelectDate = { selectedEpochDay = it.toEpochDay() },
            onPrev = { monthOffset -= 1 },
            onNext = { monthOffset += 1 },
        )

        Box(Modifier.size(20.dp))
        DayDetailCard(
            date = selectedDate,
            events = events.filter { it.date == selectedDate },
            bills = bills.filter { it.dueDate == selectedDate },
            completedTasks = completedTasks.filter { it.completedDate == selectedDate.toString() },
            onToggleEventDone = viewModel::toggleEventDone,
            onDeleteEvent = viewModel::deleteEvent,
            onTogglePaid = viewModel::togglePaid,
            onAddEvent = { showAddEvent = true },
        )

        Box(Modifier.size(20.dp))
        UpcomingChoresCard(
            events = events.filter { it.date == null || !it.date.isBefore(today) }
                .sortedWith(compareBy<EventEntity> { it.date == null }.thenBy { it.date }.thenBy { it.timeLabel }),
            onToggleDone = viewModel::toggleEventDone,
        )

        Box(Modifier.size(20.dp))
        BillsCard(bills = bills, onAddBill = { showAddBill = true }, onTogglePaid = viewModel::togglePaid, onDelete = viewModel::deleteBill)

        Box(Modifier.size(24.dp))
    }

    if (showAddEvent) {
        AddEventDialog(
            initialDate = selectedDate,
            onDismiss = { showAddEvent = false },
            onConfirm = { title, date, time ->
                viewModel.addEvent(title, date, time)
                selectedEpochDay = date.toEpochDay()
                showAddEvent = false
            },
        )
    }

    if (showAddBill) {
        AddBillDialog(
            onDismiss = { showAddBill = false },
            onConfirm = { title, amount, dueDate ->
                viewModel.addBill(title, amount, dueDate)
                showAddBill = false
            },
        )
    }
}

@Composable
private fun MonthCard(
    shownMonth: YearMonth,
    today: LocalDate,
    bills: List<BillEntity>,
    events: List<EventEntity>,
    completedTasks: List<TaskEntity>,
    selectedDate: LocalDate,
    onSelectDate: (LocalDate) -> Unit,
    onPrev: () -> Unit,
    onNext: () -> Unit,
) {
    val monthNames = stringArrayResource(id = R.array.months_of_year)
    val dayNames = stringArrayResource(id = R.array.days_of_week_short)

    Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(24.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.calendar_month_year, monthNames[shownMonth.monthValue - 1], shownMonth.year),
                    style = MaterialTheme.typography.titleLarge,
                )
                Row {
                    IconButton(onClick = onPrev) {
                        Icon(Icons.Outlined.ChevronLeft, contentDescription = stringResource(R.string.calendar_prev_month))
                    }
                    IconButton(onClick = onNext) {
                        Icon(Icons.Outlined.ChevronRight, contentDescription = stringResource(R.string.calendar_next_month))
                    }
                }
            }
            Box(Modifier.size(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                dayNames.forEach { day ->
                    Text(
                        text = day,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                    )
                }
            }
            Box(Modifier.size(8.dp))

            val firstDayOffset = shownMonth.atDay(1).dayOfWeek.value % 7 // Sunday -> 0
            val daysInMonth = shownMonth.lengthOfMonth()
            val cells = buildList {
                repeat(firstDayOffset) { add(null) }
                for (day in 1..daysInMonth) add(day)
                while (size % 7 != 0) add(null)
            }
            val billDays = bills.filter { !it.paid && YearMonth.from(it.dueDate) == shownMonth }
                .groupBy { it.dueDate.dayOfMonth }
            val eventDays = events.mapNotNull { it.date }.filter { YearMonth.from(it) == shownMonth }.map { it.dayOfMonth }.toSet()
            val taskDays = completedTasks.mapNotNull { runCatching { LocalDate.parse(it.completedDate) }.getOrNull() }
                .filter { YearMonth.from(it) == shownMonth }.map { it.dayOfMonth }.toSet()
            val overdueColor = MaterialTheme.colorScheme.error
            val billColor = MaterialTheme.colorScheme.primary
            cells.chunked(7).forEach { week ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    week.forEach { day ->
                        val date = day?.let { shownMonth.atDay(it) }
                        val dots = buildList {
                            if (day != null) {
                                billDays[day]?.let { dayBills ->
                                    add(if (dayBills.any { !it.dueDate.isAfter(today) }) overdueColor else billColor)
                                }
                                if (day in eventDays) add(EventDotColor)
                                if (day in taskDays) add(TaskDotColor)
                            }
                        }
                        DayCell(
                            day = day,
                            isToday = date == today,
                            selected = date != null && date == selectedDate,
                            dots = dots,
                            onClick = { date?.let(onSelectDate) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
            Box(Modifier.size(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                LegendDot(EventDotColor, stringResource(R.string.calendar_legend_events))
                LegendDot(MaterialTheme.colorScheme.primary, stringResource(R.string.calendar_legend_bills))
                LegendDot(TaskDotColor, stringResource(R.string.calendar_legend_tasks))
            }
        }
    }
}

private val UpcomingDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMM", Locale.getDefault())
private val EventDotColor = Color(0xFF3B82C4)
private val TaskDotColor = Color(0xFF4C9A5F)

@Composable
private fun LegendDot(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        Box(Modifier.size(8.dp).clip(CircleShape).background(color))
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun DayCell(
    day: Int?,
    isToday: Boolean,
    selected: Boolean,
    dots: List<Color>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(16.dp)
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(shape)
            .background(if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.background)
            .then(if (isToday) Modifier.border(2.dp, MaterialTheme.colorScheme.primary, shape) else Modifier)
            .then(if (day != null) Modifier.clickable(onClick = onClick) else Modifier),
        contentAlignment = Alignment.Center,
    ) {
        if (day != null) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = day.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (isToday || selected) FontWeight.Bold else null,
                    color = if (isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp), modifier = Modifier.height(6.dp)) {
                    dots.forEach { color ->
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(color),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DayDetailCard(
    date: LocalDate,
    events: List<EventEntity>,
    bills: List<BillEntity>,
    completedTasks: List<TaskEntity>,
    onToggleEventDone: (String, Boolean) -> Unit,
    onDeleteEvent: (EventEntity) -> Unit,
    onTogglePaid: (BillEntity) -> Unit,
    onAddEvent: () -> Unit,
) {
    val formatter = remember(Locale.getDefault()) { DateTimeFormatter.ofPattern("EEEE d 'de' MMMM", Locale.getDefault()) }
    Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)) {
        Column(Modifier.padding(24.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = date.format(formatter).replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.weight(1f),
                )
                IconButton(onClick = onAddEvent) {
                    Icon(Icons.Outlined.Add, contentDescription = stringResource(R.string.calendar_add_event))
                }
            }
            if (events.isEmpty() && bills.isEmpty() && completedTasks.isEmpty()) {
                Text(
                    text = stringResource(R.string.calendar_day_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }
            if (events.isNotEmpty()) {
                DaySectionTitle(Icons.Outlined.Event, stringResource(R.string.calendar_day_events), EventDotColor)
                events.sortedBy { it.timeLabel }.forEach { event ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = event.done, onCheckedChange = { onToggleEventDone(event.id, event.done) })
                        Column(Modifier.weight(1f)) {
                            Text(
                                event.title,
                                style = MaterialTheme.typography.titleMedium,
                                textDecoration = if (event.done) TextDecoration.LineThrough else null,
                            )
                            event.timeLabel?.let {
                                Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        IconButton(onClick = { onDeleteEvent(event) }) {
                            Icon(Icons.Outlined.Delete, contentDescription = stringResource(R.string.calendar_delete_event_cd), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
            if (bills.isNotEmpty()) {
                DaySectionTitle(Icons.Outlined.Receipt, stringResource(R.string.calendar_day_bills), MaterialTheme.colorScheme.primary)
                bills.forEach { bill ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = bill.paid, onCheckedChange = { onTogglePaid(bill) })
                        Text(
                            bill.title,
                            style = MaterialTheme.typography.titleMedium,
                            textDecoration = if (bill.paid) TextDecoration.LineThrough else null,
                            modifier = Modifier.weight(1f),
                        )
                        Text("$${"%.2f".format(bill.amount)}", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
            if (completedTasks.isNotEmpty()) {
                DaySectionTitle(Icons.Outlined.CheckCircle, stringResource(R.string.calendar_day_tasks), TaskDotColor)
                completedTasks.forEach { task ->
                    Text(
                        text = "• " + task.toHouseTask().getDisplayTitle(),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(vertical = 4.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun DaySectionTitle(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(top = 16.dp, bottom = 4.dp),
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
        Text(text, style = MaterialTheme.typography.labelLarge, color = color)
    }
}

@Composable
private fun UpcomingChoresCard(events: List<EventEntity>, onToggleDone: (String, Boolean) -> Unit) {
    Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(24.dp)) {
            Text(
                stringResource(R.string.calendar_upcoming_tasks),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            Box(Modifier.size(12.dp))
            if (events.isEmpty()) {
                Text(
                    text = stringResource(R.string.calendar_events_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp),
                )
            } else {
                events.forEach { event ->
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 6.dp)) {
                        Checkbox(
                            checked = event.done,
                            onCheckedChange = { onToggleDone(event.id, event.done) },
                        )
                        Column(Modifier.padding(start = 8.dp)) {
                            Text(
                                text = event.title,
                                style = MaterialTheme.typography.titleMedium,
                                textDecoration = if (event.done) TextDecoration.LineThrough else null,
                            )
                            Text(
                                text = listOfNotNull(event.date?.format(UpcomingDateFormatter) ?: event.dateLabel, event.timeLabel).joinToString(" • "),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BillsCard(bills: List<BillEntity>, onAddBill: () -> Unit, onTogglePaid: (BillEntity) -> Unit, onDelete: (BillEntity) -> Unit) {
    Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)) {
        Column(Modifier.padding(24.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Outlined.Receipt, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                    Text(
                        stringResource(R.string.calendar_bills_due),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
                IconButton(onClick = onAddBill) {
                    Icon(Icons.Outlined.Add, contentDescription = stringResource(R.string.calendar_add_bill))
                }
            }
            Box(Modifier.size(16.dp))
            if (bills.isEmpty()) {
                Text(
                    stringResource(R.string.calendar_bills_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            bills.forEach { bill -> BillRow(bill, onTogglePaid = { onTogglePaid(bill) }, onDelete = { onDelete(bill) }) }
        }
    }
}

@Composable
private fun BillRow(bill: BillEntity, onTogglePaid: () -> Unit, onDelete: () -> Unit) {
    val formatter = remember(Locale.getDefault()) { DateTimeFormatter.ofPattern("d MMM", Locale.getDefault()) }
    val overdue = !bill.paid && !bill.dueDate.isAfter(LocalDate.now())
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Checkbox(checked = bill.paid, onCheckedChange = { onTogglePaid() })
        Column(Modifier.weight(1f)) {
            Text(
                bill.title,
                style = MaterialTheme.typography.titleMedium,
                textDecoration = if (bill.paid) TextDecoration.LineThrough else null,
            )
            if (overdue) {
                Text(stringResource(R.string.calendar_due_today), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.error)
            } else {
                Text(bill.dueDate.format(formatter), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Text("$${"%.2f".format(bill.amount)}", style = MaterialTheme.typography.titleLarge)
        IconButton(onClick = onDelete) {
            Icon(Icons.Outlined.Delete, contentDescription = stringResource(R.string.calendar_delete_bill_cd), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddBillDialog(onDismiss: () -> Unit, onConfirm: (String, Double, LocalDate) -> Unit) {
    var title by rememberSaveable { mutableStateOf("") }
    var amountText by rememberSaveable { mutableStateOf("") }
    var dueDate by rememberSaveable { mutableStateOf(LocalDate.now()) }
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var error by rememberSaveable { mutableStateOf<String?>(null) }

    val titleEmptyError = stringResource(R.string.finance_error_empty_title)
    val amountInvalidError = stringResource(R.string.finance_error_invalid_amount)
    val formatter = remember(Locale.getDefault()) { DateTimeFormatter.ofPattern("d MMM yyyy", Locale.getDefault()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.calendar_add_bill)) },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(stringResource(R.string.calendar_bill_name_label)) },
                    placeholder = { Text(stringResource(R.string.calendar_bill_name_hint)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Box(Modifier.size(12.dp))
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text(stringResource(R.string.finance_expense_amount_label)) },
                    placeholder = { Text("0.00") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                )
                Box(Modifier.size(12.dp))
                OutlinedTextField(
                    value = dueDate.format(formatter),
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
                val amount = amountText.toDoubleOrNull()
                error = when {
                    title.isBlank() -> titleEmptyError
                    amount == null || amount <= 0.0 -> amountInvalidError
                    else -> null
                }
                if (error == null && amount != null) {
                    onConfirm(title.trim(), amount, dueDate)
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
        val state = rememberDatePickerState(initialSelectedDateMillis = dueDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli())
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { millis ->
                        dueDate = Instant.ofEpochMilli(millis).atZone(ZoneId.of("UTC")).toLocalDate()
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
private fun stringArrayResource(id: Int): Array<String> {
    val context = LocalContext.current
    return context.resources.getStringArray(id)
}
