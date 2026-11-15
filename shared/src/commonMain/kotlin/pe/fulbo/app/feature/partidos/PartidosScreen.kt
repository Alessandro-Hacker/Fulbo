package pe.fulbo.app.feature.partidos

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import pe.fulbo.app.core.designsystem.component.FulboCard
import pe.fulbo.app.core.designsystem.component.FulboChip
import pe.fulbo.app.core.designsystem.component.PlayerAvatar
import pe.fulbo.app.core.designsystem.component.StatusPill
import pe.fulbo.app.core.designsystem.theme.FulboColors
import pe.fulbo.app.core.designsystem.theme.spacing
import pe.fulbo.app.core.model.DeporteCasual
import pe.fulbo.app.core.model.PartidoCasualItem

private val CIUDADES_DISPONIBLES: List<String> = listOf(
    "Piura",
    "Lima",
    "Trujillo",
    "Arequipa",
    "Chiclayo",
    "Cusco",
)

/**
 * Pantalla principal con feed de partidos casuales cercanos (Mockup página 2).
 *
 * @param onPublicarPartido Navega al formulario de publicación de partido.
 * @param modifier Modificador de diseño para la pantalla.
 */
@Composable
fun PartidosScreen(
    onPublicarPartido: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var ciudadSeleccionada by remember { mutableStateOf(CIUDADES_DISPONIBLES.first()) }
    var isDialogoCiudadesVisible by remember { mutableStateOf(false) }

    var filtroDeporte by remember { mutableStateOf<DeporteCasual?>(null) }
    var isFiltroHoyActivo by remember { mutableStateOf(false) }
    var isFiltroFaltaUnoActivo by remember { mutableStateOf(false) }
    var isFiltroGratisActivo by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val partidosIniciales = remember {
        mutableStateListOf(
            PartidoCasualItem(
                id = "1",
                deporte = DeporteCasual.FUTBOL,
                cancha = "Cancha Los Álamos",
                fechaHora = "Hoy 8:00 pm",
                formato = "7 vs 7",
                nivel = "Intermedio",
                costoPersona = 8,
                cuposFaltantes = 1,
                organizadorNombre = "Marco",
                organizadorRating = 4.8,
                organizadorIniciales = "MA",
                estadoTexto = "Falta 1 jugador",
            ),
            PartidoCasualItem(
                id = "2",
                deporte = DeporteCasual.BASQUET,
                cancha = "Losa Deportiva Miraflores",
                fechaHora = "Mañana 7:00 pm",
                formato = "3 vs 3",
                nivel = "Casual",
                costoPersona = 5,
                cuposFaltantes = 2,
                organizadorNombre = "Luis",
                organizadorRating = 4.2,
                organizadorIniciales = "LU",
                estadoTexto = "Faltan 2 jugadores",
            ),
            PartidoCasualItem(
                id = "3",
                deporte = DeporteCasual.FUTBOL,
                cancha = "Cancha San Eduardo",
                fechaHora = "Sáb 6:00 pm",
                formato = "7 vs 7",
                nivel = "Todos los niveles",
                costoPersona = 6,
                cuposFaltantes = 4,
                organizadorNombre = "Andrea",
                organizadorRating = 4.9,
                organizadorIniciales = "AN",
                estadoTexto = "Abierto a todos",
            ),
        )
    }

    val partidosFiltrados = partidosIniciales.filter { partido ->
        val coincideDeporte = filtroDeporte == null || partido.deporte == filtroDeporte
        val coincideHoy = !isFiltroHoyActivo || partido.fechaHora.startsWith("Hoy")
        val coincideFaltaUno = !isFiltroFaltaUnoActivo || partido.cuposFaltantes == 1
        val coincideGratis = !isFiltroGratisActivo || partido.costoPersona == 0
        coincideDeporte && coincideHoy && coincideFaltaUno && coincideGratis
    }

    val spacing = MaterialTheme.spacing

    Scaffold(
        containerColor = FulboColors.Background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onPublicarPartido,
                containerColor = FulboColors.Green,
                contentColor = FulboColors.OnGreen,
                elevation = FloatingActionButtonDefaults.elevation(6.dp),
                shape = CircleShape,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Publicar un partido", fontWeight = FontWeight.Bold) },
            )
        },
        modifier = modifier.fillMaxSize(),
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 90.dp),
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing.screen, vertical = spacing.md),
                ) {
                    PartidosHeader(
                        ciudadSeleccionada = ciudadSeleccionada,
                        onAbrirSelectorCiudad = { isDialogoCiudadesVisible = true },
                    )

                    Spacer(Modifier.height(spacing.md))

                    FiltroDeportesRow(
                        deporteSeleccionado = filtroDeporte,
                        onSeleccionarDeporte = { filtroDeporte = it },
                    )

                    Spacer(Modifier.height(spacing.sm))

                    FiltrosSecundariosRow(
                        isFiltroHoyActivo = isFiltroHoyActivo,
                        isFiltroFaltaUnoActivo = isFiltroFaltaUnoActivo,
                        isFiltroGratisActivo = isFiltroGratisActivo,
                        onToggleHoy = { isFiltroHoyActivo = !isFiltroHoyActivo },
                        onToggleFaltaUno = { isFiltroFaltaUnoActivo = !isFiltroFaltaUnoActivo },
                        onToggleGratis = { isFiltroGratisActivo = !isFiltroGratisActivo },
                    )
                }
            }

            if (partidosFiltrados.isEmpty()) {
                item {
                    EstadoVacioPartidos(modifier = Modifier.padding(spacing.xl))
                }
            } else {
                items(partidosFiltrados, key = { it.id }) { partido ->
                    PartidoCard(
                        partido = partido,
                        onUnirme = {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("¡Te uniste al partido en ${partido.cancha}!")
                            }
                        },
                        modifier = Modifier.padding(horizontal = spacing.screen, vertical = spacing.sm),
                    )
                }
            }
        }
    }

    if (isDialogoCiudadesVisible) {
        SelectorCiudadDialog(
            ciudades = CIUDADES_DISPONIBLES,
            ciudadSeleccionada = ciudadSeleccionada,
            onCiudadSeleccionada = { ciudad ->
                ciudadSeleccionada = ciudad
                isDialogoCiudadesVisible = false
            },
            onDismiss = { isDialogoCiudadesVisible = false },
        )
    }
}

