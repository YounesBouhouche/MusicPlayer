package younesbouhouche.musicplayer.features.main.presentation.routes.album

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import younesbouhouche.musicplayer.core.domain.models.Song
import younesbouhouche.musicplayer.features.main.presentation.components.ListScreen

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AlbumScreen(
    album: String,
    modifier: Modifier = Modifier,
    bottomPadding: Dp = 0.dp,
    onShowBottomSheet: (Song) -> Unit = {},
) {
    val viewModel: AlbumViewModel = koinViewModel (
        parameters = { parametersOf(album) }
    )
    val album by viewModel.album.collectAsStateWithLifecycle()
    val sortState by viewModel.sortState.collectAsStateWithLifecycle()
    ListScreen(
        title = album.name,
        cover = album.cover,
        icon = Icons.Default.Person,
        items = album.songs,
        modifier = modifier,
        onPlay = { songs, index, shuffle ->
            viewModel.play(songs.map { it.id }, index, shuffle)
        },
        sortState = sortState,
        onSortStateChange = viewModel::setSortState,
        onShowBottomSheet = onShowBottomSheet,
        contentPadding = PaddingValues(bottom = bottomPadding)
    )
}