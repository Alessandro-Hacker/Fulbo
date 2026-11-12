package pe.fulbo.app.feature.canchas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.fulbo.app.core.util.AppError
import pe.fulbo.app.core.util.Resultado
import pe.fulbo.app.feature.canchas.data.CanchasRepository
import pe.fulbo.app.feature.canchas.domain.Cancha

enum class FiltroCancha(val etiqueta: String) {
    ABIERTO_AHORA("Abierto ahora"), FUTBOL_7("Fútbol 7"), SINTETICO("Sintético"), TECHADA("Techada")
}

/** Todo lo que la pantalla necesita para dibujarse, en un solo objeto inmutable. */
data class CanchasUiState(
    val cargando: Boolean = true,
    val canchas: List<Cancha> = emptyList(),
    val busqueda: String = "",
    val filtros: Set<FiltroCancha> = setOf(FiltroCancha.ABIERTO_AHORA),
    val error: AppError? = null,
)

/**
 * PATRÓN DE REFERENCIA para todos los features:
 *  - El ViewModel expone un único StateFlow<UiState>.
 *  - La UI llama funciones públicas (eventos) y nunca modifica el estado directamente.
 *  - Los datos vienen de un Repository inyectado por Koin.
 */
class CanchasViewModel(
    private val repo: CanchasRepository,
) : ViewModel() {

    private val local = MutableStateFlow(CanchasUiState())

    val uiState: StateFlow<CanchasUiState> =
        combine(local, repo.observarCanchas()) { s, canchas ->
            s.copy(canchas = canchas.filter { cumpleFiltros(it, s.filtros) })
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CanchasUiState())

    init { refrescar() }

    fun onBusquedaChange(texto: String) {
        local.update { it.copy(busqueda = texto) }
        refrescar()
    }

    fun onFiltroClick(filtro: FiltroCancha) = local.update {
        it.copy(filtros = if (filtro in it.filtros) it.filtros - filtro else it.filtros + filtro)
    }

    fun refrescar() = viewModelScope.launch {
        local.update { it.copy(cargando = true, error = null) }
        // TODO(M2 ubicación): usar la ubicación real del usuario. Por ahora: Miraflores, Lima.
        val result = repo.refrescar(lat = -12.1211, lng = -77.0297, texto = local.value.busqueda)
        local.update {
            it.copy(cargando = false, error = (result as? Resultado.Fallo)?.error)
        }
    }

    private fun cumpleFiltros(c: Cancha, filtros: Set<FiltroCancha>) = filtros.all { f ->
        when (f) {
            FiltroCancha.ABIERTO_AHORA -> true // TODO: depende del horario real de la cancha
            FiltroCancha.FUTBOL_7 -> c.formato == "7v7"
            FiltroCancha.SINTETICO -> c.superficie.equals("Sintético", true)
            FiltroCancha.TECHADA -> c.techada
        }
    }
}
