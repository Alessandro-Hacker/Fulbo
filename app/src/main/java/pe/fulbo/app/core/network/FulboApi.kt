package pe.fulbo.app.core.network

import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Contrato con el backend. Agregar endpoints aquí según el documento OpenAPI (issue M0 "Diseñar modelo de datos").
 * Cuando crezca, dividir por feature: AuthApi, CanchasApi, PartidosApi…
 */
interface FulboApi {

    @GET("canchas")
    suspend fun buscarCanchas(
        @Query("lat") lat: Double,
        @Query("lng") lng: Double,
        @Query("radioKm") radioKm: Double = 5.0,
        @Query("q") texto: String? = null,
    ): List<CanchaDto>
}

@Serializable
data class CanchaDto(
    val id: String,
    val nombre: String,
    val distrito: String,
    val superficie: String,
    val formato: String,
    val techada: Boolean,
    val rating: Double,
    val precioHora: Double,
    val distanciaMetros: Int,
    val proximoLibre: String? = null,
    val lat: Double,
    val lng: Double,
)
