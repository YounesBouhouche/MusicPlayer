package younesbouhouche.musicplayer.ui

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp


val statusBarHeight
    @Composable
    get() = with(LocalDensity.current) { WindowInsets.systemBars.getTop(this).toDp() }

val navBarHeight
    @Composable
    get() = with(LocalDensity.current) { WindowInsets.systemBars.getBottom(this).toDp() }

@Composable
fun WindowInsets.getVertical() =
    (getTop(LocalDensity.current) + getBottom(LocalDensity.current)).dp