package pe.fulbo.app.core.firebase

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.functions.functions
import org.koin.dsl.module

/** Instancias de Firebase para inyectar en los repositorios (se crean al pedirlas, no al arrancar). */
val firebaseModule = module {
    single { Firebase.auth }
    single { Firebase.firestore }
    single { Firebase.functions(Funciones.REGION) }
}
