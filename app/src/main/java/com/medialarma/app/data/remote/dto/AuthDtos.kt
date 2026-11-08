package com.medialarma.app.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequestDto(
    val email: String,
    val password: String,
    val displayName: String,
    val timezone: String
)

@Serializable
data class LoginRequestDto(val email: String, val password: String)

@Serializable
data class RefreshRequestDto(val refreshToken: String)

@Serializable
data class LogoutRequestDto(val refreshToken: String)

@Serializable
data class UserDto(
    val id: String,
    val email: String,
    val displayName: String,
    val timezone: String,
    val createdAt: String
)

@Serializable
data class TokensDto(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String,
    val expiresIn: Int
)

@Serializable
data class AuthResponseDto(val user: UserDto, val tokens: TokensDto)

@Serializable
data class ErrorEnvelopeDto(val error: ErrorDto)

@Serializable
data class ErrorDto(
    val code: String,
    val message: String,
    val details: List<ErrorDetailDto>? = null,
    val requestId: String? = null
)

@Serializable
data class ErrorDetailDto(val field: String, val issue: String)
