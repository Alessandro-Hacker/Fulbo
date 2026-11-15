package pe.fulbo.app.feature.auth.domain

import pe.fulbo.app.core.model.DeportePerfilConfig
import pe.fulbo.app.core.util.Resultado

/** Lo que el usuario llena en "Crea tu perfil" y "Tus deportes". */
data class NuevoPerfil(
    val nombre: String,
    val apellido: String,
    val alias: String,
    val deportes: List<DeportePerfilConfig>,
)

/** Dónde debe abrir la app según la sesión y el perfil. */
enum class PuntoDeEntrada { SIN_SESION, SIN_PERFIL, CON_PERFIL }

/**
 * Contrato que usan los ViewModels. Nunca habla con la UI ni lanza excepciones:
 * devuelve [Resultado] con mensajes de error ya traducidos.
 */
interface AuthRepository {
    /** uid de la sesión activa, o null si no hay sesión. */
    val uidActual: String?

    /** Crea el usuario en Firebase Auth (correo y contraseña). Deja la sesión iniciada. */
    suspend fun registrar(correo: String, password: String): Resultado<Unit>

    suspend fun iniciarSesion(correo: String, password: String): Resultado<Unit>

    /** Crea `usuarios/{uid}` en Firestore. Falla si no hay sesión. */
    suspend fun crearPerfil(perfil: NuevoPerfil): Resultado<Unit>

    /** Mira la sesión y si existe `usuarios/{uid}` para decidir la pantalla inicial. */
    suspend fun puntoDeEntrada(): Resultado<PuntoDeEntrada>

    suspend fun cerrarSesion()
}
