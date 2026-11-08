package com.medialarma.app.ui.medications

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.medialarma.app.di.AppViewModelProvider
import com.medialarma.app.domain.model.Medication

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationListScreen(
    onAdd: () -> Unit,
    onOpen: (String) -> Unit,
    onLogout: () -> Unit,
    viewModel: MedicationListViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // Recarga la lista al abrir y al volver de crear, editar o eliminar
    LifecycleResumeEffect(Unit) {
        viewModel.load()
        onPauseOrDispose { }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis medicamentos") },
                actions = { TextButton(onClick = onLogout) { Text("Cerrar sesión") } }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = onAdd) { Text("Agregar") }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val s = state) {
                MedicationListUiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))

                is MedicationListUiState.Error -> Column(
                    modifier = Modifier.align(Alignment.Center).padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(s.message, color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
                    Button(onClick = viewModel::load) { Text("Reintentar") }
                }

                is MedicationListUiState.Success -> if (s.medications.isEmpty()) {
                    Text(
                        "Aún no tienes medicamentos.\nToca \"Agregar\" para registrar el primero.",
                        modifier = Modifier.align(Alignment.Center).padding(24.dp),
                        textAlign = TextAlign.Center
                    )
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(s.medications, key = { it.id }) { medication ->
                            MedicationCard(medication, onClick = { onOpen(medication.id) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MedicationCard(medication: Medication, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(medication.name, style = MaterialTheme.typography.titleMedium)
            Text(medication.dosage, style = MaterialTheme.typography.bodyMedium)
            if (!medication.active) {
                Text("Inactivo", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.outline)
            }
        }
    }
}
