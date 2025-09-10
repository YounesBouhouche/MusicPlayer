package younesbouhouche.musicplayer.main.presentation.util.composables

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.systemBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection

val leftEdgeWidth
    @Composable
    get() = with(LocalDensity.current) { WindowInsets.displayCutout.getLeft(this, LocalLayoutDirection.current).toDp() }

val rightEdgeWidth
    @Composable
    get() = with(LocalDensity.current) { WindowInsets.displayCutout.getRight(this, LocalLayoutDirection.current).toDp() }

val statusBarHeight
    @Composable
    get() = with(LocalDensity.current) { WindowInsets.systemBars.getTop(this).toDp() }

val navBarHeight
    @Composable
    get() = with(LocalDensity.current) { WindowInsets.systemBars.getBottom(this).toDp() }

val isCompact
    @Composable
    get() = true
