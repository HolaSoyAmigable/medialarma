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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.medialarma.app.di.AppViewModelProvider

/** Formulario reutilizable: crea si no hay id en la ruta y edita si lo hay. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicationFormScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: MedicationFormViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.saved) {
        if (state.saved) onSaved()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (state.isEditing) "Editar medicamento" else "Nuevo medicamento") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                state.isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))

                state.loadError != null -> Column(
                    modifier = Modifier.align(Alignment.Center).padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(state.loadError ?: "", color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
                    Button(onClick = viewModel::retryLoad) { Text("Reintentar") }
                }

                else -> Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = state.name,
                        onValueChange = viewModel::onNameChange,
                        label = { Text("Nombre") },
                        isError = state.nameError != null,
                        supportingText = state.nameError?.let { { Text(it) } },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = state.dosage,
                        onValueChange = viewModel::onDosageChange,
                        label = { Text("Dosis (ej. 500 mg)") },
                        isError = state.dosageError != null,
                        supportingText = state.dosageError?.let { { Text(it) } },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = state.notes,
                        onValueChange = viewModel::onNotesChange,
                        label = { Text("Notas (opcional)") },
                        isError = state.notesError != null,
                        supportingText = state.notesError?.let { { Text(it) } },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = state.startDate,
                        onValueChange = viewModel::onStartDateChange,
                        label = { Text("Fecha de inicio (AAAA-MM-DD)") },
                        isError = state.startDateError != null,
                        supportingText = state.startDateError?.let { { Text(it) } },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = state.endDate,
                        onValueChange = viewModel::onEndDateChange,
                        label = { Text("Fecha de fin (opcional)") },
                        isError = state.endDateError != null,
                        supportingText = state.endDateError?.let { { Text(it) } },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Activo")
                        Switch(checked = state.active, onCheckedChange = viewModel::onActiveChange)
                    }

                    state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

                    Button(
                        onClick = viewModel::onSaveClick,
                        enabled = !state.isSaving,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (state.isSaving) CircularProgressIndicator(Modifier.padding(2.dp)) else Text("Guardar")
                    }
                }
            }
        }
    }
}
