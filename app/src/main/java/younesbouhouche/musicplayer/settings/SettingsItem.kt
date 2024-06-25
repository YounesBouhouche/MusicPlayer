package younesbouhouche.musicplayer.settings

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

fun LazyListScope.settingsItem(
    icon: ImageVector?,
    title: Int,
    text: Int,
    visible: Boolean? = null,
    onClick: (() -> Unit)? = null,
    trailingContent: (@Composable RowScope.() -> Unit)? = null
) {
    item {
        val content = @Composable {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = onClick != null) { if (onClick != null) onClick() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(Modifier.width(24.dp))
                if(icon == null) Spacer(Modifier.width(24.dp))
                else Icon(icon, null, modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(16.dp)
                ) {
                    Text(
                        text = stringResource(title),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        stringResource(text),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                if (trailingContent != null) trailingContent(this)
            }
        }
        if (visible == null)
            content()
        else
            AnimatedContent(targetState = visible, label = "") {
                if (it) content()
            }
    }
}


fun LazyListScope.largeSettingsItem(
    icon: ImageVector?,
    title: Int,
    text: Int,
    visible: Boolean? = null,
    onClick: (() -> Unit)? = null,
    trailingContent: (@Composable RowScope.() -> Unit)? = null
) {
    item {
        val content = @Composable {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = onClick != null) { if (onClick != null) onClick() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(Modifier.width(24.dp))
                if(icon == null) Spacer(Modifier.width(24.dp))
                else Icon(icon, null, modifier = Modifier.size(26.dp))
                Spacer(Modifier.width(8.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp, vertical = 20.dp)
                ) {
                    Text(
                        text = stringResource(title),
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Normal,
                            fontSize = 18.sp
                        )
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        stringResource(text),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                if (trailingContent != null) trailingContent(this)
            }
        }
        if (visible == null)
            content()
        else
            AnimatedContent(targetState = visible, label = "") {
                if (it) content()
            }
    }
}