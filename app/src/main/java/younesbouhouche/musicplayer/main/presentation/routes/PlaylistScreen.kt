package younesbouhouche.musicplayer.main.presentation.routes

import android.graphics.BitmapFactory
import android.os.Build
import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.kmpalette.color
import com.kmpalette.rememberPaletteState
import kotlinx.coroutines.launch
import sh.calvin.reorderable.rememberReorderableLazyListState
import soup.compose.material.motion.animation.materialSharedAxisZIn
import soup.compose.material.motion.animation.materialSharedAxisZOut
import younesbouhouche.musicplayer.core.domain.models.UiPlaylist
import younesbouhouche.musicplayer.main.presentation.components.PlaylistSortSheet
import younesbouhouche.musicplayer.main.presentation.components.SwipeMusicCardLazyItem
import younesbouhouche.musicplayer.main.presentation.util.composables.isScrollingUp
import younesbouhouche.musicplayer.main.presentation.util.PlaylistSortType
import younesbouhouche.musicplayer.main.presentation.util.SortState
import younesbouhouche.musicplayer.ui.theme.AppTheme
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistScreen(
    playlist: UiPlaylist,
    sortState: SortState<PlaylistSortType>,
    onSortStateChange: (SortState<PlaylistSortType>) -> Unit,
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
    LaunchedEffect(playlist.image) {
        playlist.image?.let {
            paletteState.generate(BitmapFactory.decodeFile(File(context.filesDir, it).absolutePath).asImageBitmap())
        }
    }
    AppTheme(
        paletteState.palette?.vibrantSwatch?.color ?: paletteState.palette?.dominantSwatch?.color
    ) {
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
                        IconButton(onClick = { onSortStateChange(sortState.copy(expanded = true)) }) {
                            Icon(Icons.AutoMirrored.Default.Sort, null)
                        }
                    },
                    scrollBehavior = scrollBehavior,
                )
            },
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
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
                contentPadding = paddingValues
            ) {
                item {
                    Column(Modifier.fillMaxWidth().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                Modifier
                                    .size(100.dp)
                                    .background(MaterialTheme.colorScheme.surfaceContainer, MaterialTheme.shapes.medium)
                                    .clip(MaterialTheme.shapes.medium)
                                    .clipToBounds(),
                                contentAlignment = Alignment.Center
                            ) {
                                if (playlist.image == null) {
                                    Icon(
                                        Icons.AutoMirrored.Filled.PlaylistPlay,
                                        null,
                                        Modifier.size(64.dp),
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                } else {
                                    Image(
                                        rememberAsyncImagePainter(request),
                                        null,
                                        Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop,
                                    )
                                }
                            }
                            Column(
                                Modifier.fillMaxWidth().weight(1f),
                                verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    playlist.name,
                                    style = MaterialTheme.typography.titleLarge,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    "${playlist.items.size} item(s)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.Start)
                                ) {
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
                                }
                            }
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
                    val dismissState =
                        rememberSwipeToDismissBoxState(
                            confirmValueChange = { value ->
                                value == SwipeToDismissBoxValue.EndToStart
                            },
                            positionalThreshold = { it / 1.5f },
                        )
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
                    ) {
                        onPlay(index, false)
                    }
                }
            }
        }
        PlaylistSortSheet(sortState) { onSortStateChange(it) }
    }
}
