package pe.fulbo.app.core.firebase

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.functions.functions

/**
 * Lo que cada plataforma le dice al código común al arrancar, para que este no sepa en qué
 * teléfono corre (Android lo lee de BuildConfig; iOS, de la configuración de Xcode).
 *
 * @param usarEmuladores true solo en la variante dev cuando corres `firebase emulators:start`.
 * @param emuladorHost "10.0.2.2" en el emulador de Android · "localhost" en el simulador de iOS.
 */
data class ConfigApp(
    val usarEmuladores: Boolean = false,
    val emuladorHost: String = "localhost",
)

/**
 * Apunta Auth, Firestore y Functions a los emuladores locales (puertos de firebase/firebase.json).
 * Debe llamarse una sola vez, antes de la primera consulta.
 */
fun conectarEmuladores(host: String) {
    Firebase.auth.useEmulator(host, 9099)
    Firebase.firestore.useEmulator(host, 8080)
    Firebase.functions(Funciones.REGION).useEmulator(host, 5001)
}
