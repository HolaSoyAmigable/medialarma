package com.medialarma.app.data.remote

import com.medialarma.app.data.local.TokenStore
import okhttp3.Interceptor
import okhttp3.Response

/** Agrega el Bearer a todas las peticiones cuando hay sesión. */
class AuthInterceptor(private val tokenStore: TokenStore) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = tokenStore.accessToken()
        val request = if (token != null && chain.request().header("Authorization") == null) {
            chain.request().newBuilder().header("Authorization", "Bearer $token").build()
        } else {
            chain.request()
        }
        return chain.proceed(request)
    }
}
