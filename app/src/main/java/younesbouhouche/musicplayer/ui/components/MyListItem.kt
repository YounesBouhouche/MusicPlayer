package younesbouhouche.musicplayer.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MyListItem(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
    headline: String,
    supporting: String,
    cover: ImageBitmap? = null,
    alternative: ImageVector = Icons.Default.MusicNote,
    trailingContent: @Composable (RowScope.() -> Unit)? = null,
) {
    val isMusicCard = alternative == Icons.Default.MusicNote
    Row(modifier.combinedClickable(onLongClick = onLongClick, onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            Modifier
                .size(80.dp)
                .padding(12.dp)
                .clip(
                    if (isMusicCard) MaterialTheme.shapes.medium
                    else CircleShape
                )
                .clipToBounds()
                .background(
                    if (isMusicCard) MaterialTheme.colorScheme.primary
                    else Color.Transparent
                ),
            contentAlignment = Alignment.Center) {
            if (cover == null)
                Icon(
                    alternative,
                    null,
                    Modifier.fillMaxSize(
                        if (isMusicCard) .75f else 1f
                    ),
                    if (isMusicCard) MaterialTheme.colorScheme.surface
                    else MaterialTheme.colorScheme.primary
                )
            else
                Image(
                    cover,
                    null,
                    Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
        }
        Column(
            Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                headline,
                modifier = Modifier.fillMaxWidth(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                supporting,
                modifier = Modifier.fillMaxWidth(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
            )
        }
        trailingContent?.invoke(this)
    }
}