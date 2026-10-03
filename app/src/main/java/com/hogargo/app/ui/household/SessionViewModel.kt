package com.hogargo.app.ui.household

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.hogargo.app.data.household.ActiveSession
import com.hogargo.app.data.household.AuthError
import com.hogargo.app.data.household.AuthResult
import com.hogargo.app.data.household.RecoveredHousehold
import com.hogargo.app.data.household.HouseholdRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface SessionState {
    /** Checking whether somebody was already signed in. */
    data object Loading : SessionState
    data object LoggedOut : SessionState
    data class LoggedIn(val session: ActiveSession) : SessionState
}

class SessionViewModel(private val repository: HouseholdRepository) : ViewModel() {

    private val _state = MutableStateFlow<SessionState>(SessionState.Loading)
    val state: StateFlow<SessionState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val restored = repository.restoreSession()
            _state.value = if (restored != null) SessionState.LoggedIn(restored) else SessionState.LoggedOut
        }
    }

    suspend fun generateUniqueCode(): String = repository.generateUniqueCode()

    suspend fun createHousehold(userName: String, householdName: String, code: String, recoveryUser: String): AuthResult =
        repository.createHousehold(userName, householdName, code, recoveryUser).also(::apply)

    suspend fun joinHousehold(userName: String, code: String, recoveryUser: String): AuthResult =
        repository.joinHousehold(userName, code, recoveryUser).also(::apply)

    suspend fun recoverCodes(recoveryUser: String): List<RecoveredHousehold> =
        repository.recoverCodes(recoveryUser)

    suspend fun renameMember(memberId: String, newName: String): AuthError? =
        repository.renameMember(memberId, newName)

    suspend fun changeRecoveryUser(memberId: String, current: String, new: String, confirm: String): AuthError? =
        repository.changeRecoveryUser(memberId, current, new, confirm)

    /** The signed-in person leaves their household for good (see HouseholdRepository.leaveHousehold). */
    fun leaveHousehold() {
        val current = (_state.value as? SessionState.LoggedIn)?.session ?: return
        // Log out first so the UI never reacts to "my own member row disappeared".
        _state.value = SessionState.LoggedOut
        viewModelScope.launch { repository.leaveHousehold(current.member.id, current.household.id) }
    }

    suspend fun signIn(userName: String, code: String): AuthResult =
        repository.signIn(userName, code).also(::apply)

    fun signOut() {
        repository.signOut()
        _state.value = SessionState.LoggedOut
    }

    private fun apply(result: AuthResult) {
        if (result is AuthResult.Success) _state.value = SessionState.LoggedIn(result.session)
    }

    companion object {
        fun factory(repository: HouseholdRepository) = viewModelFactory {
            initializer { SessionViewModel(repository) }
        }
    }
}
