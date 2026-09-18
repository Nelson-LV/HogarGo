package com.hogargo.app.ui.finance

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.automirrored.outlined.TrendingDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.hogargo.app.R
import com.hogargo.app.data.Expense
import com.hogargo.app.data.ExpenseBreakdown
import com.hogargo.app.data.FinanceSavingsGoal
import com.hogargo.app.data.MonthlyRemaining
import com.hogargo.app.data.MonthlySpent
import com.hogargo.app.data.RecentExpenses
import com.hogargo.app.ui.theme.BrandOrange
import com.hogargo.app.ui.theme.BrandOrangeDeep

@Composable
fun FinanceScreen() {
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
                .clickable { }
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(Icons.Outlined.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurface)
            Text(stringResource(R.string.finance_new_expense), style = MaterialTheme.typography.titleMedium)
        }

        Box(Modifier.size(16.dp))
        SavingsJarCard()

        Box(Modifier.size(16.dp))
        MonthOverviewCard()

        Box(Modifier.size(16.dp))
        ExpenseBreakdownCard()

        Box(Modifier.size(16.dp))
        RecentExpensesCard()

        Box(Modifier.size(24.dp))
    }
}

@Composable
private fun SavingsJarCard() {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(stringResource(FinanceSavingsGoal.titleRes), style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
            Text(
                text = stringResource(R.string.finance_savings_progress, "$${FinanceSavingsGoal.current}", "$${FinanceSavingsGoal.target}"),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Box(Modifier.size(20.dp))
            SavingsJarGraphic()
        }
    }
}

@Composable
private fun SavingsJarGraphic() {
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
                .background(Brush.verticalGradient(colors = listOf(BrandOrange, BrandOrangeDeep))),
        )
    }
}

@Composable
private fun MonthOverviewCard() {
    Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)) {
        Column(Modifier.padding(24.dp)) {
            Text(stringResource(R.string.finance_this_month), style = MaterialTheme.typography.titleLarge)
            Box(Modifier.size(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                StatTile(Icons.AutoMirrored.Outlined.TrendingDown, stringResource(R.string.finance_spent), "$$MonthlySpent", Modifier.weight(1f))
                StatTile(Icons.Outlined.AccountBalanceWallet, stringResource(R.string.finance_remaining), "$$MonthlyRemaining", Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun StatTile(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String, modifier: Modifier = Modifier) {
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
private fun ExpenseBreakdownCard() {
    Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)) {
        Column(Modifier.padding(24.dp)) {
            Text(stringResource(R.string.finance_breakdown), style = MaterialTheme.typography.titleLarge)
            Box(Modifier.size(20.dp))
            ExpenseBreakdown.forEach { share ->
                Column(Modifier.padding(bottom = 20.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(stringResource(share.labelRes), style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("$${share.amount.toInt()}", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    LinearProgressIndicator(
                        progress = { share.fraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp)
                            .height(16.dp)
                            .clip(RoundedCornerShape(50)),
                        color = share.color,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun RecentExpensesCard() {
    Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)) {
        Column(Modifier.padding(24.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(stringResource(R.string.finance_recent_expenses), style = MaterialTheme.typography.titleLarge)
                Text(
                    stringResource(R.string.finance_view_all),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { },
                )
            }
            Box(Modifier.size(16.dp))
            RecentExpenses.forEach { expense -> ExpenseRow(expense) }
        }
    }
}

@Composable
private fun ExpenseRow(expense: Expense) {
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
                .background(expense.iconBackground),
            contentAlignment = Alignment.Center,
        ) {
            Icon(expense.icon, contentDescription = null, tint = MaterialTheme.colorScheme.onPrimary)
        }
        Column(Modifier.weight(1f)) {
            Text(stringResource(expense.merchantRes), style = MaterialTheme.typography.titleMedium)
            Text(stringResource(expense.categoryRes), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text("-$${"%.2f".format(expense.amount)}", style = MaterialTheme.typography.titleMedium)
            Text(stringResource(expense.dateRes), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
