package com.hogargo.app.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hogargo.app.R
import com.hogargo.app.data.Bill
import com.hogargo.app.data.CalendarDots
import com.hogargo.app.data.CalendarMonthIndex
import com.hogargo.app.data.CalendarSelectedDay
import com.hogargo.app.data.CalendarYear
import com.hogargo.app.data.UpcomingBills
import com.hogargo.app.data.UpcomingChores
import com.hogargo.app.data.WhenLabel
import java.time.DayOfWeek
import java.time.YearMonth

@Composable
fun CalendarScreen() {
    var monthOffset by rememberSaveable { mutableIntStateOf(0) }
    val baseMonth = YearMonth.of(CalendarYear, CalendarMonthIndex + 1)
    val shownMonth = baseMonth.plusMonths(monthOffset.toLong())

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
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
        Text(
            stringResource(R.string.calendar_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth(),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )

        Box(Modifier.size(24.dp))
        MonthCard(
            shownMonth = shownMonth,
            showDots = monthOffset == 0,
            onPrev = { monthOffset -= 1 },
            onNext = { monthOffset += 1 },
        )

        Box(Modifier.size(20.dp))
        UpcomingChoresCard()

        Box(Modifier.size(20.dp))
        BillsDueCard()

        Box(Modifier.size(24.dp))
    }
}

@Composable
private fun MonthCard(shownMonth: YearMonth, showDots: Boolean, onPrev: () -> Unit, onNext: () -> Unit) {
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
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    )
                }
            }
            Box(Modifier.size(8.dp))

            val firstDayOffset = shownMonth.atDay(1).dayOfWeek.let { (it.value % 7) } // Sunday -> 0
            val daysInMonth = shownMonth.lengthOfMonth()
            val cells = buildList {
                repeat(firstDayOffset) { add(null) }
                for (day in 1..daysInMonth) add(day)
                while (size % 7 != 0) add(null)
            }
            cells.chunked(7).forEach { week ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    week.forEach { day ->
                        DayCell(day = day, selected = showDots && day == CalendarSelectedDay, dots = if (showDots) CalendarDots.find { it.day == day }?.dotColors.orEmpty() else emptyList(), modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun DayCell(day: Int?, selected: Boolean, dots: List<androidx.compose.ui.graphics.Color>, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.background)
            .then(
                if (selected) Modifier.border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp)) else Modifier,
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (day != null) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = day.toString(),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (selected) FontWeight.Bold else null,
                    color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
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
private fun UpcomingChoresCard() {
    Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(24.dp)) {
            Text(
                stringResource(R.string.calendar_upcoming_tasks),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            Box(Modifier.size(12.dp))
            UpcomingChores.forEach { chore ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 6.dp)) {
                    Checkbox(checked = chore.done, onCheckedChange = null)
                    Column(Modifier.padding(start = 8.dp)) {
                        Text(stringResource(chore.titleRes), style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = when (chore.whenLabelKey) {
                                WhenLabel.TODAY_4PM -> stringResource(R.string.calendar_today, "4 PM")
                                WhenLabel.TOMORROW -> stringResource(R.string.calendar_tomorrow)
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BillsDueCard() {
    Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)) {
        Column(Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Outlined.Receipt, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                Text(
                    stringResource(R.string.calendar_bills_due),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.error,
                )
            }
            Box(Modifier.size(16.dp))
            UpcomingBills.forEach { bill -> BillRow(bill) }
        }
    }
}

@Composable
private fun BillRow(bill: Bill) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(bill.iconBackground),
            contentAlignment = Alignment.Center,
        ) {
            Icon(bill.icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
        }
        Column(Modifier.weight(1f)) {
            Text(stringResource(bill.nameRes), style = MaterialTheme.typography.titleMedium)
            if (bill.dueToday) {
                Text(stringResource(R.string.calendar_due_today), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.error)
            } else {
                Text(bill.dateLabel.orEmpty(), style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Text("$${bill.amount}", style = MaterialTheme.typography.titleLarge)
    }
}

@Composable
private fun stringArrayResource(id: Int): Array<String> {
    val context = androidx.compose.ui.platform.LocalContext.current
    return context.resources.getStringArray(id)
}
