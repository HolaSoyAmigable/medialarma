package com.medialarma.app.ui.medications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medialarma.app.domain.model.Medication
import com.medialarma.app.domain.repository.MedicationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface MedicationListUiState {
    data object Loading : MedicationListUiState
    data class Success(val medications: List<Medication>) : MedicationListUiState
    data class Error(val message: String) : MedicationListUiState
}

class MedicationListViewModel(private val repository: MedicationRepository) : ViewModel() {
    private val _uiState = MutableStateFlow<MedicationListUiState>(MedicationListUiState.Loading)
    val uiState: StateFlow<MedicationListUiState> = _uiState.asStateFlow()

    /** Se llama al abrir la pantalla, al volver a ella y al pulsar "Reintentar". */
    fun load() {
        viewModelScope.launch {
            // Con datos ya mostrados se refresca sin parpadeo; tras un error se vuelve a mostrar la carga
            if (_uiState.value is MedicationListUiState.Error) {
                _uiState.value = MedicationListUiState.Loading
            }
            repository.getMedications()
                .onSuccess { _uiState.value = MedicationListUiState.Success(it) }
                .onFailure { _uiState.value = MedicationListUiState.Error(it.message ?: "Error desconocido") }
        }
    }
}
