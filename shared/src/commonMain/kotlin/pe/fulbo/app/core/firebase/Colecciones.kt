package pe.fulbo.app.core.firebase

/** Nombres de colecciones de Firestore (deben coincidir con firebase/firestore.rules). */
object Colecciones {
    const val USUARIOS = "usuarios"
    const val PRIVADO = "privado"            // usuarios/{uid}/privado/datos
    const val DISPOSITIVOS = "dispositivos"  // usuarios/{uid}/dispositivos/{token}
    const val DUENOS_CANCHA = "duenos_cancha"
    const val CANCHAS = "canchas"
    const val PARTIDOS = "partidos"
    const val INSCRIPCIONES = "inscripciones"    // partidos/{id}/inscripciones/{uid}
    const val CALIFICACIONES = "calificaciones"  // partidos/{id}/calificaciones/{emisor_receptor}
    const val EQUIPOS = "equipos"
    const val MIEMBROS = "miembros"              // equipos/{id}/miembros/{uid}
    const val SUSCRIPCIONES = "suscripciones"
    const val REPORTES = "reportes"
}

/** Cloud Functions que llama la app (firebase/functions/src). */
object Funciones {
    /** Misma región que REGION en functions/src/comun.ts. */
    const val REGION = "southamerica-west1"

    const val COMPLETAR_PERFIL = "completarPerfil"
    const val SOLICITAR_INSCRIPCION = "solicitarInscripcion"
    const val RESOLVER_SOLICITUD = "resolverSolicitud"
    const val DARSE_DE_BAJA = "darseDeBaja"
    const val CANCELAR_PARTIDO = "cancelarPartido"
    const val FINALIZAR_PARTIDO = "finalizarPartido"
    const val BORRAR_CUENTA = "borrarCuenta"
}
