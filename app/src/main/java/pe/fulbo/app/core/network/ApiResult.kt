package pe.fulbo.app.core.network

import retrofit2.HttpException
import java.io.IOException

/** Resultado uniforme de cualquier llamada. Los repositorios devuelven esto, nunca excepciones. */
sealed interface ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>
    data class Error(val error: AppError) : ApiResult<Nothing>
}

sealed interface AppError {
    data object SinConexion : AppError
    data object NoAutorizado : AppError          // 401 → mandar a login
    data class Servidor(val code: Int, val mensaje: String?) : AppError
    data class Desconocido(val causa: Throwable) : AppError
}

/** Envuelve una llamada de Retrofit y traduce los errores. */
suspend fun <T> safeApiCall(block: suspend () -> T): ApiResult<T> = try {
    ApiResult.Success(block())
} catch (e: IOException) {
    ApiResult.Error(AppError.SinConexion)
} catch (e: HttpException) {
    if (e.code() == 401) ApiResult.Error(AppError.NoAutorizado)
    else ApiResult.Error(AppError.Servidor(e.code(), e.response()?.errorBody()?.string()))
} catch (e: kotlin.coroutines.cancellation.CancellationException) {
    throw e
} catch (e: Exception) {
    ApiResult.Error(AppError.Desconocido(e))
}
