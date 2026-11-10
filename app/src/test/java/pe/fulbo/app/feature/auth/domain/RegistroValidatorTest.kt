package pe.fulbo.app.feature.auth.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate

class RegistroValidatorTest {

    private val hoy = LocalDate.of(2026, 11, 9)

    @Test fun `dni valido de 8 digitos`() = assertNull(RegistroValidator.validarDni("45678912"))
    @Test fun `dni con letras es invalido`() = assertNotNull(RegistroValidator.validarDni("4567891A"))
    @Test fun `dni corto es invalido`() = assertNotNull(RegistroValidator.validarDni("1234567"))
    @Test fun `dni vacio es invalido`() = assertNotNull(RegistroValidator.validarDni(""))

    @Test fun `cumple 16 hoy puede registrarse`() =
        assertNull(RegistroValidator.validarEdad(LocalDate.of(2010, 11, 9), hoy))

    @Test fun `le falta un dia para 16 no puede registrarse`() =
        assertNotNull(RegistroValidator.validarEdad(LocalDate.of(2010, 11, 10), hoy))

    @Test fun `fecha futura es invalida`() =
        assertNotNull(RegistroValidator.validarEdad(LocalDate.of(2030, 1, 1), hoy))

    @Test fun `calcula edad correctamente`() =
        assertEquals(25, RegistroValidator.edad(LocalDate.of(2001, 5, 20), hoy))

    @Test fun `correo valido`() = assertNull(RegistroValidator.validarCorreo("frank@correo.pe"))
    @Test fun `correo sin arroba es invalido`() = assertNotNull(RegistroValidator.validarCorreo("frank.correo.pe"))

    @Test fun `nombre con tildes y enie es valido`() = assertNull(RegistroValidator.validarNombre("José Ñúñez"))

    @Test fun `password sin numeros es invalida`() = assertNotNull(RegistroValidator.validarPassword("solamenteletras"))
    @Test fun `password valida`() = assertNull(RegistroValidator.validarPassword("fulbo2026"))
}
