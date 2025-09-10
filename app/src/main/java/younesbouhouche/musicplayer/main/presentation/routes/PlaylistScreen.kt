package younesbouhouche.musicplayer.main.presentation.routes

import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.request.ImageRequest
import com.kmpalette.color
import com.kmpalette.rememberPaletteState
import kotlinx.coroutines.launch
import sh.calvin.reorderable.rememberReorderableLazyListState
import soup.compose.material.motion.animation.materialSharedAxisZIn
import soup.compose.material.motion.animation.materialSharedAxisZOut
import younesbouhouche.musicplayer.core.domain.models.UiPlaylist
import younesbouhouche.musicplayer.main.presentation.components.MyImage
import younesbouhouche.musicplayer.main.presentation.components.PlaylistSortSheet
import younesbouhouche.musicplayer.main.presentation.components.SwipeMusicCardLazyItem
import younesbouhouche.musicplayer.main.presentation.util.PlaylistSortType
import younesbouhouche.musicplayer.main.presentation.util.SortState
import younesbouhouche.musicplayer.main.presentation.util.composables.isScrollingUp
import younesbouhouche.musicplayer.main.presentation.util.plus
import younesbouhouche.musicplayer.settings.presentation.components.listItemShape
import younesbouhouche.musicplayer.ui.theme.AppTheme
import java.io.File

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.PlaylistScreen(
    playlist: UiPlaylist,
    sortState: SortState<PlaylistSortType>,
    onSortStateChange: (SortState<PlaylistSortType>) -> Unit,
    animatedContentScope: AnimatedContentScope,
    modifier: Modifier = Modifier,
    navigateUp: () -> Unit = {},
    onRename: () -> Unit = {},
    onSetFavorite: (Boolean) -> Unit = {},
    onShare: () -> Unit = {},
    reorder: (Int, Int) -> Unit = { _, _ -> },
    onDismiss: (Int) -> Unit = {},
    onLongClick: (Int) -> Unit = {},
    onPlay: (index: Int, shuffle: Boolean) -> Unit = { _, _ -> },
) {
    val context = LocalContext.current
    val file = playlist.image?.let { File(context.filesDir, it) }
    val request =
        ImageRequest.Builder(context)
            .data(file)
            .build()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val state = rememberLazyListState()
    val view = LocalView.current
    val reorderableState =
        if ((sortState.ascending) and (sortState.sortType == PlaylistSortType.Custom)) {
            rememberReorderableLazyListState(lazyListState = state) { from, to ->
                view.performHapticFeedback(
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                        HapticFeedbackConstants.SEGMENT_FREQUENT_TICK
                    } else {
                        HapticFeedbackConstants.GESTURE_END
                    },
                )
                reorder(from.index - 1, to.index - 1)
            }
        } else {
            null
        }
    val paletteState = rememberPaletteState()
    val scope = rememberCoroutineScope()
    AppTheme(paletteState.palette) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {},
                    navigationIcon = {
                        IconButton(onClick = navigateUp) {
                            Icon(Icons.AutoMirrored.Default.ArrowBack, null)
                        }
                    },
                    actions = {
                        IconButton({ onSetFavorite(!playlist.favorite) }) {
                            AnimatedContent(playlist.favorite) {
                                if (it) Icon(Icons.Default.Favorite, null)
                                else Icon(Icons.Default.FavoriteBorder, null)
                            }
                        }
                        IconButton(onShare) {
                            Icon(Icons.Default.Share, null)
                        }
                        IconButton(onRename) {
                            Icon(Icons.Default.Edit, null)
                        }
                        IconButton(onClick = { onSortStateChange(sortState.copy(expanded = true)) }) {
                            Icon(Icons.AutoMirrored.Default.Sort, null)
                        }
                    },
                    scrollBehavior = scrollBehavior,
                )
            },
            modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            floatingActionButton = {
                AnimatedVisibility(
                    visible = state.isScrollingUp(),
                    enter = materialSharedAxisZIn(true),
                    exit = materialSharedAxisZOut(true),
                ) {
                    FloatingActionButton(onClick = { onPlay(0, false) }) {
                        Icon(Icons.Default.PlayArrow, null)
                    }
                }
            },
        ) { paddingValues ->
            LazyColumn(
                state = state,
                contentPadding = paddingValues + PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                item {
                    Column(
                        Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        MyImage(
                            model = request,
                            icon = Icons.AutoMirrored.Filled.PlaylistPlay,
                            modifier = Modifier
                                .sharedElement(
                                    rememberSharedContentState(key = "playlist-${playlist.id}"),
                                    animatedVisibilityScope = animatedContentScope
                                )
                                .aspectRatio(1f)
                                .fillMaxWidth()
                        ) {
                            scope.launch {
                                paletteState.generate(
                                    (it.result.drawable as BitmapDrawable).bitmap.asImageBitmap()
                                )
                            }
                        }
                        Column(
                            Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                playlist.name,
                                style = MaterialTheme.typography.titleLarge,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                "${playlist.items.size} item(s)",
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth(),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Row(Modifier.fillMaxWidth().padding(bottom = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                { onPlay(0, false) },
                                Modifier.weight(1f)
                            ) {
                                Icon(
                                    Icons.Default.PlayArrow,
                                    null,
                                    Modifier.size(ButtonDefaults.IconSize)
                                )
                                Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                                Text("Play")
                            }
                            OutlinedButton(
                                { onPlay(0, true) },
                                Modifier.weight(1f)
                            ) {
                                Icon(
                                    Icons.Default.Shuffle,
                                    null,
                                    Modifier.size(ButtonDefaults.IconSize)
                                )
                                Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                                Text("Shuffle")
                            }
                        }
                    }
                }
                itemsIndexed(playlist.items, { index, file -> file.id }) { index, file ->
                    val dismissState = rememberSwipeToDismissBoxState {
                        it / 1.5f
                    }
                    if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) {
                        onDismiss(index)
                        LaunchedEffect(Unit) {
                            launch { dismissState.reset() }
                        }
                    }
                    SwipeMusicCardLazyItem(
                        state = dismissState,
                        file = file,
                        reorderableState = reorderableState,
                        onLongClick = { onLongClick(index) },
                        shape = listItemShape(index, playlist.items.size),
                        background = MaterialTheme.colorScheme.surfaceContainerLow,
                    ) {
                        onPlay(index, false)
                    }
                }
            }
        }
        PlaylistSortSheet(sortState) { onSortStateChange(it) }
    }
}
