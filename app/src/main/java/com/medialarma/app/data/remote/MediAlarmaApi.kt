package com.medialarma.app.data.remote

import com.medialarma.app.data.remote.dto.AuthResponseDto
import com.medialarma.app.data.remote.dto.LoginRequestDto
import com.medialarma.app.data.remote.dto.LogoutRequestDto
import com.medialarma.app.data.remote.dto.MedicationDto
import com.medialarma.app.data.remote.dto.MedicationPageDto
import com.medialarma.app.data.remote.dto.MedicationRequestDto
import com.medialarma.app.data.remote.dto.RegisterRequestDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface MediAlarmaApi {
    @POST("auth/register")
    suspend fun register(@Body body: RegisterRequestDto): AuthResponseDto

    @POST("auth/login")
    suspend fun login(@Body body: LoginRequestDto): AuthResponseDto

    @POST("auth/logout")
    suspend fun logout(@Body body: LogoutRequestDto)

    @GET("medications")
    suspend fun getMedications(
        @Query("limit") limit: Int,
        @Query("cursor") cursor: String?
    ): MedicationPageDto

    @POST("medications")
    suspend fun createMedication(@Body body: MedicationRequestDto): MedicationDto

    @GET("medications/{id}")
    suspend fun getMedication(@Path("id") id: String): MedicationDto

    @PUT("medications/{id}")
    suspend fun updateMedication(@Path("id") id: String, @Body body: MedicationRequestDto): MedicationDto

    @DELETE("medications/{id}")
    suspend fun deleteMedication(@Path("id") id: String)
}
