package com.hogargo.app.ui.pet

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hogargo.app.R
import com.hogargo.app.data.pet.WardrobeCatalog

/** Where an item sits over the fox art, as fractions of the avatar size (center point) and item size. */
private data class Placement(val cx: Float, val cy: Float, val size: Float, val behindFox: Boolean = false)

private val Placements = mapOf(
    "skateboard" to Placement(cx = 0.50f, cy = 0.91f, size = 0.36f, behindFox = true),
    "scarf" to Placement(cx = 0.40f, cy = 0.57f, size = 0.24f),
    "glasses" to Placement(cx = 0.36f, cy = 0.39f, size = 0.26f),
    "ball" to Placement(cx = 0.83f, cy = 0.82f, size = 0.18f),
)

/** Zori with the currently equipped wardrobe items drawn on top. */
@Composable
fun ZoriAvatar(equippedIds: Set<String>, size: Dp, modifier: Modifier = Modifier) {
    val items = WardrobeCatalog.filter { it.id in equippedIds }
    Box(modifier = modifier.size(size)) {
        items.filter { Placements[it.id]?.behindFox == true }.forEach { WearableItem(it.emoji, Placements.getValue(it.id), size) }
        Image(
            painter = painterResource(R.drawable.img_zori_fox),
            contentDescription = null,
            modifier = Modifier.size(size),
        )
        items.filter { Placements[it.id]?.behindFox == false }.forEach { WearableItem(it.emoji, Placements.getValue(it.id), size) }
    }
}

@Composable
private fun BoxScope.WearableItem(emoji: String, placement: Placement, avatarSize: Dp) {
    val itemSize = avatarSize * placement.size
    val fontSize = with(LocalDensity.current) { (itemSize * 0.85f).toSp() }
    Text(
        text = emoji,
        fontSize = fontSize,
        modifier = Modifier
            .align(Alignment.TopStart)
            .offset(x = avatarSize * placement.cx - itemSize / 2, y = avatarSize * placement.cy - itemSize / 2)
            .size(itemSize),
    )
}
