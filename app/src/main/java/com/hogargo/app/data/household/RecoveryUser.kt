package com.hogargo.app.data.household

import java.security.MessageDigest

/**
 * The "usuario de recuperación": a second identifier, different from the display name, that lets a
 * member see the codes of the households they still belong to. Only its SHA-256 hash is stored.
 */
object RecoveryUser {
    const val MIN_LENGTH = 4

    fun normalize(raw: String): String = raw.trim().lowercase()

    fun hash(raw: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest(normalize(raw).toByteArray(Charsets.UTF_8))
            .joinToString("") { "%02x".format(it) }
}
