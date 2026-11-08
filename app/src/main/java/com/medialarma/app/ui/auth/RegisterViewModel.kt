package com.medialarma.app.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medialarma.app.domain.repository.AuthRepository
import com.medialarma.app.domain.validation.Validators
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RegisterUiState(
    val displayName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val nameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmError: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class RegisterViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onNameChange(v: String) { _uiState.update { it.copy(displayName = v, nameError = null, error = null) } }
    fun onEmailChange(v: String) { _uiState.update { it.copy(email = v, emailError = null, error = null) } }
    fun onPasswordChange(v: String) { _uiState.update { it.copy(password = v, passwordError = null, error = null) } }
    fun onConfirmChange(v: String) { _uiState.update { it.copy(confirmPassword = v, confirmError = null, error = null) } }

    fun onRegisterClick() {
        val s = _uiState.value
        if (s.isLoading) return
        val nameError = if (!Validators.isValidName(s.displayName)) "Escribe tu nombre (máx. 60 caracteres)" else null
        val emailError = if (!Validators.isValidEmail(s.email)) "Escribe un correo válido" else null
        val passwordError = if (!Validators.isValidPassword(s.password)) "La contraseña debe tener al menos 8 caracteres" else null
        val confirmError = if (s.password != s.confirmPassword) "Las contraseñas no coinciden" else null
        if (listOf(nameError, emailError, passwordError, confirmError).any { it != null }) {
            _uiState.update {
                it.copy(nameError = nameError, emailError = emailError, passwordError = passwordError, confirmError = confirmError)
            }
            return
        }
        _uiState.update { it.copy(isLoading = true, error = null) }
        viewModelScope.launch {
            authRepository.register(s.email, s.password, s.displayName)
                .onSuccess { _uiState.update { it.copy(isLoading = false) } }
                .onFailure { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
        }
    }
}
