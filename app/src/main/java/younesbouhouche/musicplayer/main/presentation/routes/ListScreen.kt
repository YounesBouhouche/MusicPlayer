package younesbouhouche.musicplayer.main.presentation.routes

import android.graphics.drawable.BitmapDrawable
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Sort
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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.kmpalette.color
import com.kmpalette.rememberPaletteState
import kotlinx.coroutines.launch
import soup.compose.material.motion.animation.materialSharedAxisZIn
import soup.compose.material.motion.animation.materialSharedAxisZOut
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.core.domain.models.MusicCard
import younesbouhouche.musicplayer.main.presentation.components.EmptyContainer
import younesbouhouche.musicplayer.main.presentation.components.LazyMusicCardScreen
import younesbouhouche.musicplayer.main.presentation.components.MyImage
import younesbouhouche.musicplayer.main.presentation.components.SortSheet
import younesbouhouche.musicplayer.main.presentation.util.SortState
import younesbouhouche.musicplayer.main.presentation.util.SortType
import younesbouhouche.musicplayer.main.presentation.util.composables.isScrollingUp
import younesbouhouche.musicplayer.main.presentation.util.plus
import younesbouhouche.musicplayer.main.presentation.util.shareFiles
import younesbouhouche.musicplayer.settings.presentation.components.listItemShape
import younesbouhouche.musicplayer.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.ListScreen(
    files: List<MusicCard>,
    title: String,
    sortState: (SortState<SortType>)?,
    onSortStateChange: ((SortState<SortType>) -> Unit)?,
    navigateUp: () -> Unit,
    onLongClick: (Int) -> Unit,
    onPlay: (index: Int, shuffle: Boolean) -> Unit,
    animatedContentScope: AnimatedContentScope,
    modifier: Modifier = Modifier,
    key: String? = null,
    subtitle: String = pluralStringResource(R.plurals.item_s, files.size, files.size),
    cover: Any? = files.firstOrNull { it.coverUri != null }?.coverUri,
    icon: ImageVector = Icons.AutoMirrored.Filled.List
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    val state = rememberLazyListState()
    val context = LocalContext.current
    val paletteState = rememberPaletteState()
    val scope = rememberCoroutineScope()
    EmptyContainer(
        files.isEmpty(),
        icon,
        stringResource(R.string.no_files_here),
        modifier
    ) {
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
                            sortState?.let {
                                IconButton(onClick = { onSortStateChange?.invoke(it.copy(expanded = true)) }) {
                                    Icon(Icons.AutoMirrored.Default.Sort, null)
                                }
                            }
                        },
                        scrollBehavior = scrollBehavior,
                    )
                },
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
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
                    contentPadding = paddingValues + PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    item {
                        Column(
                            Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                MyImage(
                                    model = cover,
                                    icon = icon,
                                    modifier = Modifier
                                        .sharedElement(
                                            rememberSharedContentState(key = key ?: ""),
                                            animatedVisibilityScope = animatedContentScope
                                        )
                                        .size(100.dp)
                                ) {
                                    scope.launch {
                                        paletteState.generate(
                                            (it.result.drawable as BitmapDrawable)
                                                .bitmap
                                                .asImageBitmap()
                                        )
                                    }
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
                    itemsIndexed(files, { index, it -> it.id }) { index, it ->
                        LazyMusicCardScreen(
                            file = it,
                            onLongClick = { onLongClick(files.indexOf(it)) },
                            shape = listItemShape(index, files.size),
                            background = MaterialTheme.colorScheme.surfaceContainerLow
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
}
