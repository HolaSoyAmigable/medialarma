package com.medialarma.app.domain.validation

object Validators {
    private val EMAIL = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")

    fun isValidEmail(value: String): Boolean {
        val v = value.trim()
        return v.length <= 254 && EMAIL.matches(v)
    }

    /** La API acepta de 8 a 72 bytes en UTF-8. */
    fun isValidPassword(value: String): Boolean =
        value.length >= 8 && value.toByteArray(Charsets.UTF_8).size <= 72

    fun isValidName(value: String): Boolean = value.trim().length in 1..60
}
