package com.hogargo.app.ui.pet

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hogargo.app.R
import com.hogargo.app.data.local.PetStateEntity
import com.hogargo.app.data.pet.WardrobeCatalog
import com.hogargo.app.data.pet.WardrobeCatalogItem
import com.hogargo.app.ui.theme.BrandOrange
import com.hogargo.app.ui.theme.BrandOrangeDeep

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PetScreen(viewModel: PetViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val feedCooldownMs by viewModel.feedCooldownMs.collectAsState()
    val playCooldownMs by viewModel.playCooldownMs.collectAsState()
    val petState = uiState.petState
    val wardrobeRequester = remember { BringIntoViewRequester() }
    val scope = rememberCoroutineScope()
    val equippedIds = uiState.wardrobe.filter { it.equipped }.map { it.id }.toSet()
    val equippedItems = WardrobeCatalog.filter { it.id in equippedIds }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
    ) {
        Box(Modifier.size(16.dp))

        PetStageCard(
            feedCooldownMs = feedCooldownMs,
            playCooldownMs = playCooldownMs,
            onFeed = viewModel::feed,
            onPlay = viewModel::play,
            equippedItems = equippedItems,
            onDecorate = { scope.launch { wardrobeRequester.bringIntoView() } },
        )

        Box(Modifier.size(16.dp))
        PetStatsCard(petState)

        Box(Modifier.size(16.dp))
        WardrobeCard(
            level = petState?.level ?: 1,
            equippedIds = equippedIds,
            onToggle = viewModel::toggleEquip,
            modifier = Modifier.bringIntoViewRequester(wardrobeRequester),
        )

        Box(Modifier.size(16.dp))
        TipCard()

        Box(Modifier.size(24.dp))
    }
}

@Composable
private fun PetStageCard(
    feedCooldownMs: Long,
    playCooldownMs: Long,
    onFeed: () -> Unit,
    onPlay: () -> Unit,
    equippedItems: List<WardrobeCatalogItem>,
    onDecorate: () -> Unit,
) {
    Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(R.drawable.img_zori_fox),
                contentDescription = null,
                modifier = Modifier.size(160.dp),
            )
            Box(Modifier.size(8.dp))
            Text(
                text = if (equippedItems.isEmpty()) {
                    stringResource(R.string.pet_wearing_nothing)
                } else {
                    stringResource(R.string.pet_wearing) + " " + equippedItems.joinToString(" ") { it.emoji }
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Box(Modifier.size(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = onFeed, colors = ButtonDefaults.buttonColors(containerColor = BrandOrangeDeep), shape = RoundedCornerShape(50)) {
                    Text(stringResource(R.string.pet_feed))
                }
                Button(onClick = onPlay, colors = ButtonDefaults.buttonColors(containerColor = BrandOrange, contentColor = MaterialTheme.colorScheme.onPrimaryContainer), shape = RoundedCornerShape(50)) {
                    Text(stringResource(R.string.pet_play))
                }
                OutlinedButton(onClick = onDecorate, shape = RoundedCornerShape(50)) {
                    Text(stringResource(R.string.pet_decorate))
                }
            }
            val feedMinutes = (feedCooldownMs / 60_000L + 1).coerceAtLeast(1)
            val playMinutes = (playCooldownMs / 60_000L + 1).coerceAtLeast(1)
            if (feedCooldownMs > 0 || playCooldownMs > 0) {
                Box(Modifier.size(8.dp))
                Text(
                    text = when {
                        feedCooldownMs > 0 && playCooldownMs > 0 -> stringResource(R.string.pet_cooldown_available_in, maxOf(feedMinutes, playMinutes))
                        feedCooldownMs > 0 -> stringResource(R.string.pet_feed) + ": " + stringResource(R.string.pet_cooldown_available_in, feedMinutes)
                        else -> stringResource(R.string.pet_play) + ": " + stringResource(R.string.pet_cooldown_available_in, playMinutes)
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun PetStatsCard(petState: PetStateEntity?) {
    Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(24.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(stringResource(R.string.pet_status_title), style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(BrandOrangeDeep)
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                ) {
                    Text(stringResource(R.string.pet_level, petState?.level ?: 1), color = MaterialTheme.colorScheme.onPrimary, style = MaterialTheme.typography.labelLarge)
                }
            }
            Box(Modifier.size(16.dp))
            StatBar(stringResource(R.string.pet_happiness), petState?.happiness ?: 0.5f, BrandOrange)
            Box(Modifier.size(12.dp))
            StatBar(stringResource(R.string.pet_satiety), petState?.satiety ?: 0.5f, BrandOrangeDeep)
        }
    }
}

@Composable
private fun StatBar(label: String, value: Float, color: androidx.compose.ui.graphics.Color) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.bodyLarge)
            Text("${(value * 100).toInt()}%", style = MaterialTheme.typography.bodyLarge)
        }
        LinearProgressIndicator(
            progress = { value },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp)
                .height(16.dp)
                .clip(RoundedCornerShape(50)),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    }
}

