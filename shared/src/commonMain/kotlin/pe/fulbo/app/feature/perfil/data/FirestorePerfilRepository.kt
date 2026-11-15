package pe.fulbo.app.feature.perfil.data

import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.firestore.FirebaseFirestore
import kotlinx.serialization.Serializable
import pe.fulbo.app.core.firebase.Colecciones
import pe.fulbo.app.core.model.DeporteCasual
import pe.fulbo.app.core.util.AppError
import pe.fulbo.app.core.util.Resultado
import pe.fulbo.app.feature.auth.data.DeporteDto
import pe.fulbo.app.feature.auth.data.traducirError
import pe.fulbo.app.feature.perfil.domain.PerfilRepository
import pe.fulbo.app.feature.perfil.domain.PerfilUsuario
import kotlin.coroutines.cancellation.CancellationException

/** Solo los campos que la pantalla lee; el resto del documento se ignora. */
@Serializable
private data class PerfilLecturaDto(
    val nombre: String = "",
    val apellido: String = "",
    val alias: String = "",
    val deportes: Map<String, DeporteDto> = emptyMap(),
    val rep_global: Double? = null,
    val rep_puntualidad: Double? = null,
    val rep_asistencia: Double? = null,
    val rep_pago: Double? = null,
    val rep_trato: Double? = null,
    val num_calificaciones: Int = 0,
    val partidos_jugados: Int = 0,
    val faltas: Int = 0,
)

class FirestorePerfilRepository(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : PerfilRepository {

    override suspend fun miPerfil(): Resultado<PerfilUsuario> {
        val uid = auth.currentUser?.uid ?: return Resultado.Fallo(AppError.NoAutorizado)
        return try {
            val doc = firestore.collection(Colecciones.USUARIOS).document(uid).get()
            if (!doc.exists) return Resultado.Fallo(AppError.Servidor("sin_perfil", "Aún no tienes perfil."))
            Resultado.Exito(doc.data<PerfilLecturaDto>().aDominio())
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Resultado.Fallo(traducirError(e))
        }
    }
}

private fun PerfilLecturaDto.aDominio(): PerfilUsuario {
    // Deporte principal: el primero (en el orden de la app) que tenga posiciones elegidas.
    val principal = DeporteCasual.entries.firstOrNull { deportes[it.id]?.posiciones?.isNotEmpty() == true }
        ?: DeporteCasual.entries.firstOrNull { deportes.containsKey(it.id) }
    val config = principal?.let { deportes[it.id] }
    return PerfilUsuario(
        nombre = nombre,
        apellido = apellido,
        alias = alias,
        deportePrincipal = principal,
        posicion = config?.posiciones?.firstOrNull(),
        nivel = config?.nivel,
        reputacion = rep_global,
        repPuntualidad = rep_puntualidad,
        repAsistencia = rep_asistencia,
        repPago = rep_pago,
        repTrato = rep_trato,
        numCalificaciones = num_calificaciones,
        partidosJugados = partidos_jugados,
        faltas = faltas,
    )
}
