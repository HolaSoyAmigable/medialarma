package com.medialarma.app.data.repository

import com.medialarma.app.data.local.TokenStore
import com.medialarma.app.data.mapper.toDomain
import com.medialarma.app.data.remote.ApiErrorParser
import com.medialarma.app.data.remote.MediAlarmaApi
import com.medialarma.app.data.remote.dto.LoginRequestDto
import com.medialarma.app.data.remote.dto.LogoutRequestDto
import com.medialarma.app.data.remote.dto.RegisterRequestDto
import com.medialarma.app.domain.model.User
import com.medialarma.app.domain.repository.AuthRepository
import retrofit2.HttpException
import java.io.IOException
import java.util.TimeZone

class AuthRepositoryImpl(
    private val api: MediAlarmaApi,
    private val tokenStore: TokenStore,
    private val errors: ApiErrorParser
) : AuthRepository {

    override suspend fun register(email: String, password: String, displayName: String): Result<User> =
        errors.call {
            val r = api.register(
                RegisterRequestDto(email.trim(), password, displayName.trim(), TimeZone.getDefault().id)
            )
            tokenStore.save(r.tokens.accessToken, r.tokens.refreshToken)
            r.user.toDomain()
        }

    override suspend fun login(email: String, password: String): Result<User> =
        errors.call {
            val r = api.login(LoginRequestDto(email.trim(), password))
            tokenStore.save(r.tokens.accessToken, r.tokens.refreshToken)
            r.user.toDomain()
        }

    override suspend fun logout() {
        val refresh = tokenStore.refreshToken()
        try {
            if (refresh != null) api.logout(LogoutRequestDto(refresh))
        } catch (_: IOException) {
        } catch (_: HttpException) {
        } finally {
            tokenStore.clear() // siempre se limpia la sesión local
        }
    }
}
