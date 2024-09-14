package younesbouhouche.musicplayer.core.presentation

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

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
    cover: ImageBitmap? = null,
    number: Int? = null,
    alternative: ImageVector = Icons.Default.MusicNote,
    trailingContent: @Composable (RowScope.() -> Unit)? = null,
) {
    val isMusicCard = alternative == Icons.Default.MusicNote
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
                    .fillMaxSize()
                    .clip(
                        if (isMusicCard) {
                            MaterialTheme.shapes.medium
                        } else {
                            CircleShape
                        },
                    )
                    .clipToBounds()
                    .background(
                        if (isMusicCard) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            Color.Transparent
                        },
                    ),
                contentAlignment = Alignment.Center,
            ) {
                if (cover == null) {
                    Icon(
                        alternative,
                        null,
                        Modifier.fillMaxSize(
                            if (isMusicCard) .75f else 1f,
                        ),
                        if (isMusicCard) {
                            MaterialTheme.colorScheme.surface
                        } else {
                            LocalContentColor.current
                        },
                    )
                } else {
                    Image(
                        cover,
                        null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                }
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
    cover: Bitmap?,
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
        AnimatedContent(
            targetState = cover,
            label = "",
            modifier =
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
        ) {
            if (it == null) {
                Icon(
                    alternative,
                    null,
                    Modifier.fillMaxSize(),
                )
            } else {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize().clip(CircleShape).clipToBounds(),
                )
            }
        }
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
