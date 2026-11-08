package com.medialarma.app.data.repository

import com.medialarma.app.data.mapper.toDomain
import com.medialarma.app.data.mapper.toRequestDto
import com.medialarma.app.data.remote.ApiErrorParser
import com.medialarma.app.data.remote.MediAlarmaApi
import com.medialarma.app.domain.model.AppException
import com.medialarma.app.domain.model.Medication
import com.medialarma.app.domain.repository.MedicationRepository

class MedicationRepositoryImpl(
    private val api: MediAlarmaApi,
    private val errors: ApiErrorParser
) : MedicationRepository {

    override suspend fun getMedications(): Result<List<Medication>> = errors.call {
        val all = mutableListOf<Medication>()
        var cursor: String? = null
        do {
            val page = api.getMedications(PAGE_SIZE, cursor)
            all.addAll(page.items.map { it.toDomain() })
            cursor = page.nextCursor
        } while (cursor != null)
        all.sortedBy { it.name.lowercase() }
    }

    override suspend fun getMedication(id: String): Result<Medication> =
        errors.call { api.getMedication(id).toDomain() }

    override suspend fun createMedication(medication: Medication): Result<Medication> =
        errors.call { api.createMedication(medication.toRequestDto()).toDomain() }

    override suspend fun updateMedication(medication: Medication): Result<Medication> =
        errors.call { api.updateMedication(medication.id, medication.toRequestDto()).toDomain() }

    override suspend fun deleteMedication(id: String): Result<Unit> {
        val result = errors.call { api.deleteMedication(id) }
        // Si ya no existe en el servidor, el resultado deseado ya se cumplió
        val error = result.exceptionOrNull()
        return if (error is AppException && error.code == "NOT_FOUND") Result.success(Unit) else result
    }

    private companion object {
        const val PAGE_SIZE = 100
    }
}
