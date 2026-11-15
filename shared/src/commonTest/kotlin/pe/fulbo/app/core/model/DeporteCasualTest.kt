package pe.fulbo.app.core.model

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DeporteCasualTest {

    @Test
    fun obtenerDeportes_alConsultarEntries_retornaTresDeportesCasuales() {
        // Arrange & Act
        val deportes = DeporteCasual.entries

        // Assert
        assertEquals(3, deportes.size)
        assertTrue(deportes.any { it.id == "futbol" && it.nombre == "Fútbol" })
        assertTrue(deportes.any { it.id == "basquet" && it.nombre == "Básquet" })
        assertTrue(deportes.any { it.id == "voley" && it.nombre == "Vóley" })
    }

    @Test
    fun buscarPorId_conIdExistente_retornaDeporteCorrecto() {
        // Arrange
        val idBuscado = "basquet"

        // Act
        val resultado = DeporteCasual.de(idBuscado)

        // Assert
        assertEquals(DeporteCasual.BASQUET, resultado)
    }

    @Test
    fun buscarPorId_conIdInexistente_retornaDeportePorDefectoFutbol() {
        // Arrange
        val idInexistente = "tenis"

        // Act
        val resultado = DeporteCasual.de(idInexistente)

        // Assert
        assertEquals(DeporteCasual.FUTBOL, resultado)
    }

    @Test
    fun obtenerPosiciones_paraCadaDeporte_retornaPosicionesOficiales() {
        // Arrange & Act
        val posicionesFutbol = DeporteCasual.obtenerPosiciones(DeporteCasual.FUTBOL)
        val posicionesBasquet = DeporteCasual.obtenerPosiciones(DeporteCasual.BASQUET)
        val posicionesVoley = DeporteCasual.obtenerPosiciones(DeporteCasual.VOLEY)

        // Assert
        assertTrue(posicionesFutbol.contains("Arquero"))
        assertTrue(posicionesFutbol.contains("Delantero"))
        assertTrue(posicionesBasquet.contains("Base"))
        assertTrue(posicionesBasquet.contains("Alero"))
        assertTrue(posicionesVoley.contains("Armador"))
        assertTrue(posicionesVoley.contains("Líbero"))
    }

    @Test
    fun obtenerFormatos_paraCadaDeporte_retornaFormatosCasualesValidos() {
        // Arrange & Act
        val formatosFutbol = DeporteCasual.obtenerFormatos(DeporteCasual.FUTBOL)
        val formatosBasquet = DeporteCasual.obtenerFormatos(DeporteCasual.BASQUET)
        val formatosVoley = DeporteCasual.obtenerFormatos(DeporteCasual.VOLEY)

        // Assert
        assertTrue(formatosFutbol.contains("7 vs 7"))
        assertTrue(formatosBasquet.contains("3 vs 3"))
        assertTrue(formatosVoley.contains("6 vs 6"))
    }

    @Test
    fun rangosEquipo_alConsultarEntries_retornaCincoRangosConMetasConsecutivas() {
        // Arrange & Act
        val rangos = RangoEquipo.entries

        // Assert
        assertEquals(5, rangos.size)
        assertEquals(RangoEquipo.BRONCE, rangos[0])
        assertEquals(1, rangos[0].semanasMeta)
        assertEquals(RangoEquipo.PLATA, rangos[1])
        assertEquals(4, rangos[1].semanasMeta)
        assertEquals(RangoEquipo.ORO, rangos[2])
        assertEquals(12, rangos[2].semanasMeta)
        assertEquals(RangoEquipo.PLATINO, rangos[3])
        assertEquals(26, rangos[3].semanasMeta)
        assertEquals(RangoEquipo.LEYENDA, rangos[4])
        assertEquals(52, rangos[4].semanasMeta)
    }

    @Test
    fun buscarRangoPorId_conIdInvalido_retornaBroncePorDefecto() {
        // Arrange
        val idInvalido = "diamante"

        // Act
        val resultado = RangoEquipo.de(idInvalido)

        // Assert
        assertEquals(RangoEquipo.BRONCE, resultado)
    }
}
