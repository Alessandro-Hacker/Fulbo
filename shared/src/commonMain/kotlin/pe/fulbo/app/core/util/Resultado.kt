package pe.fulbo.app.core.util

import kotlin.coroutines.cancellation.CancellationException

/** Resultado uniforme de cualquier operación. Los repositorios devuelven esto, nunca excepciones. */
sealed interface Resultado<out T> {
    data class Exito<T>(val datos: T) : Resultado<T>
    data class Fallo(val error: AppError) : Resultado<Nothing>
}

sealed interface AppError {
    data object SinConexion : AppError
    data object NoAutorizado : AppError          // sesión vencida → mandar a login
    data class Servidor(val codigo: String?, val mensaje: String?) : AppError
    data class Desconocido(val causa: Throwable) : AppError
}

/**
 * Envuelve una llamada (Firebase, Cloud Functions…) y traduce los errores.
 * TODO(firebase): mapear FirebaseNetworkException → SinConexion y los códigos de
 * FirebaseFunctionsException → Servidor cuando se conecten los repositorios reales.
 */
suspend fun <T> llamadaSegura(bloque: suspend () -> T): Resultado<T> = try {
    Resultado.Exito(bloque())
} catch (e: CancellationException) {
    throw e
} catch (e: Exception) {
    Resultado.Fallo(AppError.Desconocido(e))
}
