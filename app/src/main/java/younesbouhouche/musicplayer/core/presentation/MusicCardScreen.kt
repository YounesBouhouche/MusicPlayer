package younesbouhouche.musicplayer.core.presentation

import android.os.Build
import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import sh.calvin.reorderable.ReorderableCollectionItemScope
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.ReorderableLazyListState
import younesbouhouche.musicplayer.main.domain.models.MusicCard
import younesbouhouche.musicplayer.main.presentation.util.timeString

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
