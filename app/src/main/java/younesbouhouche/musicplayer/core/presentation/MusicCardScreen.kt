package younesbouhouche.musicplayer.core.presentation

import android.os.Build
import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toOffset
import com.kmpalette.color
import com.kmpalette.rememberPaletteState
import sh.calvin.reorderable.ReorderableCollectionItemScope
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.ReorderableLazyListState
import younesbouhouche.musicplayer.main.domain.models.MusicCard
import younesbouhouche.musicplayer.main.presentation.util.timeString
import younesbouhouche.musicplayer.ui.theme.AppTheme

@Composable
fun MusicCardScreen(
    file: MusicCard,
    modifier: Modifier = Modifier,
    number: Int? = null,
    background: Color = Color.Transparent,
    trailingContent: @Composable RowScope.() -> Unit = {},
    onLongClick: () -> Unit,
    onClick: () -> Unit,
) {
    MyListItem(
        modifier = modifier,
        number = number,
        background = background,
        onClick = onClick,
        onLongClick = onLongClick,
        headline = file.title,
        supporting = "${file.artist} - ${file.duration.timeString}",
        cover = file.cover?.asImageBitmap(),
        trailingContent = {
            trailingContent()
            IconButton(onClick = onLongClick) {
                Icon(Icons.Default.MoreVert, null)
            }
        },
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeMusicCard(
    card: MusicCard,
    modifier: Modifier = Modifier,
    onLongClick: () -> Unit,
    onClick: () -> Unit,
) {
    val paletteState = rememberPaletteState()
    LaunchedEffect(card.cover) {
        card.cover?.let { paletteState.generate(it.asImageBitmap()) }
    }
    AppTheme(
        paletteState.palette?.vibrantSwatch?.color ?: paletteState.palette?.dominantSwatch?.color
    ) {
        Box(
            modifier.size(300.dp, 112.dp)
                .clip(CardDefaults.shape)
                .combinedClickable(onClick = onClick, onLongClick = onLongClick)) {
            card.cover?.let {
                Image(
                    it.asImageBitmap(),
                    null,
                    Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            Row(
                Modifier
                    .background(
                        Brush
                            .horizontalGradient(
                                listOf(
                                    MaterialTheme.colorScheme.surfaceContainer,
                                    MaterialTheme.colorScheme.surfaceContainer.copy(.2f)
                                )
                            )
                    )
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier
                        .size(80.dp)
                        .background(MaterialTheme.colorScheme.surfaceContainer, MaterialTheme.shapes.medium)
                        .clip(MaterialTheme.shapes.medium)
                        .clipToBounds(),
                    contentAlignment = Alignment.Center
                ) {
                    if (card.cover == null)
                        Icon(
                            Icons.Default.MusicNote,
                            null,
                            Modifier.size(60.dp),
                            MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    else
                        Image(
                            card.cover!!.asImageBitmap(),
                            null,
                            Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                }
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        card.title,
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.bodyLarge.copy(
                            shadow = Shadow(
                                MaterialTheme.colorScheme.surface,
                                IntOffset(0, 5).toOffset(),
                                3f
                            )
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        card.artist,
                        modifier = Modifier.fillMaxWidth(),
                        style = MaterialTheme.typography.bodySmall.copy(
                            shadow = Shadow(
                                MaterialTheme.colorScheme.surface,
                                IntOffset(0, 5).toOffset(),
                                3f
                            )
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun ReorderableCollectionItemScope.MusicCardScreen(
    file: MusicCard,
    modifier: Modifier = Modifier,
    number: Int? = null,
    background: Color = Color.Transparent,
    onLongClick: () -> Unit = {},
    onClick: () -> Unit = {},
) {
    MyListItem(
        modifier =
            modifier.background(background, MaterialTheme.shapes.medium)
                .clip(MaterialTheme.shapes.medium).clipToBounds(),
        onLongClick = onLongClick,
        onClick = onClick,
        headline = file.title,
        supporting = "${file.artist} - ${file.duration.timeString}",
        cover = file.cover?.asImageBitmap(),
        number = number,
        trailingContent = {
            val view = LocalView.current
            IconButton(onClick = onLongClick) {
                Icon(Icons.Default.MoreVert, null)
            }
            IconButton(
                onClick = { },
                modifier =
                    Modifier.draggableHandle(
                        onDragStarted = {
                            view.performHapticFeedback(
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                                    HapticFeedbackConstants.DRAG_START
                                } else {
                                    HapticFeedbackConstants.VIRTUAL_KEY
                                },
                            )
                        },
                        onDragStopped = {
                            view.performHapticFeedback(
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                                    HapticFeedbackConstants.GESTURE_END
                                } else {
                                    HapticFeedbackConstants.VIRTUAL_KEY_RELEASE
                                },
                            )
                        },
                    ),
            ) {
                Icon(Icons.Default.DragHandle, null)
            }
        },
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LazyItemScope.LazyMusicCardScreen(
    file: MusicCard,
    modifier: Modifier = Modifier,
    number: Int? = null,
    background: Color = Color.Transparent,
    reorderableState: ReorderableLazyListState? = null,
    onLongClick: () -> Unit = {},
    onClick: () -> Unit = {},
) {
    if (reorderableState == null) {
        MusicCardScreen(
            file = file,
            number = number,
            background = background,
            onClick = onClick,
            onLongClick = onLongClick,
            modifier = modifier.animateItem(),
        )
    } else {
        ReorderableItem(
            state = reorderableState,
            key = file.id,
            modifier = modifier.animateItem(),
        ) {
            MusicCardScreen(
                file = file,
                number = number,
                background = background,
                onClick = onClick,
                onLongClick = onLongClick,
            )
        }
    }
}

@Composable
fun LazyItemScope.SwipeMusicCardLazyItem(
    state: SwipeToDismissBoxState,
    file: MusicCard,
    modifier: Modifier = Modifier,
    number: Int? = null,
    background: Color = MaterialTheme.colorScheme.errorContainer,
    swipingItemBackground: Color = MaterialTheme.colorScheme.surface,
    reorderableState: ReorderableLazyListState? = null,
    onLongClick: () -> Unit = {},
    onClick: () -> Unit = {},
) {
    val scale by animateFloatAsState(
        targetValue =
            if (state.dismissDirection == SwipeToDismissBoxValue.EndToStart) {
                1f
            } else {
                0f
            },
        label = "Swipe to delete scale",
    )
    val boxBackground =
        if (state.dismissDirection == SwipeToDismissBoxValue.Settled) {
            Color.Transparent
        } else {
            background
        }
    SwipeToDismissBox(
        state = state,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(boxBackground, MaterialTheme.shapes.medium)
                        .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Icon(
                    Icons.Default.Delete,
                    null,
                    Modifier
                        .fillMaxHeight()
                        .padding(vertical = 2.dp)
                        .scale(scale),
                    MaterialTheme.colorScheme.error,
                )
            }
        },
        modifier = modifier.animateItem(),
    ) {
        LazyMusicCardScreen(
            file,
            Modifier,
            number,
            swipingItemBackground,
            reorderableState,
            onLongClick,
            onClick,
        )
    }
}
