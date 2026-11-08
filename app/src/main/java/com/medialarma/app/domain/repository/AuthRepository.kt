package com.medialarma.app.domain.repository

import com.medialarma.app.domain.model.User

interface AuthRepository {
    suspend fun register(email: String, password: String, displayName: String): Result<User>
    suspend fun login(email: String, password: String): Result<User>
    suspend fun logout()
}
