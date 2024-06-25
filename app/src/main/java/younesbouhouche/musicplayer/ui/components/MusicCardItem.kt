package younesbouhouche.musicplayer.ui.components

import android.os.Build
import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.CarouselItemScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import sh.calvin.reorderable.ReorderableCollectionItemScope
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.ReorderableLazyListState
import soup.compose.material.motion.MaterialSharedAxisZ
import younesbouhouche.musicplayer.MusicCard
import younesbouhouche.musicplayer.timeString

@Composable
fun MusicCardItem(
    file: MusicCard,
    modifier: Modifier = Modifier,
    background: Color = Color.Transparent,
    trailingContent: @Composable RowScope.() -> Unit = {},
    onLongClick: () -> Unit,
    onClick: () -> Unit,
) {
    MyListItem(
        onClick = onClick,
        onLongClick = onLongClick,
        modifier = modifier.background(background),
        headline = file.title,
        supporting = "${file.artist} - ${file.duration.timeString}",
        cover = file.cover?.asImageBitmap(),
        trailingContent = {
            trailingContent()
            IconButton(onClick = onLongClick) {
                Icon(Icons.Default.MoreVert, null)
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReorderableCollectionItemScope.MusicCardItem(
    file: MusicCard,
    modifier: Modifier = Modifier,
    background: Color = Color.Transparent,
    onLongClick: () -> Unit = {},
    onClick: () -> Unit = {},
) {
    MyListItem(
        modifier = modifier.background(background),
        onLongClick = onLongClick,
        onClick = onClick,
        headline = file.title,
        supporting = "${file.artist} - ${file.duration.timeString}",
        cover = file.cover?.asImageBitmap(),
        trailingContent = {
            val view = LocalView.current
            IconButton(onClick = onLongClick) {
                Icon(Icons.Default.MoreVert, null)
            }
            IconButton(onClick = { }, modifier = Modifier.draggableHandle(
                onDragStarted = {
                    view.performHapticFeedback(
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
                            HapticFeedbackConstants.DRAG_START
                        else
                            HapticFeedbackConstants.VIRTUAL_KEY
                    )
                },
                onDragStopped = {
                    view.performHapticFeedback(
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
                            HapticFeedbackConstants.GESTURE_END
                        else
                            HapticFeedbackConstants.VIRTUAL_KEY_RELEASE
                    )
                })
            ) {
                Icon(Icons.Default.DragHandle, null)
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LazyItemScope.MusicCardLazyItem(
    file: MusicCard,
    modifier: Modifier = Modifier,
    background: Color = Color.Transparent,
    reorderableState: ReorderableLazyListState? = null,
    onLongClick: () -> Unit = {},
    onClick: () -> Unit = {}
) {
    MaterialSharedAxisZ(targetState = reorderableState, forward = true) {
        if (it == null)
            MusicCardItem(
                file = file,
                onClick = onClick,
                onLongClick = onLongClick,
                modifier = modifier.animateItem()
            )
        else
            ReorderableItem(
                state = it,
                key = file.id,
                modifier = Modifier.animateItem()
            ) {
                MusicCardItem(
                    file = file,
                    background = background,
                    onClick = onClick,
                    onLongClick = onLongClick
                )
            }
    }
}

@Composable
fun LazyItemScope.SwipeMusicCardLazyItem(
    state: SwipeToDismissBoxState,
    file: MusicCard,
    modifier: Modifier = Modifier,
    background: Color = Color.Transparent,
    reorderableState: ReorderableLazyListState? = null,
    onLongClick: () -> Unit = {},
    onClick: () -> Unit = {}
) {
    SwipeToDismissBox(
        state = state,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            val scale by animateFloatAsState(
                targetValue =
                if (state.dismissDirection == SwipeToDismissBoxValue.EndToStart) 1f
                else 0f,
                label = "Swipe to delete scale"
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    Icons.Default.Delete,
                    null,
                    Modifier
                        .fillMaxHeight()
                        .padding(vertical = 2.dp)
                        .scale(scale),
                    MaterialTheme.colorScheme.error
                )
            }
        },
        modifier = modifier.animateItem()
    ) {
        MusicCardLazyItem(file, Modifier, background, reorderableState, onLongClick, onClick)
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CarouselItemScope.MusicCardItem(
    file: MusicCard,
    modifier: Modifier = Modifier,
    alpha: Float = 1f,
    icon: ImageVector = Icons.Default.MusicNote,
    onLongClick: () -> Unit = {},
    onClick: () -> Unit = {}
) {
    val shape = rememberMaskShape(shape = MaterialTheme.shapes.extraLarge)
    Box(
        modifier
            .size(200.dp)
            .background(MaterialTheme.colorScheme.inverseOnSurface, shape)
            .clip(shape)
            .clipToBounds()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .alpha(alpha),
        contentAlignment = Alignment.Center) {
        MaterialSharedAxisZ(
            targetState = file.cover,
            forward = true,
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center)
                .zIndex(1f)
        ) {
            if (it == null)
                Box(Modifier.fillMaxSize()) {
                    Icon(
                        icon,
                        null,
                        Modifier
                            .size(90.dp)
                            .align(Alignment.Center),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            else
                Image(
                    bitmap = it.asImageBitmap(),
                    null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
        }
        Column(
            Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        0f to Color.Transparent,
                        .7f to
                                if (isSystemInDarkTheme()) MaterialTheme.colorScheme.background.copy(
                                    .8f
                                )
                                else MaterialTheme.colorScheme.onBackground.copy(.8f),
                        1f to
                                if (isSystemInDarkTheme()) MaterialTheme.colorScheme.background.copy(
                                    .9f
                                )
                                else MaterialTheme.colorScheme.onBackground.copy(.9f)
                    )
                )
                .zIndex(2f)
                .align(Alignment.BottomStart)) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = file.title,
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.bodyLarge,
                color =
                if (isSystemInDarkTheme()) MaterialTheme.colorScheme.onBackground
                else MaterialTheme.colorScheme.background,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = file.artist,
                modifier = Modifier.padding(horizontal = 16.dp),
                style = MaterialTheme.typography.bodySmall,
                color =
                if (isSystemInDarkTheme()) MaterialTheme.colorScheme.onSurfaceVariant
                else MaterialTheme.colorScheme.surfaceVariant,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}