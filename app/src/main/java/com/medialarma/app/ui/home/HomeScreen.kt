package com.medialarma.app.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.medialarma.app.di.AppViewModelProvider
import com.medialarma.app.ui.session.SessionViewModel

/** Provisional: se reemplaza por la lista de medicamentos en el siguiente lote. */
@Composable
fun HomeScreen(sessionViewModel: SessionViewModel = viewModel(factory = AppViewModelProvider.Factory)) {
    Scaffold { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Sesión iniciada")
            Button(onClick = sessionViewModel::logout) { Text("Cerrar sesión") }
        }
    }
}
