package pe.fulbo.app.feature.perfil

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EventBusy
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.SportsSoccer
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import pe.fulbo.app.feature.perfil.domain.PerfilUsuario
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pe.fulbo.app.core.designsystem.component.FulboCard
import pe.fulbo.app.core.designsystem.component.FulboSecondaryButton
import pe.fulbo.app.core.designsystem.component.RankAvatar
import pe.fulbo.app.core.designsystem.component.StarRatingBar
import pe.fulbo.app.core.designsystem.theme.FulboColors
import pe.fulbo.app.core.designsystem.theme.spacing
import pe.fulbo.app.core.model.RangoEquipo
import pe.fulbo.app.core.model.ResenaJugador

private const val PUNTAJE_MAXIMO_CONDUCTA: Float = 5.0f

/** Conecta la pantalla con el ViewModel: carga el perfil real del usuario con sesión. */
@Composable
fun PerfilRoute(
    onCerrarSesion: () -> Unit,
    viewModel: PerfilViewModel = koinViewModel(),
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val perfil = estado.perfil
    when {
        perfil != null -> PerfilScreen(perfil = perfil, onCerrarSesion = onCerrarSesion)
        estado.cargando -> Box(Modifier.fillMaxSize().background(FulboColors.Background), Alignment.Center) {
            CircularProgressIndicator(color = FulboColors.Green)
        }
        else -> Column(
            Modifier.fillMaxSize().background(FulboColors.Background).padding(MaterialTheme.spacing.screen),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.md, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(estado.error ?: "No se pudo cargar tu perfil.", color = FulboColors.TextSecondary)
            FulboSecondaryButton(text = "Reintentar", onClick = viewModel::cargar)
            FulboSecondaryButton(text = "Cerrar sesión", onClick = onCerrarSesion)
        }
    }
}

/**
 * Pantalla de perfil de jugador con reputación y estadísticas (Mockup página 4).
 * Las reseñas individuales llegarán con las calificaciones; por ahora no se muestran.
 */
@Composable
fun PerfilScreen(
    perfil: PerfilUsuario,
    modifier: Modifier = Modifier,
    onCerrarSesion: (() -> Unit)? = null,
) {
    val spacing = MaterialTheme.spacing

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(FulboColors.Background),
        contentPadding = PaddingValues(horizontal = spacing.screen, vertical = spacing.md),
        verticalArrangement = Arrangement.spacedBy(spacing.md),
    ) {
        item {
            PerfilCabecera(
                nombre = perfil.nombreCompleto,
                alias = perfil.alias,
                posicion = perfil.posicion ?: "Sin posición",
                nivel = perfil.nivel ?: "Sin nivel",
                iniciales = perfil.iniciales,
                rango = RangoEquipo.BRONCE,
            )
        }

        item {
            if (perfil.reputacion == null) {
                FulboCard {
                    Text(
                        text = "Aún no tienes calificaciones. Juega tus primeros partidos y aparecerán aquí.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = FulboColors.TextSecondary,
                    )
                }
            } else {
                PerfilReputacionCard(
                    rating = perfil.reputacion,
                    totalResenas = perfil.numCalificaciones,
                    puntajePuntualidad = (perfil.repPuntualidad ?: 0.0).toFloat(),
                    puntajeAsistencia = (perfil.repAsistencia ?: 0.0).toFloat(),
                    puntajePago = (perfil.repPago ?: 0.0).toFloat(),
                    puntajeTrato = (perfil.repTrato ?: 0.0).toFloat(),
                )
            }
        }

        item {
            PerfilEstadisticasRow(
                partidosJugados = perfil.partidosJugados,
                semanasRacha = 0,
                faltas = perfil.faltas,
            )
        }

        if (onCerrarSesion != null) {
            item {
                FulboSecondaryButton(text = "Cerrar sesión", onClick = onCerrarSesion)
            }
        }
    }
}

@Composable
private fun PerfilCabecera(
    nombre: String,
    alias: String,
    posicion: String,
    nivel: String,
    iniciales: String,
    rango: RangoEquipo,
    modifier: Modifier = Modifier,
) {
    val spacing = MaterialTheme.spacing
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(spacing.sm))
        RankAvatar(
            iniciales = iniciales,
            rango = rango,
            size = 84.dp,
        )
        Spacer(Modifier.height(spacing.sm))

        Text(
            text = nombre,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = FulboColors.TextPrimary,
        )

        Text(
            text = "“$alias” · $posicion · $nivel",
            style = MaterialTheme.typography.bodyLarge,
            color = FulboColors.TextSecondary,
        )
    }
}

