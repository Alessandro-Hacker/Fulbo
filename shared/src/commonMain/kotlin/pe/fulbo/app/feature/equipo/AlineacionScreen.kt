package pe.fulbo.app.feature.equipo

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import pe.fulbo.app.core.designsystem.component.FulboCard
import pe.fulbo.app.core.designsystem.component.FulboChip
import pe.fulbo.app.core.designsystem.component.FulboPrimaryButton
import pe.fulbo.app.core.designsystem.component.PlayerAvatar
import pe.fulbo.app.core.designsystem.theme.FulboColors
import pe.fulbo.app.core.designsystem.theme.spacing

private val PESTANAS_EQUIPO: List<String> = listOf("Equipo A", "Equipo B", "Ambos")
private val FORMACIONES_FUTBOL: List<String> = listOf("2-3-1", "3-2-1", "2-2-2")

private val COLOR_CESPED_CANCHA: Color = Color(0xFF142B1D)
private val COLOR_BORDE_CANCHA: Color = Color(0xFF265337)
private val COLOR_LINEA_CANCHA: Color = Color(0x55FFFFFF)

private data class PosicionTactico(
    val sigla: String,
    val relX: Float,
    val relY: Float,
)

/**
 * Pantalla táctica con vista de cancha de fútbol y banca de suplentes (Mockup página 7).
 *
 * @param onVolver Callback para navegar atrás.
 * @param modifier Modificador de Compose.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlineacionScreen(
    onVolver: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var equipoTab by remember { mutableStateOf(PESTANAS_EQUIPO.first()) }
    var formacionSeleccionada by remember { mutableStateOf(FORMACIONES_FUTBOL.first()) }

    val spacing = MaterialTheme.spacing
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        containerColor = FulboColors.Background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Alineación",
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
            PestanasEquipoRow(
                equipoTab = equipoTab,
                onSeleccionarTab = { equipoTab = it },
            )

            Spacer(Modifier.height(spacing.md))

            SelectorFormacionesRow(
                formacionSeleccionada = formacionSeleccionada,
                onSeleccionarFormacion = { formacionSeleccionada = it },
            )

            Spacer(Modifier.height(spacing.md))

            CanchaTacticoVisual(
                formacion = formacionSeleccionada,
                onSeleccionarPosicion = { sigla ->
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Posición seleccionada: $sigla")
                    }
                },
            )

            Spacer(Modifier.height(spacing.lg))

            BancaSuplentesSection()

            Spacer(Modifier.height(spacing.lg))

            FulboPrimaryButton(
                text = "Compartir alineación",
                onClick = {
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Alineación copiada para compartir con el equipo")
                    }
                },
            )

            Spacer(Modifier.height(spacing.lg))
        }
    }
}

@Composable
private fun PestanasEquipoRow(
    equipoTab: String,
    onSeleccionarTab: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = MaterialTheme.spacing
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
    ) {
        PESTANAS_EQUIPO.forEach { tab ->
            FulboChip(
                text = tab,
                isSelected = equipoTab == tab,
                onClick = { onSeleccionarTab(tab) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun SelectorFormacionesRow(
    formacionSeleccionada: String,
    onSeleccionarFormacion: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val spacing = MaterialTheme.spacing
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
    ) {
        FORMACIONES_FUTBOL.forEach { formacion ->
            FulboChip(
                text = formacion,
                isSelected = formacionSeleccionada == formacion,
                onClick = { onSeleccionarFormacion(formacion) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun CanchaTacticoVisual(
    formacion: String,
    onSeleccionarPosicion: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(340.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(COLOR_CESPED_CANCHA)
            .border(1.5.dp, COLOR_BORDE_CANCHA, RoundedCornerShape(16.dp)),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val ancho = size.width
            val alto = size.height
            val stroke = Stroke(width = 2.dp.toPx())
            val margen = 16.dp.toPx()

            // Límites exteriores
            drawRect(
                color = COLOR_LINEA_CANCHA,
                topLeft = Offset(margen, margen),
                size = Size(ancho - 2 * margen, alto - 2 * margen),
                style = stroke,
            )

            // Línea central
            drawLine(
                color = COLOR_LINEA_CANCHA,
                start = Offset(margen, alto / 2),
                end = Offset(ancho - margen, alto / 2),
                strokeWidth = 2.dp.toPx(),
            )

            // Círculo central
            drawCircle(
                color = COLOR_LINEA_CANCHA,
                radius = 32.dp.toPx(),
                center = Offset(ancho / 2, alto / 2),
                style = stroke,
            )

            // Área portería inferior
            val anchoArea = ancho * 0.45f
            val altoArea = 46.dp.toPx()
            drawRect(
                color = COLOR_LINEA_CANCHA,
                topLeft = Offset((ancho - anchoArea) / 2, alto - margen - altoArea),
                size = Size(anchoArea, altoArea),
                style = stroke,
            )

            // Área portería superior
            drawRect(
                color = COLOR_LINEA_CANCHA,
                topLeft = Offset((ancho - anchoArea) / 2, margen),
                size = Size(anchoArea, altoArea),
                style = stroke,
            )
        }

        val posiciones = when (formacion) {
            "3-2-1" -> listOf(
                PosicionTactico("AR", 0.5f, 0.86f),
                PosicionTactico("DF", 0.22f, 0.68f),
                PosicionTactico("DF", 0.5f, 0.68f),
                PosicionTactico("DF", 0.78f, 0.68f),
                PosicionTactico("ME", 0.35f, 0.45f),
                PosicionTactico("ME", 0.65f, 0.45f),
                PosicionTactico("DL", 0.5f, 0.22f),
            )
            "2-2-2" -> listOf(
                PosicionTactico("AR", 0.5f, 0.86f),
                PosicionTactico("DF", 0.3f, 0.70f),
                PosicionTactico("DF", 0.7f, 0.70f),
                PosicionTactico("ME", 0.3f, 0.46f),
                PosicionTactico("ME", 0.7f, 0.46f),
                PosicionTactico("DL", 0.35f, 0.22f),
                PosicionTactico("DL", 0.65f, 0.22f),
            )
            else -> listOf( // "2-3-1"
                PosicionTactico("AR", 0.5f, 0.86f),
                PosicionTactico("DF", 0.28f, 0.70f),
                PosicionTactico("DF", 0.72f, 0.70f),
                PosicionTactico("ME", 0.20f, 0.46f),
                PosicionTactico("ME", 0.50f, 0.46f),
                PosicionTactico("ME", 0.80f, 0.46f),
                PosicionTactico("DL", 0.5f, 0.22f),
            )
        }

        posiciones.forEach { posicion ->
            val colorNodo = when (posicion.sigla) {
                "AR" -> FulboColors.Orange
                "DF" -> FulboColors.Blue
                "ME" -> FulboColors.Green
                else -> FulboColors.Purple
            }

            Box(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset(
                            x = ((320 * posicion.relX) - 18).dp,
                            y = ((340 * posicion.relY) - 18).dp,
                        )
                        .size(36.dp)
                        .shadow(4.dp, CircleShape)
                        .clip(CircleShape)
                        .background(colorNodo)
                        .clickable { onSeleccionarPosicion(posicion.sigla) },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = posicion.sigla,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = FulboColors.OnGreen,
                    )
                }
            }
        }
    }
}

@Composable
private fun BancaSuplentesSection(
    modifier: Modifier = Modifier,
) {
    val spacing = MaterialTheme.spacing
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Banca",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = FulboColors.TextPrimary,
        )
        Spacer(Modifier.height(spacing.sm))

        val suplentes = listOf(
            Pair("PA", "Pablo · Medio"),
            Pair("JO", "Jorge · Defensa"),
        )

        suplentes.forEach { (iniciales, descripcion) ->
            FulboCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    PlayerAvatar(
                        nombre = iniciales,
                        size = 38.dp,
                        ringColor = FulboColors.Outline,
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = descripcion,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = FulboColors.TextPrimary,
                    )
                }
            }
        }
    }
}
