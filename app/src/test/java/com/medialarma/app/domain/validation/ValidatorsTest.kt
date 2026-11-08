package com.medialarma.app.domain.validation

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidatorsTest {
    @Test
    fun correoValido() {
        assertTrue(Validators.isValidEmail("ana@correo.com"))
    }

    @Test
    fun correoInvalido() {
        assertFalse(Validators.isValidEmail("ana"))
        assertFalse(Validators.isValidEmail("ana@correo"))
    }

    @Test
    fun contrasenaDebeTenerOchoCaracteres() {
        assertFalse(Validators.isValidPassword("1234567"))
        assertTrue(Validators.isValidPassword("12345678"))
    }

    @Test
    fun fechaDebeExistirEnElCalendario() {
        assertTrue(Validators.isValidDate("2026-11-08"))
        assertFalse(Validators.isValidDate("2026-02-30"))
        assertFalse(Validators.isValidDate("08/11/2026"))
    }
}