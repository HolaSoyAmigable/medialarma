package com.medialarma.app.data.remote

import com.medialarma.app.data.remote.dto.ErrorEnvelopeDto
import com.medialarma.app.domain.model.AppException
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import java.io.IOException

/** Convierte errores de red/HTTP en AppException con mensaje en español. */
class ApiErrorParser(private val json: Json) {

    suspend fun <T> call(block: suspend () -> T): Result<T> = try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: HttpException) {
        Result.failure(parse(e))
    } catch (e: IOException) {
        Result.failure(AppException("NETWORK", "No se pudo conectar con el servidor. Revisa tu conexión."))
    }

    private fun parse(e: HttpException): AppException {
        val raw = e.response()?.errorBody()?.string()
        val error = raw?.let { runCatching { json.decodeFromString<ErrorEnvelopeDto>(it).error }.getOrNull() }
        return if (error != null) {
            AppException(error.code, error.message, error.details.orEmpty().associate { it.field to it.issue })
        } else {
            AppException("HTTP_${e.code()}", "Error del servidor (${e.code()}).")
        }
    }
}
