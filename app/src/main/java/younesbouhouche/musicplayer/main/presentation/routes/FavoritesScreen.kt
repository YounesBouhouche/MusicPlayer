package younesbouhouche.musicplayer.main.presentation.routes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.core.domain.models.MusicCard
import younesbouhouche.musicplayer.main.presentation.components.EmptyContainer
import younesbouhouche.musicplayer.main.presentation.components.MusicCardScreen
import younesbouhouche.musicplayer.main.presentation.components.PlaylistListItem
import younesbouhouche.musicplayer.main.presentation.expandableListItem
import younesbouhouche.musicplayer.main.presentation.resultHolder
import younesbouhouche.musicplayer.main.presentation.util.plus
import younesbouhouche.musicplayer.main.presentation.viewmodel.FavoritesViewModel
import younesbouhouche.musicplayer.settings.presentation.components.listItemShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    favoritesViewModel: FavoritesViewModel,
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onPlaylistClick: (Int) -> Unit = { _ -> },
    onPlayPlaylist: (Int) -> Unit = { _ -> },
    onFileClick: (List<MusicCard>, Int) -> Unit = { _, _ -> },
) {
    val files by favoritesViewModel.favorites.collectAsState()
    val playlists by favoritesViewModel.playlists.collectAsState()
    var listExpanded by remember { mutableStateOf(false) }
    var playlistsExpanded by remember { mutableStateOf(false) }
    val expandButtonBackground = MaterialTheme.colorScheme.surfaceContainerLow
    EmptyContainer(
        files.isEmpty() and playlists.isEmpty(),
        Icons.Default.Favorite,
        stringResource(R.string.favorites_empty_message),
        modifier.fillMaxSize()
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                LargeTopAppBar(
                    title = {
                        Text(stringResource(R.string.favorites))
                    },
                    navigationIcon = {
                        IconButton(onBack) {
                            Icon(
                                Icons.AutoMirrored.Default.ArrowBack,
                                null
                            )
                        }
                    }
                )
            },
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = it + PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (files.isNotEmpty()) {
                    resultHolder(
                        null,
                        files,
                        listExpanded,
                        { listExpanded = !listExpanded },
                        expandButtonBackground = expandButtonBackground
                    ) { index, file ->
                        MusicCardScreen(
                            file = file,
                            shape = expandableListItem(index, files.size),
                            background = MaterialTheme.colorScheme.surfaceContainerLow,
                            modifier = Modifier
                                .padding(horizontal = 12.dp)
                                .animateItem()
                        ) {
                            onFileClick(files, index)
                        }
                    }
                }
                if (playlists.isNotEmpty()) {
                    item {
                        Spacer(Modifier.height(4.dp))
                    }
                    item {
                        Label(stringResource(R.string.playlists))
                    }
                    resultHolder(
                        null,
                        playlists,
                        playlistsExpanded,
                        { playlistsExpanded = !playlistsExpanded },
                        expandButtonBackground = expandButtonBackground
                    ) { index, playlist ->
                        PlaylistListItem(
                            playlist,
                            { onPlaylistClick(playlist.id) },
                            {  },
                            {
                                onPlayPlaylist(playlist.id)
                            },
                            Modifier
                                .padding(horizontal = 12.dp)
                                .animateItem(),
                            shape = listItemShape(index, playlists.size),
                            background = MaterialTheme.colorScheme.surfaceContainerLow,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Label(
    text: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null
) {
    Row(
        Modifier
            .padding(16.dp, 12.dp)
            .fillMaxWidth()
            .then(onClick?.let { Modifier.clickable(onClick = onClick) } ?: Modifier),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            modifier = modifier.weight(1f)
        )
        trailingContent?.invoke()
    }
}