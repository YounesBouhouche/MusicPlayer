package younesbouhouche.musicplayer.main.presentation

import android.content.Intent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import soup.compose.material.motion.animation.materialSharedAxisZ
import soup.compose.material.motion.animation.materialSharedAxisZIn
import soup.compose.material.motion.animation.materialSharedAxisZOut
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.core.domain.models.Album
import younesbouhouche.musicplayer.core.domain.models.Artist
import younesbouhouche.musicplayer.core.domain.models.MusicCard
import younesbouhouche.musicplayer.core.domain.models.Playlist
import younesbouhouche.musicplayer.core.domain.models.SearchFilter
import younesbouhouche.musicplayer.main.domain.events.SearchEvent
import younesbouhouche.musicplayer.main.domain.models.LoadingState
import younesbouhouche.musicplayer.main.presentation.components.EmptyContainer
import younesbouhouche.musicplayer.main.presentation.components.LazyMusicCardScreen
import younesbouhouche.musicplayer.main.presentation.components.MyListItem
import younesbouhouche.musicplayer.main.presentation.states.SearchState
import younesbouhouche.musicplayer.main.presentation.util.intUpDownTransSpec
import younesbouhouche.musicplayer.main.presentation.util.plus
import younesbouhouche.musicplayer.settings.presentation.SettingsActivity
import younesbouhouche.musicplayer.settings.presentation.components.listItemShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    state: SearchState,
    showAppName: Boolean,
    loadingState: LoadingState,
    onSearchEvent: (SearchEvent) -> Unit,
    onPlay: (Int) -> Unit,
    onAlbumClick: (Album) -> Unit,
    onArtistClick: (Artist) -> Unit,
    onPlaylistClick: (Playlist) -> Unit,
    modifier: Modifier = Modifier,
    sortButton: (@Composable RowScope.() -> Unit)? = null,
    showBottomSheet: (MusicCard) -> Unit,
) {
    val context = LocalContext.current
    val padding by animateDpAsState(targetValue = if (state.expanded) 0.dp else 8.dp, label = "")
    val loadingValue = (
            loadingState.step
                    + (loadingState.progress / loadingState.progressMax.toFloat())
            ) / loadingState.stepsCount
    val animatedProgress by animateFloatAsState(
        targetValue = loadingValue,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
    )
    val isLoading = loadingValue < 1f
    Box(modifier) {
        SearchBar(
            inputField = {
                SearchBarDefaults.InputField(
                    query = state.query,
                    onQueryChange = { onSearchEvent(SearchEvent.UpdateQuery(it)) },
                    onSearch = { onSearchEvent(SearchEvent.UpdateQuery(it)) },
                    expanded = state.expanded,
                    placeholder = {
                        AnimatedContent(showAppName) {
                            if (it)
                                AppNameLabel(modifier = Modifier.fillMaxWidth())
                            else
                                AnimatedContent(isLoading) { loading ->
                                    if (loading)
                                        AnimatedContent(
                                            loadingState.step,
                                            transitionSpec = intUpDownTransSpec,
                                            modifier = Modifier.padding(start = 3.dp)
                                        ) { step ->
                                            Text(
                                                stringResource(
                                                    when(step) {
                                                            0 -> R.string.loading_files
                                                            1 -> R.string.loading_thumbnails
                                                            2 -> R.string.loading_artists
                                                            else -> R.string.loading
                                                        },
                                                    loadingState.progress,
                                                    loadingState.progressMax
                                                )
                                            )
                                        }
                                    else Text(stringResource(R.string.search))
                                }
                        }
                    },
                    leadingIcon = {
                        AnimatedContent(
                            isLoading,
                            transitionSpec = {
                                materialSharedAxisZ(true)
                            }
                        ) {
                            if (it)
                                CircularProgressIndicator(
                                    progress = { animatedProgress },
                                    modifier = Modifier.size(32.dp),
                                    strokeWidth = 3.dp
                                )
                            else {
                                if (state.expanded) {
                                    IconButton(onClick = { onSearchEvent(SearchEvent.Collapse) }) {
                                        Icon(Icons.AutoMirrored.Default.ArrowBack, null)
                                    }
                                } else {
                                    IconButton(onClick = { onSearchEvent(SearchEvent.Expand) }) {
                                        Icon(Icons.Default.Search, null)
                                    }
                                }
                            }
                        }
                    },
                    trailingIcon = {
                        AnimatedVisibility(
                            !isLoading,
                            enter = materialSharedAxisZIn(true),
                            exit = materialSharedAxisZOut(true),
                        ) {
                            Row {
                                IconButton(onClick = {
                                    context.startActivity(Intent(
                                        context,
                                        SettingsActivity::class.java
                                    )) }) {
                                    Icon(Icons.Default.Settings, null)
                                }
                                sortButton?.invoke(this)
                            }
                        }
                    },
                    onExpandedChange = { onSearchEvent(SearchEvent.UpdateExpanded(it)) },
                )
            },
            expanded = state.expanded,
            onExpandedChange = { onSearchEvent(SearchEvent.UpdateExpanded(it)) },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(padding),
        ) {
            LazyRow(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(8.dp)
            ) {
                items(SearchFilter.entries) {
                    FilterChip(
                        selected = it in state.result.filters,
                        onClick = { onSearchEvent(SearchEvent.ToggleFilter(it)) },
                        label = { Text(stringResource(it.label)) },
                        leadingIcon = { Icon(it.icon, null) },
                    )
                }
            }
            EmptyContainer(
                state.query.isBlank(),
                Icons.Default.Search,
                stringResource(R.string.type_to_search),
                Modifier
                    .weight(1f)
                    .imePadding()
            ) {

                EmptyContainer(
                    state.result.isEmpty(),
                    Icons.Default.Search,
                    stringResource(R.string.empty_result),
                    Modifier.fillMaxSize()
                ) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = WindowInsets.navigationBars.asPaddingValues() +
                                PaddingValues(bottom = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.Top)
                    ) {
                        resultHolder(
                            label = R.string.files,
                            items = state.result.files,
                            leadingSpace = true,
                            expanded = state.filesExpanded,
                            onExpandedChange = {
                                onSearchEvent(
                                    SearchEvent.UpdateResultExpanded(files = it)
                                )
                                               },
                        ) { index, file ->
                            LazyMusicCardScreen(
                                file = file,
                                modifier = Modifier
                                    .padding(horizontal = 12.dp)
                                    .animateItem(),
                                shape = expandableListItem(index, state.result.files.size),
                                background = MaterialTheme.colorScheme.surface,
                                onLongClick = {
                                    showBottomSheet(file)
                                }
                            ) {
                                onPlay(index)
                            }
                        }
                        resultHolder(
                            label = R.string.albums,
                            items = state.result.albums,
                            expanded = state.albumsExpanded,
                            onExpandedChange = {
                                onSearchEvent(
                                    SearchEvent.UpdateResultExpanded(albums = it)
                                )
                            }
                        ) { index, album ->
                            MyListItem(
                                headline = album.name,
                                supporting = pluralStringResource(
                                    R.plurals.item_s,
                                    album.items.size,
                                    album.items.size
                                ),
                                shape = expandableListItem(index, state.result.albums.size),
                                background = MaterialTheme.colorScheme.surface,
                                cover = album.cover,
                                alternative = Icons.Default.Album,
                                modifier = Modifier.padding(horizontal = 12.dp).animateItem(),
                                onClick = {
                                    onAlbumClick(album)
                                }
                            )
                        }
                        resultHolder(
                            label = R.string.artists,
                            items = state.result.artists,
                            expanded = state.artistsExpanded,
                            onExpandedChange = {
                                onSearchEvent(
                                    SearchEvent.UpdateResultExpanded(artists = it)
                                )
                            }
                        ) { index, artist ->
                            MyListItem(
                                headline = artist.name,
                                supporting = pluralStringResource(
                                    R.plurals.item_s,
                                    artist.items.size,
                                    artist.items.size
                                ),
                                cover = artist.getPicture(),
                                alternative = Icons.Default.Person,
                                shape = expandableListItem(index, state.result.artists.size),
                                background = MaterialTheme.colorScheme.surface,
                                modifier = Modifier.padding(horizontal = 12.dp).animateItem(),
                                onClick = {
                                    onArtistClick(artist)
                                }
                            )
                        }
                        resultHolder(
                            label = R.string.playlists,
                            items = state.result.playlists,
                            expanded = state.playlistsExpanded,
                            onExpandedChange = {
                                onSearchEvent(
                                    SearchEvent.UpdateResultExpanded(playlists = it)
                                )
                            }
                        ) { index, playlist ->
                            MyListItem(
                                headline = playlist.name,
                                supporting = pluralStringResource(
                                    R.plurals.item_s,
                                    playlist.items.size,
                                    playlist.items.size
                                ),
                                cover = playlist.image,
                                alternative = Icons.AutoMirrored.Filled.PlaylistPlay,
                                shape = expandableListItem(index, state.result.playlists.size),
                                background = MaterialTheme.colorScheme.surface,
                                modifier = Modifier.padding(horizontal = 12.dp).animateItem(),
                                onClick = {
                                    onPlaylistClick(playlist)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppNameLabel(modifier: Modifier = Modifier) {
    Row(
        modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)
    ) {
        Text(
            text = stringResource(R.string.app_name_first),
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = stringResource(R.string.app_name_second),
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
fun expandableListItem(index: Int, itemsCount: Int) = listItemShape(
    index = index,
    itemsCount = if (itemsCount > 3) itemsCount + 1 else itemsCount
)

inline fun <T> LazyListScope.resultHolder(
    label: Int?,
    items: List<T>,
    expanded: Boolean = true,
    crossinline onExpandedChange: (Boolean) -> Unit,
    leadingSpace: Boolean = true,
    expandButtonBackground: Color = Color.Unspecified,
    noinline itemKey: ((Int, T) -> Any)? = null,
    crossinline itemContent: @Composable LazyItemScope.(Int, T) -> Unit,
) {
    if (items.isNotEmpty()) {
        item {
            Column {
                if (leadingSpace) Spacer(Modifier.height(8.dp))
                label?.let {
                    Text(
                        text = stringResource(label),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }
        }
        itemsIndexed(
            items.filterIndexed { index, it -> (expanded) or (index < 3) },
            itemKey,
            itemContent = itemContent
        )
        if (items.size > 3)
            item {
                Button(
                    { onExpandedChange(!expanded) },
                    shape = listItemShape(1, 2),
                    colors = ButtonDefaults.buttonColors(
                        containerColor =
                            expandButtonBackground.takeIf { it != Color.Unspecified }
                                ?: MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                ) {
                    Icon(
                        if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        null,
                    )
                    Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                    Text(
                        stringResource(if (expanded) R.string.show_less else R.string.show_more)
                    )
                }
            }
    }
}