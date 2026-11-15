package pe.fulbo.app.feature.perfil.domain

import pe.fulbo.app.core.model.DeporteCasual
import pe.fulbo.app.core.util.Resultado

/** Perfil del usuario con sesión, tal como lo necesita la pantalla (independiente de Firestore). */
data class PerfilUsuario(
    val nombre: String,
    val apellido: String,
    val alias: String,
    /** Deporte principal con su primera posición y nivel; null si no eligió ninguno. */
    val deportePrincipal: DeporteCasual?,
    val posicion: String?,
    val nivel: String?,
    /** null mientras no tenga calificaciones. */
    val reputacion: Double?,
    val repPuntualidad: Double?,
    val repAsistencia: Double?,
    val repPago: Double?,
    val repTrato: Double?,
    val numCalificaciones: Int,
    val partidosJugados: Int,
    val faltas: Int,
) {
    val nombreCompleto: String get() = "$nombre $apellido".trim()
    val iniciales: String
        get() = "${nombre.firstOrNull() ?: ' '}${apellido.firstOrNull() ?: ' '}".trim().uppercase()
}

interface PerfilRepository {
    /** Lee `usuarios/{uid}` de la sesión activa. */
    suspend fun miPerfil(): Resultado<PerfilUsuario>
}
