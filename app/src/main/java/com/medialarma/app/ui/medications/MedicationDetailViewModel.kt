package com.medialarma.app.ui.medications

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medialarma.app.domain.model.Medication
import com.medialarma.app.domain.repository.MedicationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MedicationDetailUiState(
    val medication: Medication? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val showDeleteConfirm: Boolean = false,
    val isDeleting: Boolean = false,
    val deleted: Boolean = false
)

class MedicationDetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: MedicationRepository
) : ViewModel() {

    private val medicationId: String = checkNotNull(savedStateHandle.get<String>("id"))

    private val _uiState = MutableStateFlow(MedicationDetailUiState())
    val uiState: StateFlow<MedicationDetailUiState> = _uiState.asStateFlow()

    /** Se llama al abrir y al volver de editar. */
    fun load() {
        viewModelScope.launch {
            if (_uiState.value.medication == null) _uiState.update { it.copy(isLoading = true, error = null) }
            repository.getMedication(medicationId)
                .onSuccess { m -> _uiState.update { it.copy(medication = m, isLoading = false, error = null) } }
                .onFailure { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
        }
    }

    fun onDeleteClick() {
        _uiState.update { it.copy(showDeleteConfirm = true) }
    }

    fun onDeleteDismiss() {
        _uiState.update { it.copy(showDeleteConfirm = false) }
    }

    fun onDeleteConfirm() {
        if (_uiState.value.isDeleting) return
        _uiState.update { it.copy(showDeleteConfirm = false, isDeleting = true, error = null) }
        viewModelScope.launch {
            repository.deleteMedication(medicationId)
                .onSuccess { _uiState.update { it.copy(isDeleting = false, deleted = true) } }
                .onFailure { e -> _uiState.update { it.copy(isDeleting = false, error = e.message) } }
        }
    }
}
