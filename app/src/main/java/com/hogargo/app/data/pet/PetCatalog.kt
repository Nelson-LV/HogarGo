package com.hogargo.app.data.pet

import androidx.annotation.StringRes
import com.hogargo.app.R

/** Static catalog of wardrobe items. Only [WardrobeItemEntity.equipped] is persisted per item. */
data class WardrobeCatalogItem(
    val id: String,
    @StringRes val nameRes: Int,
    val emoji: String,
    val unlockLevel: Int,
)

val WardrobeCatalog = listOf(
    WardrobeCatalogItem("scarf", R.string.pet_item_scarf, "\uD83E\uDDE3", unlockLevel = 1),
    WardrobeCatalogItem("ball", R.string.pet_item_ball, "\u26BD", unlockLevel = 1),
    WardrobeCatalogItem("glasses", R.string.pet_item_glasses, "\uD83D\uDD76\uFE0F", unlockLevel = 3),
    WardrobeCatalogItem("skateboard", R.string.pet_item_skateboard, "\uD83D\uDEF9", unlockLevel = 5),
)
