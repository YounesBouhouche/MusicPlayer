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
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        text = title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                actions = {
                    sortState?.let {
                        IconButton(onClick = { onSortStateChange?.invoke(it.copy(expanded = true)) }) {
                            Icon(Icons.AutoMirrored.Default.Sort, null)
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = navigateUp) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, null)
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
                FloatingActionButton(onClick = { onClick(0) }) {
                    Icon(Icons.Default.PlayArrow, null)
                }
            }
        },
    ) { paddingValues ->
        LazyVerticalGridWithHeader(
            GridCells.Fixed(1),
            modifier,
            contentPadding = paddingValues,
            statusBarSpace = false,
            searchBarSpace = false
        ) {
            items(files, { it.id }) {
                LazyMusicCardScreen(
                    file = it,
                    onLongClick = { onLongClick(files.indexOf(it)) },
                ) {
                    onClick(files.indexOf(it))
                }
            }
        }
    }
    sortState?.let { state ->
        SortSheet(state) { onSortStateChange?.invoke(it) }
    }
}
