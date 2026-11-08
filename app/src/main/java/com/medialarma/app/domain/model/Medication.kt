package com.medialarma.app.domain.model

data class Medication(
    val id: String,
    val name: String,
    val dosage: String,
    val notes: String?,
    val startDate: String,   // yyyy-MM-dd
    val endDate: String?,    // yyyy-MM-dd
    val active: Boolean
)
