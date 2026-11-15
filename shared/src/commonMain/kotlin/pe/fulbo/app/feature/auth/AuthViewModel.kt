package pe.fulbo.app.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pe.fulbo.app.core.model.DeportePerfilConfig
import pe.fulbo.app.core.util.AppError
import pe.fulbo.app.core.util.Resultado
import pe.fulbo.app.feature.auth.domain.AuthRepository
import pe.fulbo.app.feature.auth.domain.NuevoPerfil
import pe.fulbo.app.feature.auth.domain.PuntoDeEntrada
import pe.fulbo.app.feature.auth.domain.RegistroValidator

/** Estado de las pantallas de registro, login y perfil. `error` ya viene listo para mostrar. */
data class AuthUiState(
    val cargando: Boolean = false,
    val error: String? = null,
)

/** Datos del paso 1 ("Crea tu perfil") que esperan al paso 2 ("Tus deportes"). */
private data class BorradorPerfil(val nombre: String, val apellido: String, val alias: String)

/**
 * Un solo ViewModel para todo el flujo de entrada (Registro → Crea tu perfil → Tus deportes → app,
 * y también Login). Se comparte en el NavHost porque los pasos del perfil se acumulan.
 */
class AuthViewModel(
    private val repo: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    /** null mientras se averigua si hay sesión y perfil (la app muestra un loader). */
    private val _inicio = MutableStateFlow<PuntoDeEntrada?>(null)
    val inicio: StateFlow<PuntoDeEntrada?> = _inicio.asStateFlow()

    private var borrador: BorradorPerfil? = null

    init {
        resolverInicio()
    }

    private fun resolverInicio() {
        viewModelScope.launch {
            _inicio.value = when (val r = repo.puntoDeEntrada()) {
                is Resultado.Exito -> r.datos
                // Sin conexión: si había sesión, dejamos pasar; si no, bienvenida.
                is Resultado.Fallo ->
                    if (repo.uidActual != null) PuntoDeEntrada.CON_PERFIL else PuntoDeEntrada.SIN_SESION
            }
        }
    }

    fun limpiarError() = _uiState.update { it.copy(error = null) }

    fun registrar(correo: String, password: String, mayorDe16: Boolean, onListo: () -> Unit) {
        val problema = RegistroValidator.validarCorreo(correo)
            ?: RegistroValidator.validarPassword(password)
            ?: if (!mayorDe16) "Debes confirmar que tienes ${RegistroValidator.EDAD_MINIMA} años o más." else null
        if (problema != null) return mostrarError(problema)

        ejecutar(onExito = { onListo() }) { repo.registrar(correo, password) }
    }

    fun iniciarSesion(correo: String, password: String, onListo: (PuntoDeEntrada) -> Unit) {
        val problema = RegistroValidator.validarCorreo(correo)
            ?: if (password.isBlank()) "Ingresa tu contraseña" else null
        if (problema != null) return mostrarError(problema)

        viewModelScope.launch {
            _uiState.value = AuthUiState(cargando = true)
            when (val r = repo.iniciarSesion(correo, password)) {
                is Resultado.Fallo -> mostrarError(r.error.mensaje())
                is Resultado.Exito -> when (val destino = repo.puntoDeEntrada()) {
                    is Resultado.Fallo -> mostrarError(destino.error.mensaje())
                    is Resultado.Exito -> {
                        _uiState.value = AuthUiState()
                        onListo(destino.datos)
                    }
                }
            }
        }
    }

    /** Paso 1: guarda nombre, apellido y alias en memoria hasta terminar el paso 2. */
    fun guardarBorrador(nombre: String, apellido: String, alias: String, onListo: () -> Unit) {
        val problema = RegistroValidator.validarNombre(nombre, "nombre")
            ?: RegistroValidator.validarNombre(apellido, "apellido")
            ?: if (alias.trim().length > MAX_ALIAS) "El alias puede tener hasta $MAX_ALIAS letras" else null
        if (problema != null) return mostrarError(problema)

        borrador = BorradorPerfil(nombre, apellido, alias)
        limpiarError()
        onListo()
    }

    /** Paso 2: crea `usuarios/{uid}` en Firestore. */
    fun crearPerfil(deportes: List<DeportePerfilConfig>, onListo: () -> Unit) {
        val datos = borrador ?: return mostrarError("Falta completar el paso 1.")
        val perfil = NuevoPerfil(datos.nombre, datos.apellido, datos.alias, deportes)
        ejecutar(onExito = { borrador = null; onListo() }) { repo.crearPerfil(perfil) }
    }

    fun cerrarSesion(onListo: () -> Unit) {
        viewModelScope.launch {
            repo.cerrarSesion()
            borrador = null
            _uiState.value = AuthUiState()
            onListo()
        }
    }

    private fun ejecutar(onExito: () -> Unit, llamada: suspend () -> Resultado<Unit>) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(cargando = true)
            when (val r = llamada()) {
                is Resultado.Exito -> {
                    _uiState.value = AuthUiState()
                    onExito()
                }
                is Resultado.Fallo -> mostrarError(r.error.mensaje())
            }
        }
    }

    private fun mostrarError(mensaje: String) {
        _uiState.value = AuthUiState(cargando = false, error = mensaje)
    }

    private companion object {
        const val MAX_ALIAS = 30
    }
}

private fun AppError.mensaje(): String = when (this) {
    AppError.SinConexion -> "Sin conexión a internet. Revisa tu red."
    AppError.NoAutorizado -> "Tu sesión venció. Inicia sesión de nuevo."
    is AppError.Servidor -> mensaje ?: "No se pudo completar la operación."
    is AppError.Desconocido -> "Ocurrió un error inesperado."
}
