package younesbouhouche.musicplayer.main.presentation.routes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import soup.compose.material.motion.animation.materialSharedAxisZIn
import soup.compose.material.motion.animation.materialSharedAxisZOut
import younesbouhouche.musicplayer.core.domain.models.MusicCard
import younesbouhouche.musicplayer.main.presentation.components.LazyMusicCardScreen
import younesbouhouche.musicplayer.main.presentation.components.LazyVerticalGridWithHeader
import younesbouhouche.musicplayer.main.presentation.components.SortSheet
import younesbouhouche.musicplayer.main.presentation.util.composables.isScrollingUp
import younesbouhouche.musicplayer.main.presentation.util.SortState
import younesbouhouche.musicplayer.main.presentation.util.SortType
import younesbouhouche.musicplayer.main.presentation.util.composables.isScrollingUp
import younesbouhouche.musicplayer.main.presentation.util.shareFiles
import younesbouhouche.musicplayer.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    files: List<MusicCard>,
    title: String,
    sortState: (SortState<SortType>)?,
    onSortStateChange: ((SortState<SortType>) -> Unit)?,
    navigateUp: () -> Unit,
    onLongClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    onClick: (Int) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val state = rememberLazyListState()
    val context = LocalContext.current
    val paletteState = rememberPaletteState()
    val scope = rememberCoroutineScope()
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
                        sortState?.let {
                            IconButton(onClick = { onSortStateChange?.invoke(it.copy(expanded = true)) }) {
                                Icon(Icons.AutoMirrored.Default.Sort, null)
                            }
                        }
                    },
                    scrollBehavior = scrollBehavior,
                )
            },
            modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            floatingActionButton = {
                AnimatedVisibility(
                    visible = state.isScrollingUp() and files.isNotEmpty(),
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
                    Column(
                        Modifier.fillMaxWidth().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                Modifier
                                    .size(100.dp)
                                    .background(
                                        MaterialTheme.colorScheme.surfaceContainer,
                                        MaterialTheme.shapes.medium
                                    )
                                    .clip(MaterialTheme.shapes.medium)
                                    .clipToBounds(),
                                contentAlignment = Alignment.Center
                            ) {
                                SubcomposeAsyncImage(
                                    model = cover,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop,
                                    onSuccess = {
                                        scope.launch {
                                            paletteState.generate(
                                                (it.result.drawable as BitmapDrawable)
                                                .bitmap
                                                .asImageBitmap()
                                            )
                                        }
                                    },
                                    error = {
                                        Box(
                                            Modifier.fillMaxSize(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                icon,
                                                null,
                                                Modifier.size(64.dp),
                                                MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                )
                            }
                            Column(
                                Modifier.fillMaxWidth().weight(1f),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    title,
                                    style = MaterialTheme.typography.titleLarge,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    subtitle,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Row(
                                    Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(
                                        4.dp,
                                        Alignment.Start
                                    )
                                ) {
                                    IconButton({ context.shareFiles(files) }) {
                                        Icon(Icons.Default.Share, null)
                                    }
                                }
                            }
                        }
                        Row(
                            Modifier.fillMaxWidth().padding(bottom = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button({ onPlay(0, false) }, Modifier.weight(1f)) {
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
                items(files, { it.id }) {
                    LazyMusicCardScreen(
                        file = it,
                        onLongClick = { onLongClick(files.indexOf(it)) },
                    ) {
                        onPlay(files.indexOf(it), false)
                    }
                }
            }
        }
        sortState?.let { state ->
            SortSheet(state) { onSortStateChange?.invoke(it) }
        }
    }
}
