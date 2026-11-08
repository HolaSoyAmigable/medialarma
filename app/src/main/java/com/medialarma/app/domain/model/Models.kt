package com.medialarma.app.domain.model

data class User(
    val id: String,
    val email: String,
    val displayName: String
)

/** Error de la API o de red, ya con mensaje listo para mostrar al usuario. */
class AppException(
    val code: String,
    override val message: String,
    val fieldErrors: Map<String, String> = emptyMap()
) : Exception(message)