@Composable
private fun PerfilReputacionCard(
    rating: Double,
    totalResenas: Int,
    puntajePuntualidad: Float,
    puntajeAsistencia: Float,
    puntajePago: Float,
    puntajeTrato: Float,
    modifier: Modifier = Modifier,
) {
    FulboCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = rating.toString(),
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Bold,
                        color = FulboColors.TextPrimary,
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "de 5",
                        style = MaterialTheme.typography.titleMedium,
                        color = FulboColors.TextSecondary,
                        modifier = Modifier.padding(bottom = 6.dp),
                    )
                }
                Text(
                    text = "$totalResenas reseñas",
                    style = MaterialTheme.typography.bodyMedium,
                    color = FulboColors.TextSecondary,
                )
            }

            StarRatingBar(rating = rating, starSize = 22.dp)
        }

        Spacer(Modifier.height(18.dp))

        MetricaDesgloseFila(titulo = "Puntualidad", puntaje = puntajePuntualidad)
        Spacer(Modifier.height(10.dp))
        MetricaDesgloseFila(titulo = "Asiste", puntaje = puntajeAsistencia)
        Spacer(Modifier.height(10.dp))
        MetricaDesgloseFila(titulo = "Paga su parte", puntaje = puntajePago)
        Spacer(Modifier.height(10.dp))
        MetricaDesgloseFila(titulo = "Buen trato", puntaje = puntajeTrato)
    }
}

@Composable
private fun MetricaDesgloseFila(
    titulo: String,
    puntaje: Float,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = titulo,
            style = MaterialTheme.typography.bodyMedium,
            color = FulboColors.TextPrimary,
            modifier = Modifier.width(100.dp),
        )

        LinearProgressIndicator(
            progress = { puntaje / PUNTAJE_MAXIMO_CONDUCTA },
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .clip(CircleShape),
            color = FulboColors.Green,
            trackColor = FulboColors.SurfaceHigh,
        )

        Spacer(Modifier.width(12.dp))

        Text(
            text = puntaje.toString(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = FulboColors.TextPrimary,
        )
    }
}

@Composable
private fun PerfilEstadisticasRow(
    partidosJugados: Int,
    semanasRacha: Int,
    faltas: Int,
    modifier: Modifier = Modifier,
) {
    val spacing = MaterialTheme.spacing
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
    ) {
        StatCard(
            valor = partidosJugados.toString(),
            etiqueta = "Partidos",
            icono = Icons.Outlined.SportsSoccer,
            color = FulboColors.Green,
            modifier = Modifier.weight(1f),
        )
        StatCard(
            valor = "$semanasRacha sem",
            etiqueta = "Racha",
            icono = Icons.Outlined.LocalFireDepartment,
            color = FulboColors.Orange,
            modifier = Modifier.weight(1f),
        )
        StatCard(
            valor = faltas.toString(),
            etiqueta = "Faltas",
            icono = Icons.Outlined.EventBusy,
            color = FulboColors.Red,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun StatCard(
    valor: String,
    etiqueta: String,
    icono: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = MaterialTheme.shapes.large,
        color = FulboColors.Surface,
        border = BorderStroke(1.dp, FulboColors.Outline),
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp),
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = valor,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = FulboColors.TextPrimary,
            )
            Text(
                text = etiqueta,
                style = MaterialTheme.typography.bodySmall,
                color = FulboColors.TextSecondary,
            )
        }
    }
}

@Composable
private fun PerfilResenasSection(
    resenas: List<ResenaJugador>,
    modifier: Modifier = Modifier,
) {
    val spacing = MaterialTheme.spacing
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "Reseñas recientes",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = FulboColors.TextPrimary,
            modifier = Modifier.padding(vertical = spacing.xs),
        )

        Spacer(Modifier.height(spacing.xs))

        resenas.forEach { resena ->
            FulboCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
            ) {
                Text(
                    text = "“${resena.texto}”",
                    style = MaterialTheme.typography.bodyLarge,
                    fontStyle = FontStyle.Italic,
                    color = FulboColors.TextPrimary,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "— ${resena.autor}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = FulboColors.Green,
                )
            }
        }
    }
}
