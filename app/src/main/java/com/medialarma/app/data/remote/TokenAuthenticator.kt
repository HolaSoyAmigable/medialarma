package com.medialarma.app.data.remote

import com.medialarma.app.data.local.TokenStore
import com.medialarma.app.data.remote.dto.RefreshRequestDto
import com.medialarma.app.data.remote.dto.TokensDto
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.Authenticator
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.Route
import java.io.IOException

/**
 * Renueva el token cuando la API responde 401. El refresh token es de un solo uso,
 * así que la renovación está serializada con un candado.
 */
class TokenAuthenticator(
    private val tokenStore: TokenStore,
    private val refreshClient: OkHttpClient,
    private val baseUrl: String,
    private val json: Json
) : Authenticator {

    private val lock = Any()

    override fun authenticate(route: Route?, response: Response): Request? =
        synchronized(lock) { refreshAndRetry(response) }

    private fun refreshAndRetry(response: Response): Request? {
        if (response.priorResponse != null) return null // ya se reintentó una vez

        val sentToken = response.request.header("Authorization")?.removePrefix("Bearer ")
        val current = tokenStore.accessToken()
        // Otra petición ya renovó mientras esperábamos el candado
        if (current != null && sentToken != null && sentToken != current) {
            return retry(response, current)
        }

        val refresh = tokenStore.refreshToken() ?: return null
        val body = json.encodeToString(RefreshRequestDto(refresh))
            .toRequestBody("application/json".toMediaType())
        val request = Request.Builder().url(baseUrl + "auth/refresh").post(body).build()

        return try {
            refreshClient.newCall(request).execute().use { r ->
                when {
                    r.isSuccessful -> {
                        val tokens = json.decodeFromString<TokensDto>(r.body.string())
                        tokenStore.save(tokens.accessToken, tokens.refreshToken)
                        retry(response, tokens.accessToken)
                    }
                    r.code == 401 -> { tokenStore.clear(); null } // sesión perdida -> login
                    else -> null
                }
            }
        } catch (e: IOException) {
            null
        }
    }

    private fun retry(response: Response, token: String): Request =
        response.request.newBuilder().header("Authorization", "Bearer $token").build()
}
