package younesbouhouche.musicplayer.ui.routes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import soup.compose.material.motion.animation.materialSharedAxisZIn
import soup.compose.material.motion.animation.materialSharedAxisZOut
import younesbouhouche.musicplayer.MusicCard
import younesbouhouche.musicplayer.SortEvent
import younesbouhouche.musicplayer.isScrollingUp
import younesbouhouche.musicplayer.states.SortState
import younesbouhouche.musicplayer.ui.components.LazyColumnWithHeader
import younesbouhouche.musicplayer.ui.components.LazyColumnWithSortBar
import younesbouhouche.musicplayer.ui.components.MusicCardLazyItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    files: List<MusicCard>,
    title: String,
    sortState: SortState?,
    onSortEvent: ((SortEvent) -> Unit)?,
    navigateUp: () -> Unit,
    onLongClick: (Int) -> Unit,
    onClick: (Int) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    val state = rememberLazyListState()
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text(title) },
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
                exit = materialSharedAxisZOut(true)
            ) {
                FloatingActionButton(onClick = { onClick(0) }) {
                    Icon(Icons.Default.PlayArrow, null)
                }
            }
        }
    ) { paddingValues ->
        if ((sortState != null) and (onSortEvent != null))
            LazyColumnWithSortBar(
                state = state,
                sortState = sortState!!,
                onSortEvent = onSortEvent!!,
                searchBarSpace = false,
                contentPadding = paddingValues) {
                items(files, { it.id }) {
                    MusicCardLazyItem(
                        file = it,
                        onLongClick = { onLongClick(files.indexOf(it)) }
                    ) {
                        onClick(files.indexOf(it))
                    }
                }
            }
        else
            LazyColumnWithHeader(
                state = state,
                leadingContent = {},
                searchBarSpace = false,
                contentPadding = paddingValues,) {
                items(files, { it.id }) {
                    MusicCardLazyItem(
                        file = it,
                        onLongClick = { onLongClick(files.indexOf(it)) }
                    ) {
                        onClick(files.indexOf(it))
                    }
                }
            }
    }
}