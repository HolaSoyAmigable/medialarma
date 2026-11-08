package com.medialarma.app.ui.medications

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.medialarma.app.domain.model.AppException
import com.medialarma.app.domain.model.Medication
import com.medialarma.app.domain.repository.MedicationRepository
import com.medialarma.app.domain.validation.Validators
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class MedicationFormUiState(
    val isEditing: Boolean = false,
    val name: String = "",
    val dosage: String = "",
    val notes: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val active: Boolean = true,
    val nameError: String? = null,
    val dosageError: String? = null,
    val notesError: String? = null,
    val startDateError: String? = null,
    val endDateError: String? = null,
    val isLoading: Boolean = false,
    val loadError: String? = null,
    val isSaving: Boolean = false,
    val error: String? = null,
    val saved: Boolean = false
)

/** Un solo ViewModel para crear (sin id) y editar (con id). */
class MedicationFormViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: MedicationRepository
) : ViewModel() {

    private val editingId: String? = savedStateHandle.get<String>("id")

    // El id lo genera la app; si el guardado se repite, la API no duplica (POST idempotente)
    private val newId: String = UUID.randomUUID().toString()

    private val _uiState = MutableStateFlow(
        MedicationFormUiState(
            isEditing = editingId != null,
            isLoading = editingId != null,
            startDate = if (editingId == null) today() else ""
        )
    )
    val uiState: StateFlow<MedicationFormUiState> = _uiState.asStateFlow()

    init {
        if (editingId != null) loadExisting(editingId)
    }

    fun retryLoad() {
        editingId?.let {
            _uiState.update { s -> s.copy(isLoading = true, loadError = null) }
            loadExisting(it)
        }
    }

    private fun loadExisting(id: String) {
        viewModelScope.launch {
            repository.getMedication(id)
                .onSuccess { m ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            name = m.name,
                            dosage = m.dosage,
                            notes = m.notes.orEmpty(),
                            startDate = m.startDate,
                            endDate = m.endDate.orEmpty(),
                            active = m.active
                        )
                    }
                }
                .onFailure { e -> _uiState.update { it.copy(isLoading = false, loadError = e.message) } }
        }
    }

    fun onNameChange(v: String) { _uiState.update { it.copy(name = v, nameError = null, error = null) } }
    fun onDosageChange(v: String) { _uiState.update { it.copy(dosage = v, dosageError = null, error = null) } }
    fun onNotesChange(v: String) { _uiState.update { it.copy(notes = v, notesError = null, error = null) } }
    fun onStartDateChange(v: String) { _uiState.update { it.copy(startDate = v, startDateError = null, error = null) } }
    fun onEndDateChange(v: String) { _uiState.update { it.copy(endDate = v, endDateError = null, error = null) } }
    fun onActiveChange(v: Boolean) { _uiState.update { it.copy(active = v) } }

    fun onSaveClick() {
        val s = _uiState.value
        if (s.isSaving) return

        val nameError = if (s.name.trim().length !in 1..100) "Escribe el nombre (máx. 100 caracteres)" else null
        val dosageError = if (s.dosage.trim().length !in 1..50) "Escribe la dosis (máx. 50 caracteres)" else null
        val notesError = if (s.notes.trim().length > 500) "Máximo 500 caracteres" else null
        val startValid = Validators.isValidDate(s.startDate)
        val startDateError = if (!startValid) "Usa el formato AAAA-MM-DD" else null
        val endDateError = when {
            s.endDate.isBlank() -> null
            !Validators.isValidDate(s.endDate) -> "Usa el formato AAAA-MM-DD"
            startValid && s.endDate.trim() < s.startDate.trim() -> "No puede ser anterior al inicio"
            else -> null
        }
        if (listOf(nameError, dosageError, notesError, startDateError, endDateError).any { it != null }) {
            _uiState.update {
                it.copy(
                    nameError = nameError, dosageError = dosageError, notesError = notesError,
                    startDateError = startDateError, endDateError = endDateError
                )
            }
            return
        }

        _uiState.update { it.copy(isSaving = true, error = null) }
        val medication = Medication(
            id = editingId ?: newId,
            name = s.name,
            dosage = s.dosage,
            notes = s.notes,
            startDate = s.startDate,
            endDate = s.endDate,
            active = s.active
        )
        viewModelScope.launch {
            val result = if (editingId == null) {
                repository.createMedication(medication)
            } else {
                repository.updateMedication(medication)
            }
            result
                .onSuccess { _uiState.update { it.copy(isSaving = false, saved = true) } }
                .onFailure { e ->
                    // Si la API devuelve errores por campo, se muestran junto a cada campo
                    val fields = (e as? AppException)?.fieldErrors.orEmpty()
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            error = e.message,
                            nameError = fields["name"],
                            dosageError = fields["dosage"],
                            notesError = fields["notes"],
                            startDateError = fields["startDate"],
                            endDateError = fields["endDate"]
                        )
                    }
                }
        }
    }

    private fun today(): String = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
}
