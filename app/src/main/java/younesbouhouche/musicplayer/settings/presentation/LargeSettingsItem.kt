package younesbouhouche.musicplayer.settings.presentation

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

data class LargeSettingsItem(
    @StringRes val title: Int,
    @StringRes val description: Int,
    val icon: ImageVector,
    val onClick: (() -> Unit)?
)

@Composable
fun LargeSettingsItem(
    item: LargeSettingsItem,
    modifier: Modifier = Modifier
) {
    Row(modifier
        .background(MaterialTheme.colorScheme.surfaceContainer)
        .then(item.onClick?.let { Modifier.clickable(onClick = it) } ?: Modifier)
        .padding(16.dp, 20.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(item.icon, null, Modifier.size(40.dp).padding(start = 4.dp))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(stringResource(item.title), style = MaterialTheme.typography.titleMedium)
            Text(stringResource(item.description), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

fun LazyListScope.settingsItem(
    icon: ImageVector?,
    title: Int,
    text: Int,
    visible: Boolean = true,
    onClick: (() -> Unit)? = null,
    trailingContent: (@Composable RowScope.() -> Unit)? = null,
) {
    item {
        AnimatedVisibility(
            visible = visible,
            label = "",
            enter = fadeIn() + expandVertically(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .then(onClick?.let { Modifier.clickable(onClick = onClick) } ?: Modifier),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(Modifier.width(24.dp))
                if (icon == null) {
                    Spacer(Modifier.width(24.dp))
                } else {
                    Icon(icon, null, modifier = Modifier.size(24.dp))
                }
                Spacer(Modifier.width(8.dp))
                Column(
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(16.dp),
                ) {
                    Text(
                        text = stringResource(title),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        stringResource(text),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline,
                    )
                }
                if (trailingContent != null) trailingContent(this)
            }
        }
    }
}