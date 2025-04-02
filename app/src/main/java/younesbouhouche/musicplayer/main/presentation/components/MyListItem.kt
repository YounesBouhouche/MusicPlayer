package younesbouhouche.musicplayer.main.presentation.components

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
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
            MyImage(
                model = cover,
                icon = alternative,
                modifier = Modifier.fillMaxSize(),
            )
        },
        number,
        trailingContent,
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.MyListItem(
    modifier: Modifier = Modifier,
    background: Color = Color.Transparent,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
    headline: String,
    supporting: String,
    cover: Any? = null,
    number: Int? = null,
    alternative: ImageVector = Icons.Default.MusicNote,
    animatedContentScope: AnimatedContentScope,
    key: String,
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
            MyImage(
                model = cover,
                icon = alternative,
                modifier = Modifier
                    .sharedElement(
                        rememberSharedContentState(key = key),
                        animatedVisibilityScope = animatedContentScope
                    )
                    .fillMaxSize(),
            )
        },
        number,
        trailingContent,
    )
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.MyCard(
    modifier: Modifier = Modifier,
    text: String,
    cover: Any?,
    alternative: ImageVector,
    animatedContentScope: AnimatedContentScope,
    key: String,
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
        MyImage(
            model = cover,
            icon = alternative,
            modifier =
                Modifier
                    .sharedElement(
                        rememberSharedContentState(key = key),
                        animatedVisibilityScope = animatedContentScope
                    )
                    .fillMaxWidth()
                    .aspectRatio(1f),
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
