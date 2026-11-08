package com.medialarma.app.data.mapper

import com.medialarma.app.data.remote.dto.MedicationDto
import com.medialarma.app.data.remote.dto.MedicationRequestDto
import com.medialarma.app.domain.model.Medication

fun MedicationDto.toDomain() = Medication(
    id = id,
    name = name,
    dosage = dosage,
    notes = notes,
    startDate = startDate,
    endDate = endDate,
    active = active
)

fun Medication.toRequestDto() = MedicationRequestDto(
    id = id,
    name = name.trim(),
    dosage = dosage.trim(),
    notes = notes?.trim()?.takeIf { it.isNotEmpty() },
    startDate = startDate.trim(),
    endDate = endDate?.trim()?.takeIf { it.isNotEmpty() },
    active = active
)
