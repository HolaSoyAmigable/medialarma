package com.medialarma.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.medialarma.app.di.AppViewModelProvider
import com.medialarma.app.ui.auth.LoginScreen
import com.medialarma.app.ui.auth.RegisterScreen
import com.medialarma.app.ui.home.HomeScreen
import com.medialarma.app.ui.session.SessionViewModel

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
}

@Composable
fun AppNavHost(sessionViewModel: SessionViewModel = viewModel(factory = AppViewModelProvider.Factory)) {
    val loggedIn by sessionViewModel.isLoggedIn.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    val startDestination = remember { if (loggedIn) Routes.HOME else Routes.LOGIN }

    // La navegación sigue a la sesión: al entrar o salir se vacía la pila, así "Atrás" no regresa.
    LaunchedEffect(loggedIn) {
        val current = navController.currentDestination?.route ?: return@LaunchedEffect
        val inAuth = current == Routes.LOGIN || current == Routes.REGISTER
        if (loggedIn && inAuth) {
            navController.navigate(Routes.HOME) { popUpTo(0) { inclusive = true } }
        } else if (!loggedIn && !inAuth) {
            navController.navigate(Routes.LOGIN) { popUpTo(0) { inclusive = true } }
        }
    }

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Routes.LOGIN) {
            LoginScreen(onGoToRegister = { navController.navigate(Routes.REGISTER) })
        }
        composable(Routes.REGISTER) {
            RegisterScreen(onGoToLogin = { navController.popBackStack() })
        }
        composable(Routes.HOME) {
            HomeScreen()
        }
    }
}
