package pe.fulbo.app.feature.auth.data

import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.firestore.FirebaseFirestore
import pe.fulbo.app.core.firebase.Colecciones
import pe.fulbo.app.core.util.AppError
import pe.fulbo.app.core.util.Resultado
import pe.fulbo.app.feature.auth.domain.AuthRepository
import pe.fulbo.app.feature.auth.domain.NuevoPerfil
import pe.fulbo.app.feature.auth.domain.PuntoDeEntrada
import kotlin.coroutines.cancellation.CancellationException

/** Implementación real con Firebase Auth + Firestore (SDK de GitLive). */
class FirebaseAuthRepository(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : AuthRepository {

    override val uidActual: String? get() = auth.currentUser?.uid

    override suspend fun registrar(correo: String, password: String): Resultado<Unit> =
        llamada { auth.createUserWithEmailAndPassword(correo.trim(), password) }

    override suspend fun iniciarSesion(correo: String, password: String): Resultado<Unit> =
        llamada { auth.signInWithEmailAndPassword(correo.trim(), password) }

    override suspend fun crearPerfil(perfil: NuevoPerfil): Resultado<Unit> {
        val uid = uidActual ?: return Resultado.Fallo(AppError.NoAutorizado)
        val dto = PerfilDto(
            nombre = perfil.nombre.trim(),
            apellido = perfil.apellido.trim(),
            // Si no pone alias, se muestra el nombre.
            alias = perfil.alias.trim().ifBlank { perfil.nombre.trim() },
            deportes = perfil.deportes.associate { config ->
                config.deporte.id to DeporteDto(config.posiciones, config.nivel)
            },
            mayor_de_16 = true,
        )
        return llamada { firestore.collection(Colecciones.USUARIOS).document(uid).set(dto) }
    }

    override suspend fun puntoDeEntrada(): Resultado<PuntoDeEntrada> {
        val uid = uidActual ?: return Resultado.Exito(PuntoDeEntrada.SIN_SESION)
        return try {
            val existe = firestore.collection(Colecciones.USUARIOS).document(uid).get().exists
            Resultado.Exito(if (existe) PuntoDeEntrada.CON_PERFIL else PuntoDeEntrada.SIN_PERFIL)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resultado.Fallo(traducirError(e))
        }
    }

    override suspend fun cerrarSesion() {
        auth.signOut()
    }

    private suspend fun llamada(bloque: suspend () -> Any?): Resultado<Unit> = try {
        bloque()
        Resultado.Exito(Unit)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Resultado.Fallo(traducirError(e))
    }
}

/**
 * Traduce las excepciones de Firebase a un mensaje en español para el usuario.
 * Se compara por nombre de clase para no depender de imports distintos entre Android e iOS.
 */
internal fun traducirError(e: Exception): AppError {
    val clase = e::class.simpleName.orEmpty()
    val texto = e.message.orEmpty()
    val mensaje = when {
        clase.contains("Network") || texto.contains("network", ignoreCase = true) ->
            return AppError.SinConexion
        clase.contains("UserCollision") || texto.contains("email address is already", ignoreCase = true) ->
            "Ese correo ya tiene una cuenta. Inicia sesión."
        clase.contains("WeakPassword") -> "La contraseña es muy débil."
        clase.contains("InvalidCredentials") || clase.contains("InvalidUser") ||
            texto.contains("password is invalid", ignoreCase = true) ||
            texto.contains("no user record", ignoreCase = true) ||
            texto.contains("credential", ignoreCase = true) ->
            "Correo o contraseña incorrectos."
        texto.contains("PERMISSION_DENIED", ignoreCase = true) ||
            texto.contains("permission", ignoreCase = true) ->
            "No tienes permiso para guardar tu perfil. Avisa al equipo (reglas de Firestore)."
        texto.contains("too many", ignoreCase = true) ->
            "Demasiados intentos. Espera unos minutos."
        else -> "No se pudo completar la operación. Intenta de nuevo."
    }
    return AppError.Servidor(codigo = clase.ifBlank { null }, mensaje = mensaje)
}
