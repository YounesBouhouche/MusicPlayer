package younesbouhouche.musicplayer.features.main.presentation.util.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity

@Composable
fun Int.toDp() = with(LocalDensity.current) { this@toDp.toDp() }