@Composable
private fun PartidosHeader(
    ciudadSeleccionada: String,
    onAbrirSelectorCiudad: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Partidos cerca",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = FulboColors.TextPrimary,
        )

        Surface(
            onClick = onAbrirSelectorCiudad,
            shape = CircleShape,
            color = FulboColors.Surface,
            border = BorderStroke(1.dp, FulboColors.Outline),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = null,
                    tint = FulboColors.Green,
                    modifier = Modifier.size(16.dp),
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = ciudadSeleccionada,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = FulboColors.TextPrimary,
                )
                Icon(
                    imageVector = Icons.Outlined.KeyboardArrowDown,
                    contentDescription = null,
                    tint = FulboColors.TextSecondary,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}

@Composable
private fun FiltroDeportesRow(
    deporteSeleccionado: DeporteCasual?,
    onSeleccionarDeporte: (DeporteCasual?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = MaterialTheme.spacing
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
    ) {
        FulboChip(
            text = "Todos",
            isSelected = deporteSeleccionado == null,
            onClick = { onSeleccionarDeporte(null) },
        )
        DeporteCasual.entries.forEach { deporte ->
            FulboChip(
                text = "${deporte.emoji} ${deporte.nombre}",
                isSelected = deporteSeleccionado == deporte,
                onClick = { onSeleccionarDeporte(deporte) },
            )
        }
    }
}

@Composable
private fun FiltrosSecundariosRow(
    isFiltroHoyActivo: Boolean,
    isFiltroFaltaUnoActivo: Boolean,
    isFiltroGratisActivo: Boolean,
    onToggleHoy: () -> Unit,
    onToggleFaltaUno: () -> Unit,
    onToggleGratis: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = MaterialTheme.spacing
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
    ) {
        FulboChip(
            text = "Hoy",
            isSelected = isFiltroHoyActivo,
            onClick = onToggleHoy,
        )
        FulboChip(
            text = "Falta 1",
            isSelected = isFiltroFaltaUnoActivo,
            onClick = onToggleFaltaUno,
        )
        FulboChip(
            text = "Gratis",
            isSelected = isFiltroGratisActivo,
            onClick = onToggleGratis,
        )
    }
}

@Composable
private fun EstadoVacioPartidos(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "No se encontraron partidos con estos filtros.",
            style = MaterialTheme.typography.bodyLarge,
            color = FulboColors.TextSecondary,
        )
    }
}

/**
 * Tarjeta individual de partido con organizador, nivel, cupos y botón de unirse.
 */
@Composable
fun PartidoCard(
    partido: PartidoCasualItem,
    onUnirme: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val pillColor = if (partido.cuposFaltantes <= 1) FulboColors.Orange else FulboColors.Green

    FulboCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StatusPill(text = partido.estadoTexto, color = pillColor)
            Text(
                text = "${partido.deporte.emoji} ${partido.deporte.nombre}",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = FulboColors.TextSecondary,
            )
        }

        Spacer(Modifier.height(10.dp))

        Text(
            text = "${partido.cancha} · ${partido.fechaHora}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = FulboColors.TextPrimary,
        )

        Spacer(Modifier.height(4.dp))

        val costoTexto = if (partido.costoPersona == 0) "Gratis" else "S/ ${partido.costoPersona} por persona"
        Text(
            text = "${partido.formato} · ${partido.nivel} · $costoTexto",
            style = MaterialTheme.typography.bodyMedium,
            color = FulboColors.TextSecondary,
        )

        Spacer(Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                PlayerAvatar(
                    nombre = partido.organizadorNombre,
                    size = 36.dp,
                    ringColor = FulboColors.Outline,
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "${partido.organizadorNombre} ★ ${partido.organizadorRating}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = FulboColors.TextPrimary,
                )
            }

            Button(
                onClick = onUnirme,
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = FulboColors.Green,
                    contentColor = FulboColors.OnGreen,
                ),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            ) {
                Text("Unirme", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun SelectorCiudadDialog(
    ciudades: List<String>,
    ciudadSeleccionada: String,
    onCiudadSeleccionada: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Elige tu ciudad", color = FulboColors.TextPrimary) },
        containerColor = FulboColors.Surface,
        text = {
            Column {
                ciudades.forEach { ciudad ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onCiudadSeleccionada(ciudad) }
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = ciudad == ciudadSeleccionada,
                            onClick = { onCiudadSeleccionada(ciudad) },
                            colors = RadioButtonDefaults.colors(selectedColor = FulboColors.Green),
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(ciudad, style = MaterialTheme.typography.bodyLarge, color = FulboColors.TextPrimary)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar", color = FulboColors.Green)
            }
        },
    )
}
