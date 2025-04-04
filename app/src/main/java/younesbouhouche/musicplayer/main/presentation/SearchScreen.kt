package younesbouhouche.musicplayer.main.presentation

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import soup.compose.material.motion.animation.materialSharedAxisZIn
import soup.compose.material.motion.animation.materialSharedAxisZOut
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.main.presentation.components.LazyMusicCardScreen
import younesbouhouche.musicplayer.main.domain.events.SearchEvent
import younesbouhouche.musicplayer.core.domain.models.MusicCard
import younesbouhouche.musicplayer.main.presentation.states.SearchState
import younesbouhouche.musicplayer.settings.presentation.SettingsActivity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    state: SearchState,
    loading: Boolean = false,
    onSearchEvent: (SearchEvent) -> Unit,
    play: (Int) -> Unit,
    sortButton: (@Composable RowScope.() -> Unit)? = null,
    showBottomSheet: (MusicCard) -> Unit,
) {
    val context = LocalContext.current
    val padding by animateDpAsState(targetValue = if (state.expanded) 0.dp else 8.dp, label = "")
    Box {
        SearchBar(
            inputField = {
                SearchBarDefaults.InputField(
                    query = state.query,
                    onQueryChange = { onSearchEvent(SearchEvent.UpdateQuery(it)) },
                    onSearch = { onSearchEvent(SearchEvent.UpdateQuery(it)) },
                    expanded = state.expanded,
                    placeholder = {
                        Text(stringResource(R.string.search))
                    },
                    leadingIcon = {
                        if (state.expanded) {
                            IconButton(onClick = { onSearchEvent(SearchEvent.Collapse) }) {
                                Icon(Icons.AutoMirrored.Default.ArrowBack, null)
                            }
                        } else {
                            IconButton(onClick = { onSearchEvent(SearchEvent.Expand) }) {
                                Icon(Icons.Default.Search, null)
                            }
                        }
                    },
                    trailingIcon = {
                        Row {
                            IconButton(onClick = { context.startActivity(Intent(context, SettingsActivity::class.java)) }) {
                                Icon(Icons.Default.Settings, null)
                            }
                            sortButton?.invoke(this)
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
            LazyColumn(Modifier.fillMaxSize()) {
                items(state.result, { it.id }) {
                    LazyMusicCardScreen(
                        file = it,
                        onLongClick = {
                            showBottomSheet(it)
                        },
                    ) {
                        play(state.result.indexOf(it))
                    }
                }
            }
        }
        AnimatedVisibility(
            visible = loading,
            enter = materialSharedAxisZIn(true),
            exit = materialSharedAxisZOut(true),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = SearchBarDefaults.InputFieldHeight)
                    .align(Alignment.BottomCenter)
                    .imePadding(),
        ) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
    }
}
