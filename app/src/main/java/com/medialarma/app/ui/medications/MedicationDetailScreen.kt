package com.medialarma.app.ui.medications

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.medialarma.app.di.AppViewModelProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationDetailScreen(
    onBack: () -> Unit,
    onEdit: (String) -> Unit,
    viewModel: MedicationDetailViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LifecycleResumeEffect(Unit) {
        viewModel.load()
        onPauseOrDispose { }
    }
    LaunchedEffect(state.deleted) {
        if (state.deleted) onBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            val medication = state.medication
            when {
                medication != null -> Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    DetailRow("Nombre", medication.name)
                    DetailRow("Dosis", medication.dosage)
                    DetailRow("Notas", medication.notes ?: "—")
                    DetailRow("Fecha de inicio", medication.startDate)
                    DetailRow("Fecha de fin", medication.endDate ?: "—")
                    DetailRow("Estado", if (medication.active) "Activo" else "Inactivo")

                    state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(onClick = { onEdit(medication.id) }, modifier = Modifier.weight(1f)) {
                            Text("Editar")
                        }
                        Button(
                            onClick = viewModel::onDeleteClick,
                            enabled = !state.isDeleting,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Eliminar")
                        }
                    }
                }

                state.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))

                else -> Column(
                    modifier = Modifier.align(Alignment.Center).padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        state.error ?: "No se pudo cargar el medicamento.",
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                    Button(onClick = viewModel::load) { Text("Reintentar") }
                }
            }
        }
    }

    if (state.showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = viewModel::onDeleteDismiss,
            title = { Text("Eliminar medicamento") },
            text = { Text("¿Seguro que quieres eliminarlo? Se borrarán también sus horarios.") },
            confirmButton = { TextButton(onClick = viewModel::onDeleteConfirm) { Text("Eliminar") } },
            dismissButton = { TextButton(onClick = viewModel::onDeleteDismiss) { Text("Cancelar") } }
        )
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyLarge)
    }
}
