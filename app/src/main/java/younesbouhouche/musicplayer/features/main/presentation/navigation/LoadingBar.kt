package younesbouhouche.musicplayer.features.main.presentation.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.features.main.domain.models.LoadingState
import younesbouhouche.musicplayer.features.main.presentation.util.intUpDownTransSpec

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LoadingBar(
    state: LoadingState,
    modifier: Modifier = Modifier
) {
    Row(
        modifier.statusBarsPadding()
            .padding(8.dp)
            .padding(bottom = 12.dp)
            .clip(MaterialTheme.shapes.extraLarge)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CircularWavyProgressIndicator(
            progress = {
                state.getValue()
            },
            Modifier.size(40.dp),
            stroke =
                Stroke(
                    width = with(LocalDensity.current) { 3.dp.toPx() },
                    cap = StrokeCap.Round,
                ),
            trackStroke =
                Stroke(
                    width = with(LocalDensity.current) { 3.dp.toPx() },
                    cap = StrokeCap.Round,
                ),
        )
        AnimatedContent(
            state.step,
            transitionSpec = intUpDownTransSpec,
        ) { step ->
            Text(
                stringResource(
                    when(step) {
                        0 -> R.string.loading_files
                        1 -> R.string.loading_thumbnails
                        2 -> R.string.loading_artists
                        else -> R.string.loading
                    },
                    state.progress,
                    state.progressMax,
                ),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}