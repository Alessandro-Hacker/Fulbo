package pe.fulbo.app.feature.equipo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pe.fulbo.app.core.designsystem.component.FulboCard
import pe.fulbo.app.core.designsystem.component.FulboChip
import pe.fulbo.app.core.designsystem.component.FulboPrimaryButton
import pe.fulbo.app.core.designsystem.component.FulboStepper
import pe.fulbo.app.core.designsystem.component.FulboTextField
import pe.fulbo.app.core.designsystem.component.PlayerAvatar
import pe.fulbo.app.core.designsystem.theme.FulboColors
import pe.fulbo.app.core.designsystem.theme.spacing

private val OPCIONES_TIPO_RIVAL: List<String> = listOf("Equipo de la app", "Equipo invitado")
private const val MINIMO_GOLES_MARCADOR: Int = 0
private const val MAXIMO_GOLES_MARCADOR: Int = 30

/**
 * Pantalla de registro de marcador y resultado de partido entre dos equipos (Mockup página 10).
 *
 * @param onVolver Navegación atrás.
 * @param onVerAlineaciones Abre el detalle de las alineaciones tácticas.
 * @param onGuardarResultado Callback invocado tras guardar el marcador.
 * @param modifier Modificador de Compose.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrarResultadoScreen(
    onVolver: () -> Unit,
    onVerAlineaciones: () -> Unit,
    onGuardarResultado: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var golesLocal by remember { mutableStateOf(4) }
    var golesVisitante by remember { mutableStateOf(2) }
    var nombreVisitante by remember { mutableStateOf("Rayo FC") }
    var fecha by remember { mutableStateOf("Hoy, 14 Nov") }
    var cancha by remember { mutableStateOf("Cancha Los Álamos") }
    var tipoRival by remember { mutableStateOf(OPCIONES_TIPO_RIVAL.first()) }

    val canGuardarResultado = nombreVisitante.isNotBlank() && fecha.isNotBlank() && cancha.isNotBlank()
    val spacing = MaterialTheme.spacing

    Scaffold(
        containerColor = FulboColors.Background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Registrar resultado",
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

            MarcadorScoreboardCard(
                nombreLocal = "Los Pibes",
                golesLocal = golesLocal,
                onGolesLocalChange = { golesLocal = it },
                nombreVisitante = nombreVisitante,
                golesVisitante = golesVisitante,
                onGolesVisitanteChange = { golesVisitante = it },
            )

            Spacer(Modifier.height(spacing.lg))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.md),
            ) {
                FulboTextField(
                    value = fecha,
                    onValueChange = { fecha = it },
                    label = "Fecha",
                    placeholder = "Fecha del partido",
                    modifier = Modifier.weight(1f),
                )
                FulboTextField(
                    value = cancha,
                    onValueChange = { cancha = it },
                    label = "Cancha",
                    placeholder = "Lugar",
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(Modifier.height(spacing.md))

            TipoRivalSelectorRow(
                tipoRivalSeleccionado = tipoRival,
                onTipoRivalSeleccionado = { tipoRival = it },
            )

            Spacer(Modifier.height(spacing.md))

            AlineacionesCard(onVerAlineaciones = onVerAlineaciones)

            Spacer(Modifier.height(spacing.md))

            AvisoRachaRow()

            Spacer(Modifier.weight(1f))
            Spacer(Modifier.height(spacing.xl))

            FulboPrimaryButton(
                text = "Guardar resultado",
                isEnabled = canGuardarResultado,
                onClick = onGuardarResultado,
            )

            Spacer(Modifier.height(spacing.lg))
        }
    }
}

@Composable
private fun MarcadorScoreboardCard(
    nombreLocal: String,
    golesLocal: Int,
    onGolesLocalChange: (Int) -> Unit,
    nombreVisitante: String,
    golesVisitante: Int,
    onGolesVisitanteChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    FulboCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            EquipoMarcadorColumna(
                nombre = nombreLocal,
                iniciales = "LO",
                colorAro = FulboColors.Green,
                goles = golesLocal,
                onGolesChange = onGolesLocalChange,
                modifier = Modifier.weight(1f),
            )

            Text(
                text = "vs",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = FulboColors.TextSecondary,
                modifier = Modifier.padding(horizontal = 8.dp),
            )

            EquipoMarcadorColumna(
                nombre = nombreVisitante,
                iniciales = "RA",
                colorAro = FulboColors.Blue,
                goles = golesVisitante,
                onGolesChange = onGolesVisitanteChange,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun EquipoMarcadorColumna(
    nombre: String,
    iniciales: String,
    colorAro: Color,
    goles: Int,
    onGolesChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        PlayerAvatar(nombre = iniciales, size = 52.dp, ringColor = colorAro)
        Spacer(Modifier.height(8.dp))
        Text(
            text = nombre,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = FulboColors.TextPrimary,
        )
        Spacer(Modifier.height(10.dp))
        FulboStepper(
            value = goles,
            onValueChange = onGolesChange,
            minimo = MINIMO_GOLES_MARCADOR,
            maximo = MAXIMO_GOLES_MARCADOR,
        )
    }
}

@Composable
private fun TipoRivalSelectorRow(
    tipoRivalSeleccionado: String,
    onTipoRivalSeleccionado: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = MaterialTheme.spacing
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
    ) {
        OPCIONES_TIPO_RIVAL.forEach { opcion ->
            FulboChip(
                text = opcion,
                isSelected = tipoRivalSeleccionado == opcion,
                onClick = { onTipoRivalSeleccionado(opcion) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun AlineacionesCard(
    onVerAlineaciones: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FulboCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = "Alineaciones",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = FulboColors.TextPrimary,
                )
                Text(
                    text = "Revisa la formación táctica registrada",
                    style = MaterialTheme.typography.bodySmall,
                    color = FulboColors.TextSecondary,
                )
            }
            TextButton(onClick = onVerAlineaciones) {
                Text("Ver", color = FulboColors.Green, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun AvisoRachaRow(
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(horizontal = 4.dp),
    ) {
        Icon(
            imageVector = Icons.Outlined.Info,
            contentDescription = null,
            tint = FulboColors.Green,
            modifier = Modifier.size(18.dp),
        )
        Spacer(Modifier.padding(horizontal = 4.dp))
        Text(
            text = "Los dos equipos suman partido a su racha.",
            style = MaterialTheme.typography.bodyMedium,
            color = FulboColors.TextSecondary,
        )
    }
}
