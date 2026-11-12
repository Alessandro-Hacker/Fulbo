package pe.fulbo.app.feature.auth.domain

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.todayIn
import kotlinx.datetime.yearsUntil
import kotlin.time.Clock

/**
 * Reglas del registro (issue M1 "Validaciones de registro").
 * Las Cloud Functions repiten estas validaciones; la app solo las usa para avisar rápido al usuario.
 */
object RegistroValidator {

    const val EDAD_MINIMA = 16

    private val EMAIL = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    private val NOMBRE = Regex("^[\\p{L} '-]{2,50}$")

    fun validarDni(dni: String): String? = when {
        dni.isBlank() -> "Ingresa tu DNI"
        !dni.all(Char::isDigit) -> "El DNI solo debe tener números"
        dni.length != 8 -> "El DNI debe tener 8 dígitos"
        else -> null
    }

    fun validarNombre(valor: String, campo: String = "nombre"): String? = when {
        valor.isBlank() -> "Ingresa tu $campo"
        !NOMBRE.matches(valor.trim()) -> "El $campo no es válido"
        else -> null
    }

    fun validarCorreo(correo: String): String? = when {
        correo.isBlank() -> "Ingresa tu correo"
        !EMAIL.matches(correo.trim()) -> "El correo no es válido"
        else -> null
    }

    fun validarPassword(password: String): String? = when {
        password.length < 8 -> "La contraseña debe tener al menos 8 caracteres"
        password.none(Char::isDigit) || password.none(Char::isLetter) -> "Usa letras y números"
        else -> null
    }

    fun hoy(): LocalDate = Clock.System.todayIn(TimeZone.currentSystemDefault())

    fun edad(nacimiento: LocalDate, hoy: LocalDate = hoy()): Int = nacimiento.yearsUntil(hoy)

    fun validarEdad(nacimiento: LocalDate?, hoy: LocalDate = hoy()): String? = when {
        nacimiento == null -> "Ingresa tu fecha de nacimiento"
        nacimiento > hoy -> "La fecha no es válida"
        edad(nacimiento, hoy) < EDAD_MINIMA -> "Debes tener al menos $EDAD_MINIMA años para registrarte"
        else -> null
    }
}
