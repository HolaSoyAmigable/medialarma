package com.medialarma.app.domain.repository

import com.medialarma.app.domain.model.Medication

interface MedicationRepository {
    suspend fun getMedications(): Result<List<Medication>>
    suspend fun getMedication(id: String): Result<Medication>
    suspend fun createMedication(medication: Medication): Result<Medication>
    suspend fun updateMedication(medication: Medication): Result<Medication>
    suspend fun deleteMedication(id: String): Result<Unit>
}
