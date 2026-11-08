package com.medialarma.app.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class MedicationDto(
    val id: String,
    val name: String,
    val dosage: String,
    val notes: String? = null,
    val startDate: String,
    val endDate: String? = null,
    val active: Boolean,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val deletedAt: String? = null
)

/** Cuerpo de POST y PUT /medications. */
@Serializable
data class MedicationRequestDto(
    val id: String,
    val name: String,
    val dosage: String,
    val notes: String? = null,
    val startDate: String,
    val endDate: String? = null,
    val active: Boolean
)

@Serializable
data class MedicationPageDto(
    val items: List<MedicationDto>,
    val nextCursor: String? = null
)
