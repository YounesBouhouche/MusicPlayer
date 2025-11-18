package younesbouhouche.musicplayer.features.main.presentation.util

import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun topAppBarIconButtonColors() = IconButtonDefaults.iconButtonColors(
    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
)

@Composable
fun searchBarIconButtonColors() = IconButtonDefaults.iconButtonColors(
    containerColor = MaterialTheme.colorScheme.surfaceContainer,
    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
)