package younesbouhouche.musicplayer.main.presentation.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MyListItem(
    modifier: Modifier = Modifier,
    background: Color = Color.Transparent,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
    headline: String,
    supporting: String,
    cover: @Composable () -> Unit,
    number: Int? = null,
    trailingContent: @Composable (RowScope.() -> Unit)? = null,
) {
    Row(
        modifier.combinedClickable(onLongClick = onLongClick, onClick = onClick)
            .background(background, MaterialTheme.shapes.medium)
            .clip(MaterialTheme.shapes.medium)
            .clipToBounds(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Box(Modifier.size(80.dp).padding(12.dp)) {
            cover()
            number?.let {
                Text(
                    "$it",
                    Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = 8.dp)
                        .border(1.dp, MaterialTheme.colorScheme.onSurface, RoundedCornerShape(100))
                        .background(
                            MaterialTheme.colorScheme.surface.copy(alpha = .9f),
                            RoundedCornerShape(100),
                        )
                        .clip(RoundedCornerShape(100))
                        .clipToBounds()
                        .padding(vertical = 2.dp)
                        .fillMaxWidth(.8f),
                    MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        Column(
            Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                headline,
                modifier = Modifier.fillMaxWidth(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style =
                    MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Normal,
                    ),
            )
            Text(
                supporting,
                modifier = Modifier.fillMaxWidth(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
            )
        }
        trailingContent?.invoke(this)
    }
}

@Composable
fun MyListItem(
    modifier: Modifier = Modifier,
    background: Color = Color.Transparent,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
    headline: String,
    supporting: String,
    cover: Any? = null,
    number: Int? = null,
    alternative: ImageVector = Icons.Default.MusicNote,
    fitIconToBounds: Boolean = false,
    trailingContent: @Composable (RowScope.() -> Unit)? = null
) {
    MyListItem(
        modifier,
        background,
        onClick,
        onLongClick,
        headline,
        supporting,
        {
            Box(
                Modifier
                    .clip(if (fitIconToBounds) MaterialTheme.shapes.medium else CircleShape)
                    .fillMaxSize()
                    .background(if (fitIconToBounds) MaterialTheme.colorScheme.surfaceContainer else Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                SubcomposeAsyncImage(
                    model = cover,
                    contentDescription = "",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    loading = {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(Modifier.fillMaxSize(.5f))
                        }
                    },
                    error = {
                        Box(Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = alternative,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(if (fitIconToBounds) .5f else 1f),
                                tint =
                                    if (fitIconToBounds) MaterialTheme.colorScheme.onSurfaceVariant
                                    else LocalContentColor.current
                            )
                        }
                    }
                )
            }
        },
        number,
        trailingContent,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MyCard(
    modifier: Modifier = Modifier,
    text: String,
    cover: Any?,
    alternative: ImageVector,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
) {
    Column(
        modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.extraLarge)
            .clipToBounds()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SubcomposeAsyncImage(
            model = cover,
            contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier =
                Modifier
                    .clip(CircleShape)
                    .clipToBounds()
                    .fillMaxWidth()
                    .aspectRatio(1f),
            error = {
                Icon(
                    alternative,
                    null,
                    Modifier.fillMaxSize(),
                )
            }
        )
        Text(
            text,
            Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
