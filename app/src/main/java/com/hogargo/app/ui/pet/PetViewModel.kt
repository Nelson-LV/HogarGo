package com.hogargo.app.ui.pet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.hogargo.app.data.local.PetStateEntity
import com.hogargo.app.data.local.WardrobeItemEntity
import com.hogargo.app.data.pet.CareAction
import com.hogargo.app.data.pet.PetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class PetUiState(
    val petState: PetStateEntity? = null,
    val wardrobe: List<WardrobeItemEntity> = emptyList(),
)

class PetViewModel(private val repository: PetRepository) : ViewModel() {

    val uiState: StateFlow<PetUiState> = combine(repository.petState, repository.wardrobeItems) { pet, wardrobe ->
        PetUiState(petState = pet, wardrobe = wardrobe)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), PetUiState())

    private val _feedCooldownMs = MutableStateFlow(0L)
    val feedCooldownMs: StateFlow<Long> = _feedCooldownMs

    private val _playCooldownMs = MutableStateFlow(0L)
    val playCooldownMs: StateFlow<Long> = _playCooldownMs

    init {
        viewModelScope.launch { repository.refreshOnOpen() }
    }

    fun feed() = performAction(CareAction.FEED)

    fun play() = performAction(CareAction.PLAY)

    private fun performAction(action: CareAction) {
        viewModelScope.launch {
            val remaining = repository.cooldownRemaining(action)
            if (remaining > 0) {
                setCooldown(action, remaining)
            } else {
                repository.performCareAction(action)
                setCooldown(action, 0L)
            }
        }
    }

    private fun setCooldown(action: CareAction, remaining: Long) {
        when (action) {
            CareAction.FEED -> _feedCooldownMs.value = remaining
            CareAction.PLAY -> _playCooldownMs.value = remaining
        }
    }

    fun toggleEquip(itemId: String) {
        viewModelScope.launch { repository.toggleEquipped(itemId) }
    }

    companion object {
        fun factory(repository: PetRepository) = viewModelFactory {
            initializer { PetViewModel(repository) }
        }
    }
}
