package com.medialarma.app.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

/** Abstracción del almacenamiento de sesión. */
interface TokenStore {
    val isLoggedIn: StateFlow<Boolean>
    fun accessToken(): String?
    fun refreshToken(): String?
    fun save(accessToken: String, refreshToken: String)
    fun clear()
}

val Context.sessionDataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

class DataStoreTokenStore(private val dataStore: DataStore<Preferences>) : TokenStore {
    private val accessKey = stringPreferencesKey("access_token")
    private val refreshKey = stringPreferencesKey("refresh_token")

    @Volatile private var access: String? = null
    @Volatile private var refresh: String? = null
    private val _isLoggedIn = MutableStateFlow(false)
    override val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    init {
        // Lectura única al crear el contenedor, para saber si la sesión persiste al reabrir la app
        val prefs = runBlocking { dataStore.data.first() }
        access = prefs[accessKey]
        refresh = prefs[refreshKey]
        _isLoggedIn.value = refresh != null
    }

    override fun accessToken(): String? = access
    override fun refreshToken(): String? = refresh

    override fun save(accessToken: String, refreshToken: String) {
        access = accessToken
        refresh = refreshToken
        runBlocking {
            dataStore.edit {
                it[accessKey] = accessToken
                it[refreshKey] = refreshToken
            }
        }
        _isLoggedIn.value = true
    }

    override fun clear() {
        access = null
        refresh = null
        runBlocking { dataStore.edit { it.clear() } }
        _isLoggedIn.value = false
    }
}
