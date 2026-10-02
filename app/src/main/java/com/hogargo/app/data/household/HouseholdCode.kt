package com.hogargo.app.data.household

import kotlin.random.Random

/**
 * Invitation codes look like `HGR-789`: 3 letters + 3 digits. They are stored without the dash
 * (`HGR789`) so typing, pasting or scanning either form resolves to the same household.
 */
object HouseholdCode {
    const val LENGTH = 6

    // No I/O/0/1 so a code read aloud or from a screenshot can't be mistyped.
    private const val LETTERS = "ABCDEFGHJKLMNPQRSTUVWXYZ"
    private const val DIGITS = "23456789"

    fun generate(random: Random = Random.Default): String = buildString {
        repeat(3) { append(LETTERS[random.nextInt(LETTERS.length)]) }
        repeat(3) { append(DIGITS[random.nextInt(DIGITS.length)]) }
    }

    /** Uppercases and strips everything that is not A-Z / 0-9 (dashes, spaces), capped at [LENGTH]. */
    fun normalize(raw: String): String =
        raw.uppercase().filter { it in 'A'..'Z' || it in '0'..'9' }.take(LENGTH)

    /** `HGR789` -> `HGR-789`. */
    fun format(code: String): String =
        if (code.length > 3) "${code.take(3)}-${code.drop(3)}" else code
}
