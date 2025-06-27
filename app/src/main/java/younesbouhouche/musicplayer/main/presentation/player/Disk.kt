package younesbouhouche.musicplayer.main.presentation.player

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.snapping.SnapPosition
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import coil.compose.SubcomposeAsyncImage
import younesbouhouche.musicplayer.main.domain.models.QueueModel
import kotlin.math.absoluteValue

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Disk(
    enabled: Boolean,
    queue: QueueModel,
    playing: Boolean,
    pagerState: PagerState,
    onUpdateFavorite: (String, Boolean) -> Unit,
) {
    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize(),
        snapPosition = SnapPosition.Center,
        key = { queue.items[it].id },
        userScrollEnabled = enabled
    ) { page ->
        val transition = rememberInfiniteTransition(label = "Playing animation")
        val animatedScale by transition.animateFloat(
            initialValue = 0.9f,
            targetValue = 1f,
            animationSpec =
                infiniteRepeatable(
                    animation = tween(5000),
                    repeatMode = RepeatMode.Reverse,
                ),
            label = "Scale animation",
        )
        val diskScale by animateFloatAsState(
            targetValue = if (playing and (queue.index == page)) animatedScale else 1f,
            label = "",
        )
        val pageOffset =
            (
                (pagerState.currentPage - page) +
                    pagerState
                        .currentPageOffsetFraction
            ).absoluteValue
        with(queue.items[page]) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                        .scale(
                            lerp(
                                0.75f,
                                1f,
                                1f - pageOffset.coerceIn(0f, 1f),
                            ),
                        ),
            ) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                        .align(Alignment.Center)
                        .scale(diskScale)
                        .clip(CircleShape)
                        .clipToBounds()
                        .combinedClickable(
                            onDoubleClick = {
                                onUpdateFavorite(path, true)
                            },
                        ) {},
                    contentAlignment = Alignment.Center,
                ) {
                    SubcomposeAsyncImage(
                        model = coverUri,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        error = {
                            Box(Modifier
                                .background(MaterialTheme.colorScheme.secondary)
                                .fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.MusicNote,
                                    null,
                                    Modifier.fillMaxSize(.75f),
                                    tint = NavigationBarDefaults.containerColor,
                                )
                            }
                        }
                    )
                }
                LargeFloatingActionButton(
                    onClick = { onUpdateFavorite(path, !favorite) },
                    modifier =
                        Modifier
                            .align(Alignment.BottomStart)
                            .offset(
                                12.dp,
                                (-12).dp,
                            ),
                ) {
                    AnimatedContent(targetState = favorite, label = "") {
                        if (it) {
                            Icon(
                                Icons.Default.Favorite,
                                null,
                                Modifier.size(ButtonDefaults.IconSize * 2),
                            )
                        } else {
                            Icon(
                                Icons.Default.FavoriteBorder,
                                null,
                                Modifier.size(ButtonDefaults.IconSize * 2),
                            )
                        }
                    }
                }
            }
        }
    }
}
