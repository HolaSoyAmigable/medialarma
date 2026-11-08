package com.medialarma.app.di

import android.content.Context
import com.medialarma.app.BuildConfig
import com.medialarma.app.data.local.DataStoreTokenStore
import com.medialarma.app.data.local.TokenStore
import com.medialarma.app.data.local.sessionDataStore
import com.medialarma.app.data.remote.ApiErrorParser
import com.medialarma.app.data.remote.AuthInterceptor
import com.medialarma.app.data.remote.MediAlarmaApi
import com.medialarma.app.data.remote.TokenAuthenticator
import com.medialarma.app.data.repository.AuthRepositoryImpl
import com.medialarma.app.domain.repository.AuthRepository
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

/** Contenedor de dependencias manual. Se crea una sola vez en MediAlarmaApp. */
class AppContainer(context: Context) {

    private val json = Json { ignoreUnknownKeys = true }

    val tokenStore: TokenStore = DataStoreTokenStore(context.applicationContext.sessionDataStore)

    // Cliente base sin interceptor: lo usa TokenAuthenticator para llamar a /auth/refresh
    private val baseHttpClient = OkHttpClient()

    private val httpClient: OkHttpClient = baseHttpClient.newBuilder()
        .addInterceptor(AuthInterceptor(tokenStore))
        .authenticator(TokenAuthenticator(tokenStore, baseHttpClient, BuildConfig.API_BASE_URL, json))
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.API_BASE_URL)
        .client(httpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    private val api: MediAlarmaApi = retrofit.create(MediAlarmaApi::class.java)
    private val errorParser = ApiErrorParser(json)

    val authRepository: AuthRepository = AuthRepositoryImpl(api, tokenStore, errorParser)
}
