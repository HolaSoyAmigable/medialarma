package com.medialarma.app.di

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.medialarma.app.MediAlarmaApp
import com.medialarma.app.ui.auth.LoginViewModel
import com.medialarma.app.ui.auth.RegisterViewModel
import com.medialarma.app.ui.session.SessionViewModel

private fun CreationExtras.app(): MediAlarmaApp = this[APPLICATION_KEY] as MediAlarmaApp

object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer { SessionViewModel(app().container.tokenStore, app().container.authRepository) }
        initializer { LoginViewModel(app().container.authRepository) }
        initializer { RegisterViewModel(app().container.authRepository) }
    }
}
