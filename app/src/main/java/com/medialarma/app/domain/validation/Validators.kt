package com.medialarma.app.domain.validation

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

object Validators {
    private val EMAIL = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
    private val DATE = Regex("^\\d{4}-\\d{2}-\\d{2}$")

    fun isValidEmail(value: String): Boolean {
        val v = value.trim()
        return v.length <= 254 && EMAIL.matches(v)
    }

    /** La API acepta de 8 a 72 bytes en UTF-8. */
    fun isValidPassword(value: String): Boolean =
        value.length >= 8 && value.toByteArray(Charsets.UTF_8).size <= 72

    fun isValidName(value: String): Boolean = value.trim().length in 1..60

    /** Fecha yyyy-MM-dd real (rechaza 2026-02-30). */
    fun isValidDate(value: String): Boolean {
        val v = value.trim()
        if (!DATE.matches(v)) return false
        return try {
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            format.isLenient = false
            format.parse(v) != null
        } catch (e: ParseException) {
            false
        }
    }
}
