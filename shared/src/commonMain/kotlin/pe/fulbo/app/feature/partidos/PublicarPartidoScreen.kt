package pe.fulbo.app.feature.partidos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pe.fulbo.app.core.designsystem.component.FulboChip
import pe.fulbo.app.core.designsystem.component.FulboPrimaryButton
import pe.fulbo.app.core.designsystem.component.FulboStepper
import pe.fulbo.app.core.designsystem.component.FulboTextField
import pe.fulbo.app.core.designsystem.theme.FulboColors
import pe.fulbo.app.core.designsystem.theme.spacing
import pe.fulbo.app.core.model.DeporteCasual

private val NIVELES_PARTIDO_DISPONIBLES: List<String> = listOf("Casual", "Intermedio", "Todos")
private const val MINIMO_CUPOS_FALTANTES: Int = 1
private const val MAXIMO_CUPOS_FALTANTES: Int = 14

/**
 * Pantalla para publicar un nuevo partido casual en la comunidad (Mockup página 3).
 *
 * @param onVolver Navegación hacia atrás.
 * @param onPartidoPublicado Callback ejecutado tras confirmar la publicación.
 * @param canchaInicial Nombre de la cancha preseleccionada si proviene del mapa.
 * @param modifier Modificador de Compose.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicarPartidoScreen(
    onVolver: () -> Unit,
    onPartidoPublicado: () -> Unit,
    canchaInicial: String = "Cancha Los Álamos",
    modifier: Modifier = Modifier,
) {
    var deporteSeleccionado by remember { mutableStateOf(DeporteCasual.FUTBOL) }
    var cancha by remember { mutableStateOf(canchaInicial) }
    var fecha by remember { mutableStateOf("Hoy, 14 Nov") }
    var hora by remember { mutableStateOf("8:00 pm") }
    var formatoSeleccionado by remember { mutableStateOf("7 vs 7") }
    var cuposFaltantes by remember { mutableStateOf(2) }
    var nivelBuscado by remember { mutableStateOf("Intermedio") }
    var costoPersona by remember { mutableStateOf("8") }

    val canPublicar = cancha.isNotBlank() && fecha.isNotBlank() && hora.isNotBlank()
    val spacing = MaterialTheme.spacing

    Scaffold(
        containerColor = FulboColors.Background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Publicar partido",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = FulboColors.TextPrimary,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = FulboColors.TextPrimary,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = FulboColors.Background),
            )
        },
        modifier = modifier.fillMaxSize(),
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = spacing.screen)
                .verticalScroll(rememberScrollState()),
        ) {
            Spacer(Modifier.height(spacing.sm))

            SelectorDeporteRow(
                deporteSeleccionado = deporteSeleccionado,
                onDeporteSeleccionado = { deporte ->
                    deporteSeleccionado = deporte
                    formatoSeleccionado = DeporteCasual.obtenerFormatos(deporte).first()
                },
            )

            Spacer(Modifier.height(spacing.md))

            FulboTextField(
                value = cancha,
                onValueChange = { cancha = it },
                label = "Cancha",
                placeholder = "Nombre de la cancha o complejo",
            )

            Spacer(Modifier.height(spacing.md))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.md),
            ) {
                FulboTextField(
                    value = fecha,
                    onValueChange = { fecha = it },
                    label = "Fecha",
                    placeholder = "Ej: Hoy 8:00 pm",
                    modifier = Modifier.weight(1f),
                )
                FulboTextField(
                    value = hora,
                    onValueChange = { hora = it },
                    label = "Hora",
                    placeholder = "Ej: 8:00 pm",
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(Modifier.height(spacing.md))

            SelectorFormatoSection(
                deporte = deporteSeleccionado,
                formatoSeleccionado = formatoSeleccionado,
                onFormatoSeleccionado = { formatoSeleccionado = it },
            )

            Spacer(Modifier.height(spacing.md))

            CuposFaltantesStepperRow(
                cuposFaltantes = cuposFaltantes,
                onCuposChange = { cuposFaltantes = it },
            )

            Spacer(Modifier.height(spacing.md))

            SelectorNivelRow(
                nivelBuscado = nivelBuscado,
                onNivelSeleccionado = { nivelBuscado = it },
            )

            Spacer(Modifier.height(spacing.md))

            FulboTextField(
                value = costoPersona,
                onValueChange = { costoPersona = it },
                label = "Costo por persona (S/)",
                placeholder = "0 para gratis, o monto en soles",
            )

            Spacer(Modifier.height(spacing.xl))

            FulboPrimaryButton(
                text = "Publicar partido",
                isEnabled = canPublicar,
                onClick = onPartidoPublicado,
            )

            Spacer(Modifier.height(spacing.lg))
        }
    }
}

@Composable
private fun SelectorDeporteRow(
    deporteSeleccionado: DeporteCasual,
    onDeporteSeleccionado: (DeporteCasual) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = MaterialTheme.spacing
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Deporte",
            style = MaterialTheme.typography.labelLarge,
            color = FulboColors.TextSecondary,
        )
        Spacer(Modifier.height(spacing.xs))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            DeporteCasual.entries.forEach { deporte ->
                FulboChip(
                    text = "${deporte.emoji} ${deporte.nombre}",
                    isSelected = deporte == deporteSeleccionado,
                    onClick = { onDeporteSeleccionado(deporte) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SelectorFormatoSection(
    deporte: DeporteCasual,
    formatoSeleccionado: String,
    onFormatoSeleccionado: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = MaterialTheme.spacing
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Formato",
            style = MaterialTheme.typography.labelLarge,
            color = FulboColors.TextSecondary,
        )
        Spacer(Modifier.height(spacing.xs))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
            verticalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            DeporteCasual.obtenerFormatos(deporte).forEach { formato ->
                FulboChip(
                    text = formato,
                    isSelected = formato == formatoSeleccionado,
                    onClick = { onFormatoSeleccionado(formato) },
                )
            }
        }
    }
}

@Composable
private fun CuposFaltantesStepperRow(
    cuposFaltantes: Int,
    onCuposChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column {
            Text(
                text = "Jugadores que faltan",
                style = MaterialTheme.typography.labelLarge,
                color = FulboColors.TextPrimary,
            )
            Text(
                text = "Incluye porteros",
                style = MaterialTheme.typography.bodySmall,
                color = FulboColors.TextSecondary,
            )
        }
        FulboStepper(
            value = cuposFaltantes,
            onValueChange = onCuposChange,
            minimo = MINIMO_CUPOS_FALTANTES,
            maximo = MAXIMO_CUPOS_FALTANTES,
        )
    }
}

@Composable
private fun SelectorNivelRow(
    nivelBuscado: String,
    onNivelSeleccionado: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = MaterialTheme.spacing
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Nivel buscado",
            style = MaterialTheme.typography.labelLarge,
            color = FulboColors.TextSecondary,
        )
        Spacer(Modifier.height(spacing.xs))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            NIVELES_PARTIDO_DISPONIBLES.forEach { nivel ->
                FulboChip(
                    text = nivel,
                    isSelected = nivel == nivelBuscado,
                    onClick = { onNivelSeleccionado(nivel) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}
