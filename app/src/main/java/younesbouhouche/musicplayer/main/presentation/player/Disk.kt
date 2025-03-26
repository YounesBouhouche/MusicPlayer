package younesbouhouche.musicplayer.main.presentation.player

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import younesbouhouche.musicplayer.main.domain.events.PlayerEvent
import younesbouhouche.musicplayer.core.domain.models.MusicCard
import kotlin.math.absoluteValue

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Disk(
    queue: List<MusicCard>,
    index: Int,
    playing: Boolean,
    pagerState: PagerState,
    onPlayerEvent: (PlayerEvent) -> Unit,
) {
    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize(),
        key = { queue[it].id },
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
            targetValue = if (playing and (index == page)) animatedScale else 1f,
            label = "",
        )
        val pageOffset =
            (
                (pagerState.currentPage - page) +
                    pagerState
                        .currentPageOffsetFraction
            ).absoluteValue
        with(queue[page]) {
            val fav by favorite.collectAsState(false)
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
                                onPlayerEvent(PlayerEvent.UpdateFavorite(path, true))
                            },
                        ) {},
                    contentAlignment = Alignment.Center,
                ) {
                    if (cover != null) {
                        Image(
                            bitmap = cover!!.asImageBitmap(),
                            null,
                            contentScale = ContentScale.Crop,
                            modifier =
                                Modifier
                                    .fillMaxSize(),
                        )
                    } else {
                        Box(
                            Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.secondary),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                Icons.Default.MusicNote,
                                null,
                                Modifier.fillMaxSize(.75f),
                                tint = NavigationBarDefaults.containerColor,
                            )
                        }
                    }
                }
                LargeFloatingActionButton(
                    onClick = { onPlayerEvent(PlayerEvent.UpdateFavorite(path, !fav)) },
                    modifier =
                        Modifier
                            .align(Alignment.BottomStart)
                            .offset(
                                12.dp,
                                (-12).dp,
                            ),
                ) {
                    AnimatedContent(targetState = fav, label = "") {
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
