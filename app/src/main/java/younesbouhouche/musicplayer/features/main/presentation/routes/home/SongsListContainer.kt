package younesbouhouche.musicplayer.features.main.presentation.routes.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SplitButtonDefaults
import androidx.compose.material3.SplitButtonLayout
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.younesb.mydesignsystem.presentation.components.ExpressiveIconButton
import com.younesb.mydesignsystem.presentation.components.Image
import younesbouhouche.musicplayer.core.domain.models.Song

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SongsListContainer(
    title: String,
    subtitle: String,
    list: List<Song>,
    onPlay: (songIds: List<Long>, startIndex: Int, shuffle: Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    ListContainer(
        title = title,
        subtitle = subtitle,
        modifier = modifier,
        actions = {
            SplitButtonLayout(
                leadingButton = {
                    SplitButtonDefaults.LeadingButton(
                        onClick = {
                            onPlay(list.map { it.id }, 0, false)
                        },
                        Modifier.height(46.dp),
                    ) {
                        Icon(
                            Icons.Default.PlayArrow,
                            null,
                            modifier = Modifier.size(SplitButtonDefaults.LeadingIconSize),
                        )
                        Spacer(Modifier.width(ButtonDefaults.ExtraSmallIconSpacing))
                        Text("Play")
                    }
                },
                trailingButton = {
                    SplitButtonDefaults.TrailingButton(
                        onClick = {
                            onPlay(list.map { it.id }, 0, true)
                        },
                        Modifier.height(46.dp),
                    ) {
                        Icon(Icons.Default.Shuffle, null)
                    }
                },
            )
        },
    ) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 20.dp),
        ) {
            items(list, { it.id }) { song ->
                SongItem(song, Modifier.animateItem()) {
                    onPlay(
                        list.map { it.id },
                        list.indexOf(song),
                        false,
                    )
                }
            }
        }
    }
}

@Composable
fun SongItem(
    song: Song,
    modifier: Modifier = Modifier,
    onPlay: () -> Unit,
) {
    Surface(
        modifier = modifier.widthIn(max = 240.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        Row(
            Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Image(
                song.coverUri,
                Icons.Default.MusicNote,
                Modifier.size(50.dp),
                background = MaterialTheme.colorScheme.surfaceVariant,
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    song.title,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    song.artist,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            ExpressiveIconButton(
                onClick = onPlay,
                icon = Icons.Default.PlayArrow,
            )
        }
    }
}
