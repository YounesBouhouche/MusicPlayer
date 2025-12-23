package younesbouhouche.musicplayer.features.main.presentation.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.AllInbox
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.AppBarWithSearch
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.younesb.mydesignsystem.presentation.components.ExpressiveIconButton
import com.younesb.mydesignsystem.presentation.components.ExpressiveToggleButton
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.core.domain.models.Album
import younesbouhouche.musicplayer.core.domain.models.Artist
import younesbouhouche.musicplayer.core.domain.models.Playlist
import younesbouhouche.musicplayer.core.domain.models.Song
import younesbouhouche.musicplayer.features.main.domain.events.SearchAction
import younesbouhouche.musicplayer.features.main.presentation.SearchFilter
import younesbouhouche.musicplayer.features.main.presentation.components.EmptyContainer
import younesbouhouche.musicplayer.features.main.presentation.components.ListItem
import younesbouhouche.musicplayer.features.main.presentation.components.SongListItem
import younesbouhouche.musicplayer.features.main.presentation.states.isEmpty
import com.younesb.mydesignsystem.presentation.util.plus
import younesbouhouche.musicplayer.features.main.presentation.util.searchBarIconButtonColors
import younesbouhouche.musicplayer.features.main.presentation.viewmodel.SearchVM
import younesbouhouche.musicplayer.features.settings.presentation.components.listItemShape

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SearchScreen(
    onShowBottomSheet: (Song) -> Unit,
    onAlbumClick: (Album) -> Unit,
    onArtistClick: (Artist) -> Unit,
    onPlaylistClick: (Playlist) -> Unit,
    navigateToSettings: () -> Unit,
) {
    val viewModel = koinViewModel<SearchVM>()
    val searchState by viewModel.searchState.collectAsState()
    val state = rememberSearchBarState(initialValue = SearchBarValue.Collapsed)
    val textFieldState = rememberTextFieldState()
    val scope = rememberCoroutineScope()
    LaunchedEffect(state.currentValue) {
        if (state.currentValue == SearchBarValue.Collapsed) {
            viewModel.onAction(SearchAction.ClearQuery)
            textFieldState.clearText()
        }
    }
    LaunchedEffect(textFieldState.text) {
        if (textFieldState.text.isEmpty())
            viewModel.onAction(SearchAction.ClearQuery)
    }
    val inputField: @Composable (Boolean) -> Unit =
        @Composable {
            val color =
                if (it)
                    MaterialTheme.colorScheme.onSurfaceVariant
                else
                    MaterialTheme.colorScheme.tertiary
            SearchBarDefaults.InputField(
                searchBarState = state,
                textFieldState = textFieldState,
                colors = SearchBarDefaults.inputFieldColors(
                    unfocusedPlaceholderColor = color,
                    focusedPlaceholderColor = color,
                ),
                leadingIcon = if (state.currentValue == SearchBarValue.Expanded) {
                    {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            ExpressiveIconButton(
                                Icons.AutoMirrored.Default.ArrowBack,
                                size = IconButtonDefaults.mediumIconSize,
                                colors = searchBarIconButtonColors()
                            ) {
                                scope.launch {
                                    state.animateToCollapsed()
                                }
                            }
                        }
                    }
                } else null,
                trailingIcon = if (state.currentValue == SearchBarValue.Expanded) {
                    {
                        AnimatedVisibility(
                            visible = textFieldState.text.isNotEmpty(),
                            enter = expandHorizontally(expandFrom = Alignment.End),
                            exit = shrinkHorizontally(shrinkTowards = Alignment.End),
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.padding(horizontal = 4.dp)
                            ) {
                                ExpressiveIconButton(
                                    Icons.Default.Clear,
                                    size = IconButtonDefaults.mediumIconSize,
                                    colors = searchBarIconButtonColors()
                                ) {
                                    textFieldState.clearText()
                                }
                                ExpressiveIconButton(
                                    Icons.Default.Search,
                                    size = IconButtonDefaults.mediumIconSize,
                                    colors = IconButtonDefaults.filledTonalIconButtonColors()
                                ) {
                                    viewModel.onAction(SearchAction.UpdateQuery("${textFieldState.text}"))
                                }
                            }
                        }
                    }
                } else null,
                onSearch = {
                    viewModel.onAction(SearchAction.UpdateQuery("${textFieldState.text}"))
                },
                placeholder = {
                    Text(
                        stringResource(R.string.search_placeholder),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                },
            )
        }
    AppBarWithSearch(
        state = state,
        inputField = {
            inputField(false)
        },
        colors = SearchBarDefaults.appBarWithSearchColors(
            appBarContainerColor = Color.Transparent,
            searchBarColors = SearchBarDefaults.colors(
                MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = .6f)
            )
        ),
        contentPadding = PaddingValues(8.dp, 8.dp),
        actions = {
            ExpressiveIconButton(
                Icons.Outlined.Settings,
                size = IconButtonDefaults.mediumIconSize,
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                ),
                onClick = navigateToSettings
            )
        }
    )
    if (state.currentValue == SearchBarValue.Expanded)
        ExpandedFullScreenSearchBar(
            state = state,
            inputField = {
                inputField(true)
            },
        ) {
            Column(
                Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                LazyRow(
                    contentPadding = PaddingValues(12.dp, 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(SearchFilter.entries, { it.name }) {
                        ExpressiveToggleButton(
                            checked = searchState.result.filters.contains(it),
                            text = {
                                Text(stringResource(it.label))
                            },
                            icon = it.icon,
                            outlined = true,
                            size = 40.dp
                        ) { _ ->
                            viewModel.onAction(
                                SearchAction.ToggleFilter(it)
                            )
                        }
                    }
                }
                EmptyContainer(
                    searchState.query.isEmpty(),
                    icon = Icons.Default.Search,
                    text = stringResource(R.string.search_start_prompt),
                    modifier = Modifier.fillMaxSize().weight(1f)
                ) {
                    EmptyContainer(
                        searchState.isEmpty,
                        icon = Icons.Default.AllInbox,
                        text = stringResource(R.string.search_no_results)
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = WindowInsets.navigationBars.asPaddingValues() +
                                    PaddingValues(bottom = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.Top)
                        ) {
                            resultHolder(
                                label = R.string.files,
                                items = searchState.result.files,
                                leadingSpace = true,
                                expanded = searchState.filesExpanded,
                                onExpandedChange = {
                                    viewModel.onAction(SearchAction.UpdateResultExpanded(files = it))
                                },
                            ) { index, file ->
                                SongListItem(
                                    song = file,
                                    modifier = Modifier
                                        .padding(horizontal = 12.dp)
                                        .animateItem(),
                                    shape = expandableListItem(index, searchState.result.files.size),
                                    onLongClick = {
                                        onShowBottomSheet(file)
                                    }
                                ) {
                                    viewModel.play(
                                        searchState.result.files.map { it.id },
                                        index
                                    )
                                }
                            }
                            resultHolder(
                                label = R.string.albums,
                                items = searchState.result.albums,
                                expanded = searchState.albumsExpanded,
                                onExpandedChange = {
                                    viewModel.onAction(SearchAction.UpdateResultExpanded(albums = it))
                                }
                            ) { index, album ->
                                ListItem(
                                    headline = album.name,
                                    supporting = pluralStringResource(
                                        R.plurals.item_s,
                                        album.songs.size,
                                        album.songs.size
                                    ),
                                    shape = expandableListItem(index, searchState.result.albums.size),
                                    background = MaterialTheme.colorScheme.surface,
                                    cover = album.cover,
                                    icon = Icons.Default.Album,
                                    modifier = Modifier
                                        .padding(horizontal = 12.dp)
                                        .animateItem(),
                                    onClick = {
                                        onAlbumClick(album)
                                    }
                                )
                            }
                            resultHolder(
                                label = R.string.artists,
                                items = searchState.result.artists,
                                expanded = searchState.artistsExpanded,
                                onExpandedChange = {
                                    viewModel.onAction(SearchAction.UpdateResultExpanded(artists = it))
                                }
                            ) { index, artist ->
                                ListItem(
                                    headline = artist.name,
                                    supporting = pluralStringResource(
                                        R.plurals.item_s,
                                        artist.songs.size,
                                        artist.songs.size
                                    ),
                                    shape = expandableListItem(index, searchState.result.artists.size),
                                    background = MaterialTheme.colorScheme.surface,
                                    cover = artist.getPicture(),
                                    icon = Icons.Default.Person,
                                    modifier = Modifier
                                        .padding(horizontal = 12.dp)
                                        .animateItem(),
                                    onClick = {
                                        onArtistClick(artist)
                                    }
                                )
                            }
                            resultHolder(
                                label = R.string.playlists,
                                items = searchState.result.playlists,
                                expanded = searchState.playlistsExpanded,
                                onExpandedChange = {
                                    viewModel.onAction(SearchAction.UpdateResultExpanded(playlists = it))
                                }
                            ) { index, playlist ->
                                ListItem(
                                    headline = playlist.name,
                                    supporting = pluralStringResource(
                                        R.plurals.item_s,
                                        playlist.songs.size,
                                        playlist.songs.size
                                    ),
                                    shape = expandableListItem(index, searchState.result.playlists.size),
                                    background = MaterialTheme.colorScheme.surface,
                                    cover = playlist.image,
                                    icon = Icons.Default.Person,
                                    modifier = Modifier
                                        .padding(horizontal = 12.dp)
                                        .animateItem(),
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
            items.filterIndexed { index, _ -> (expanded) or (index < 3) },
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