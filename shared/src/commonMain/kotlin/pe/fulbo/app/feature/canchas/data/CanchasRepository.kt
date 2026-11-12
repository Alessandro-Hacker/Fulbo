package pe.fulbo.app.feature.canchas.data

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import pe.fulbo.app.core.util.Resultado
import pe.fulbo.app.feature.canchas.domain.Cancha

/**
 * Contrato que usa el ViewModel. La UI y el ViewModel nunca hablan con Firebase directamente.
 */
interface CanchasRepository {
    fun observarCanchas(): Flow<List<Cancha>>
    suspend fun refrescar(lat: Double, lng: Double, texto: String? = null): Resultado<Unit>
}

/**
 * Implementación falsa con los datos de los mockups, para avanzar la UI sin backend.
 * TODO(M2 búsqueda de canchas): crear FirestoreCanchasRepository (colección `canchas`) y
 * cambiar el binding en canchasModule.
 */
class FakeCanchasRepository : CanchasRepository {

    private val canchas = MutableStateFlow<List<Cancha>>(emptyList())

    override fun observarCanchas(): Flow<List<Cancha>> = canchas.asStateFlow()

    override suspend fun refrescar(lat: Double, lng: Double, texto: String?): Resultado<Unit> {
        delay(600) // simula red
        canchas.value = DATOS.filter {
            texto.isNullOrBlank() || it.nombre.contains(texto, true) || it.distrito.contains(texto, true)
        }
        return Resultado.Exito(Unit)
    }

    private companion object {
        val DATOS = listOf(
            Cancha("1", "La Bombonerita", "Miraflores", "Sintético", "7v7", true, 4.8, 120.0, 650, "Libre 19:00", false),
            Cancha("2", "Surquillo Pro", "Surquillo", "Sintético", "5v5", false, 4.5, 95.0, 1200, "Última hora 21:00", true),
            Cancha("3", "Estadio Chico 7", "San Isidro", "Sintético", "7v7", true, 4.6, 140.0, 2400, "Libre 20:00", false),
            Cancha("4", "Cancha El Potrero", "Barranco", "Grass natural", "11v11", false, 4.2, 110.0, 3100, "Libre 18:00", false),
        )
    }
}
