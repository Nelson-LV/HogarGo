package com.hogargo.app.data.pet

import com.hogargo.app.data.local.PetStateDao
import com.hogargo.app.data.local.TaskDao
import com.hogargo.app.data.local.PetStateEntity
import com.hogargo.app.data.local.WardrobeItemDao
import com.hogargo.app.data.local.WardrobeItemEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first

private const val DECAY_INTERVAL_MILLIS = 2 * 60 * 60 * 1000L // 2 hours
private const val DECAY_AMOUNT = 0.05f
private const val CARE_GAIN = 0.15f
private const val COOLDOWN_MILLIS = 15 * 60 * 1000L // 15 minutes
const val ACTIONS_PER_LEVEL = 5

enum class CareAction { FEED, PLAY }

class PetRepository(
    private val petStateDao: PetStateDao,
    private val wardrobeItemDao: WardrobeItemDao,
    private val taskDao: TaskDao,
    private val now: () -> Long = System::currentTimeMillis,
) {
    val petState: Flow<PetStateEntity?> = petStateDao.observe()
    val wardrobeItems: Flow<List<WardrobeItemEntity>> = wardrobeItemDao.observeAll()

    /** Coins earned from completed tasks minus the price of owned wardrobe items. */
    val coins: Flow<Int> = combine(taskDao.getAllTasks(), wardrobeItemDao.observeAll()) { tasks, items ->
        computeCoins(tasks.filter { it.completed }.sumOf { it.coinReward }, items)
    }

    /** Call once when the screen opens: seeds default rows and applies any pending time-based decay. */
    suspend fun refreshOnOpen() {
        wardrobeItemDao.insertIfAbsent(WardrobeCatalog.map { WardrobeItemEntity(id = it.id, equipped = false) })

        val current = petStateDao.getOnce() ?: PetStateEntity(lastDecayAt = now())
        petStateDao.upsert(applyDecay(current))
    }

    /** Returns how many milliseconds remain before [action] is available again, or 0 if it's ready now. */
    suspend fun cooldownRemaining(action: CareAction): Long {
        val state = petStateDao.getOnce() ?: return 0L
        val lastAt = if (action == CareAction.FEED) state.lastFeedAt else state.lastPlayAt
        val remaining = COOLDOWN_MILLIS - (now() - lastAt)
        return remaining.coerceAtLeast(0L)
    }

    suspend fun performCareAction(action: CareAction) {
        val current = petStateDao.getOnce() ?: PetStateEntity(lastDecayAt = now())
        val decayed = applyDecay(current)
        val lastAt = if (action == CareAction.FEED) decayed.lastFeedAt else decayed.lastPlayAt
        if (now() - lastAt < COOLDOWN_MILLIS) return

        val updated = when (action) {
            CareAction.FEED -> decayed.copy(
                satiety = (decayed.satiety + CARE_GAIN).coerceAtMost(1f),
                lastFeedAt = now(),
                careActions = decayed.careActions + 1,
            )
            CareAction.PLAY -> decayed.copy(
                happiness = (decayed.happiness + CARE_GAIN).coerceAtMost(1f),
                lastPlayAt = now(),
                careActions = decayed.careActions + 1,
            )
        }
        petStateDao.upsert(updated)
    }

    suspend fun toggleEquipped(itemId: String) {
        val catalogEntry = WardrobeCatalog.find { it.id == itemId } ?: return
        val level = petStateDao.getOnce()?.level ?: 1
        if (level < catalogEntry.unlockLevel) return

        if (wardrobeItemDao.getOnce(itemId)?.owned != true) return

        val currentlyEquipped = wardrobeItemDao.getOnce(itemId)?.equipped ?: false
        wardrobeItemDao.upsert(WardrobeItemEntity(id = itemId, equipped = !currentlyEquipped, owned = true))
    }

    /** Buys [itemId] if the level is high enough and the balance covers the price. Returns true on success. */
    suspend fun buy(itemId: String): Boolean {
        val catalogEntry = WardrobeCatalog.find { it.id == itemId } ?: return false
        val level = petStateDao.getOnce()?.level ?: 1
        if (level < catalogEntry.unlockLevel) return false
        if (wardrobeItemDao.getOnce(itemId)?.owned == true) return false
        if (coins.first() < catalogEntry.price) return false

        wardrobeItemDao.upsert(WardrobeItemEntity(id = itemId, equipped = false, owned = true))
        return true
    }

    private fun computeCoins(earned: Int, items: List<WardrobeItemEntity>): Int {
        val spent = items.filter { it.owned }.sumOf { item -> WardrobeCatalog.find { it.id == item.id }?.price ?: 0 }
        return (earned - spent).coerceAtLeast(0)
    }

    private fun applyDecay(state: PetStateEntity): PetStateEntity {
        val elapsed = now() - state.lastDecayAt
        val chunks = elapsed / DECAY_INTERVAL_MILLIS
        if (chunks <= 0) return state
        return state.copy(
            happiness = (state.happiness - DECAY_AMOUNT * chunks).coerceAtLeast(0f),
            satiety = (state.satiety - DECAY_AMOUNT * chunks).coerceAtLeast(0f),
            lastDecayAt = state.lastDecayAt + chunks * DECAY_INTERVAL_MILLIS,
        )
    }
}