@Composable
private fun WardrobeCard(level: Int, equippedIds: Set<String>, onToggle: (String) -> Unit, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(24.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(stringResource(R.string.pet_wardrobe_title), style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                Text(
                    "${equippedIds.size}/${WardrobeCatalog.size}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                stringResource(R.string.pet_wardrobe_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp),
            )
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                WardrobeCatalog.chunked(2).forEach { rowItems ->
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        rowItems.forEach { catalogItem ->
                            WardrobeItemCard(
                                item = catalogItem,
                                unlocked = level >= catalogItem.unlockLevel,
                                equipped = catalogItem.id in equippedIds,
                                onToggle = { onToggle(catalogItem.id) },
                                modifier = Modifier.weight(1f),
                            )
                        }
                        if (rowItems.size == 1) Box(Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun WardrobeItemCard(
    item: WardrobeCatalogItem,
    unlocked: Boolean,
    equipped: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier = modifier
            .clip(shape)
            .background(
                when {
                    equipped -> MaterialTheme.colorScheme.primaryContainer
                    unlocked -> MaterialTheme.colorScheme.surfaceContainerHigh
                    else -> MaterialTheme.colorScheme.surfaceContainer
                },
            )
            .border(
                width = if (equipped) 2.dp else if (unlocked) 0.dp else 1.dp,
                color = if (equipped) BrandOrangeDeep else MaterialTheme.colorScheme.outlineVariant,
                shape = shape,
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = item.emoji,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.alpha(if (unlocked) 1f else 0.35f),
            )
            if (!unlocked) {
                Icon(Icons.Outlined.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(22.dp))
            }
        }
        Text(
            text = stringResource(item.nameRes),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp),
        )
        Box(Modifier.size(8.dp))
        when {
            !unlocked -> Text(
                text = stringResource(R.string.pet_level_required, item.unlockLevel),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            equipped -> {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Check, contentDescription = null, tint = BrandOrangeDeep, modifier = Modifier.size(16.dp))
                    Text(
                        text = stringResource(R.string.pet_equipped_badge),
                        style = MaterialTheme.typography.labelMedium,
                        color = BrandOrangeDeep,
                        modifier = Modifier.padding(start = 4.dp),
                    )
                }
                OutlinedButton(
                    onClick = onToggle,
                    shape = RoundedCornerShape(50),
                    contentPadding = PaddingValues(vertical = 4.dp, horizontal = 12.dp),
                ) {
                    Text(stringResource(R.string.pet_remove), style = MaterialTheme.typography.labelMedium)
                }
            }
            else -> Button(
                onClick = onToggle,
                colors = ButtonDefaults.buttonColors(containerColor = BrandOrange, contentColor = MaterialTheme.colorScheme.onPrimaryContainer),
                shape = RoundedCornerShape(50),
                contentPadding = PaddingValues(vertical = 4.dp, horizontal = 12.dp),
            ) {
                Text(stringResource(R.string.pet_wear), style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}

@Composable
private fun TipCard() {
    Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = BrandOrangeDeep)) {
        Column(Modifier.padding(24.dp)) {
            Text(stringResource(R.string.pet_tip_title), style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onPrimary)
            Text(
                stringResource(R.string.pet_tip_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f),
            )
        }
    }
}
