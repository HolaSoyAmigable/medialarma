package com.medialarma.app.data.remote

import com.medialarma.app.data.remote.dto.AuthResponseDto
import com.medialarma.app.data.remote.dto.LoginRequestDto
import com.medialarma.app.data.remote.dto.LogoutRequestDto
import com.medialarma.app.data.remote.dto.RegisterRequestDto
import retrofit2.http.Body
import retrofit2.http.POST

interface MediAlarmaApi {
    @POST("auth/register")
    suspend fun register(@Body body: RegisterRequestDto): AuthResponseDto

    @POST("auth/login")
    suspend fun login(@Body body: LoginRequestDto): AuthResponseDto

    @POST("auth/logout")
    suspend fun logout(@Body body: LogoutRequestDto)
}
