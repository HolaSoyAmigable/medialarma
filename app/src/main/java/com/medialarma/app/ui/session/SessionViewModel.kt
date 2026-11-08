package com.medialarma.app.ui.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medialarma.app.data.local.TokenStore
import com.medialarma.app.domain.repository.AuthRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SessionViewModel(
    tokenStore: TokenStore,
    private val authRepository: AuthRepository
) : ViewModel() {
    val isLoggedIn: StateFlow<Boolean> = tokenStore.isLoggedIn

    fun logout() {
        viewModelScope.launch { authRepository.logout() }
    }
}
