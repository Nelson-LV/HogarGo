package com.hogargo.app.data.pet

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.DirectionsBike
import androidx.compose.material.icons.outlined.Checkroom
import androidx.compose.material.icons.outlined.SportsBasketball
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.ui.graphics.vector.ImageVector
import com.hogargo.app.R

/** Static catalog of wardrobe items. Only [WardrobeItemEntity.equipped] is persisted per item. */
data class WardrobeCatalogItem(
    val id: String,
    @StringRes val nameRes: Int,
    val icon: ImageVector,
    val unlockLevel: Int,
)

val WardrobeCatalog = listOf(
    WardrobeCatalogItem("scarf", R.string.pet_item_scarf, Icons.Outlined.Checkroom, unlockLevel = 1),
    WardrobeCatalogItem("ball", R.string.pet_item_ball, Icons.Outlined.SportsBasketball, unlockLevel = 1),
    WardrobeCatalogItem("glasses", R.string.pet_item_glasses, Icons.Outlined.Visibility, unlockLevel = 5),
    WardrobeCatalogItem("skateboard", R.string.pet_item_skateboard, Icons.AutoMirrored.Outlined.DirectionsBike, unlockLevel = 10),
)
