package com.hogargo.app.data.household

import android.content.Context

/** Remembers which person is signed in on this device (survives app restarts). */
class SessionStore(context: Context) {
    private val prefs = context.applicationContext
        .getSharedPreferences("hogargo_session", Context.MODE_PRIVATE)

    val memberId: String? get() = prefs.getString(KEY_MEMBER, null)
    val householdId: String? get() = prefs.getString(KEY_HOUSEHOLD, null)

    fun save(memberId: String, householdId: String) {
        prefs.edit().putString(KEY_MEMBER, memberId).putString(KEY_HOUSEHOLD, householdId).apply()
    }

    fun clear() {
        prefs.edit().clear().apply()
    }

    private companion object {
        const val KEY_MEMBER = "member_id"
        const val KEY_HOUSEHOLD = "household_id"
    }
}
