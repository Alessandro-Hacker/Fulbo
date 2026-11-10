package pe.fulbo.app.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import pe.fulbo.app.R
import pe.fulbo.app.core.designsystem.theme.FulboColors

/** Pantalla temporal para rutas aún no implementadas. Borrar cuando cada feature exista. */
@Composable
fun PlaceholderScreen(titulo: String, milestone: String, modifier: Modifier = Modifier) {
    Column(
        modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(titulo, style = MaterialTheme.typography.headlineMedium)
        Text(
            stringResource(R.string.coming_soon, milestone),
            color = FulboColors.TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}
