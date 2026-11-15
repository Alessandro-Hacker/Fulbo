package pe.fulbo.app.feature.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import pe.fulbo.app.core.util.AppError
import pe.fulbo.app.core.util.Resultado
import pe.fulbo.app.feature.perfil.domain.PerfilRepository
import pe.fulbo.app.feature.perfil.domain.PerfilUsuario

data class PerfilUiState(
    val cargando: Boolean = true,
    val perfil: PerfilUsuario? = null,
    val error: String? = null,
)

class PerfilViewModel(
    private val repo: PerfilRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PerfilUiState())
    val uiState: StateFlow<PerfilUiState> = _uiState.asStateFlow()

    init {
        cargar()
    }

    fun cargar() {
        viewModelScope.launch {
            _uiState.value = PerfilUiState(cargando = true)
            _uiState.value = when (val r = repo.miPerfil()) {
                is Resultado.Exito -> PerfilUiState(cargando = false, perfil = r.datos)
                is Resultado.Fallo -> PerfilUiState(cargando = false, error = r.error.texto())
            }
        }
    }
}

private fun AppError.texto(): String = when (this) {
    AppError.SinConexion -> "Sin conexión a internet."
    AppError.NoAutorizado -> "Tu sesión venció. Inicia sesión de nuevo."
    is AppError.Servidor -> mensaje ?: "No se pudo cargar tu perfil."
    is AppError.Desconocido -> "No se pudo cargar tu perfil."
}
