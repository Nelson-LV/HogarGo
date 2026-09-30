package com.hogargo.app.ui.pet

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import com.hogargo.app.data.local.WardrobeItemEntity
import com.hogargo.app.data.pet.WardrobeCatalog
import com.hogargo.app.data.pet.WardrobeCatalogItem
import com.hogargo.app.ui.theme.BrandOrange
import com.hogargo.app.ui.theme.BrandOrangeDeep

@Composable
fun PetScreen(viewModel: PetViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val feedCooldownMs by viewModel.feedCooldownMs.collectAsState()
    val playCooldownMs by viewModel.playCooldownMs.collectAsState()
    val petState = uiState.petState

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
        )

        Box(Modifier.size(16.dp))
        PetStatsCard(petState)

        Box(Modifier.size(16.dp))
        WardrobeCard(level = petState?.level ?: 1, equippedItems = uiState.wardrobe, onToggle = viewModel::toggleEquip)

        Box(Modifier.size(16.dp))
        TipCard()

        Box(Modifier.size(24.dp))
    }
}

@Composable
private fun PetStageCard(feedCooldownMs: Long, playCooldownMs: Long, onFeed: () -> Unit, onPlay: () -> Unit) {
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
            Box(Modifier.size(20.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = onFeed, colors = ButtonDefaults.buttonColors(containerColor = BrandOrangeDeep), shape = RoundedCornerShape(50)) {
                    Text(stringResource(R.string.pet_feed))
                }
                Button(onClick = onPlay, colors = ButtonDefaults.buttonColors(containerColor = BrandOrange, contentColor = MaterialTheme.colorScheme.onPrimaryContainer), shape = RoundedCornerShape(50)) {
                    Text(stringResource(R.string.pet_play))
                }
                OutlinedButton(onClick = { }, shape = RoundedCornerShape(50)) {
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
private fun WardrobeCard(level: Int, equippedItems: List<WardrobeItemEntity>, onToggle: (String) -> Unit) {
    Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.padding(24.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(stringResource(R.string.pet_wardrobe_title), style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                Text(stringResource(R.string.pet_shop), style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(
                stringResource(R.string.pet_wardrobe_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp),
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.height(380.dp),
            ) {
                items(WardrobeCatalog) { catalogItem ->
                    val equipped = equippedItems.find { it.id == catalogItem.id }?.equipped ?: false
                    val unlocked = level >= catalogItem.unlockLevel
                    WardrobeItemCard(item = catalogItem, unlocked = unlocked, equipped = equipped, onToggle = { onToggle(catalogItem.id) })
                }
            }
        }
    }
}

@Composable
private fun WardrobeItemCard(item: WardrobeCatalogItem, unlocked: Boolean, equipped: Boolean, onToggle: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (unlocked) MaterialTheme.colorScheme.surfaceContainerHigh else MaterialTheme.colorScheme.surfaceContainer)
            .border(
                width = if (unlocked) 0.dp else 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = RoundedCornerShape(16.dp),
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
                .alpha(if (unlocked) 1f else 0.6f),
            contentAlignment = Alignment.Center,
        ) {
            if (unlocked) {
                Icon(item.icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            } else {
                Icon(Icons.Outlined.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Text(
            text = stringResource(item.nameRes),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp),
        )
        Box(Modifier.size(8.dp))
        if (unlocked) {
            Button(
                onClick = onToggle,
                colors = ButtonDefaults.buttonColors(containerColor = BrandOrange, contentColor = MaterialTheme.colorScheme.onPrimaryContainer),
                shape = RoundedCornerShape(50),
                contentPadding = PaddingValues(vertical = 4.dp, horizontal = 12.dp),
            ) {
                Text(
                    text = stringResource(if (equipped) R.string.pet_equip else R.string.pet_wear),
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        } else {
            Text(
                text = stringResource(R.string.pet_level_required, item.unlockLevel),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
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
