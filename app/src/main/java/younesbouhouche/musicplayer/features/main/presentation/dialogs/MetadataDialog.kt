package younesbouhouche.musicplayer.features.main.presentation.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.features.main.presentation.components.Dialog
import younesbouhouche.musicplayer.features.main.presentation.states.MusicMetadata

@Composable
fun MetadataDialog(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    okListener: () -> Unit,
    metadata: MusicMetadata,
    onMetadataChange: (MusicMetadata) -> Unit,
) {
    Dialog(
        visible = visible,
        onDismissRequest = onDismissRequest,
        title = stringResource(R.string.edit_metadata),
        cancelListener = onDismissRequest,
        okListener = okListener,
    ) {
        LazyVerticalGrid(columns = GridCells.Fixed(2)) {
            listOf(
                listOf((R.string.title to Icons.Default.Title) to metadata.newTitle),
                listOf(
                    (R.string.album to Icons.Default.Album) to metadata.newAlbum,
                    (R.string.artist to Icons.Default.Person) to metadata.newArtist,
                ),
                listOf(
                    (R.string.genre to Icons.Default.Category) to metadata.newGenre,
                    (R.string.composer to Icons.Default.Person) to metadata.newComposer,
                ),
                listOf((R.string.year to Icons.Default.CalendarMonth) to metadata.newYear),
            ).forEach { row ->
                row.forEach {
                    item(span = { GridItemSpan(if (row.size == 1) maxCurrentLineSpan else 1) }) {
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                        ) {
                            OutlinedTextField(
                                value = it.second,
                                onValueChange = { value ->
                                    onMetadataChange(
                                        when (it.first.first) {
                                            R.string.title -> metadata.copy(newTitle = value)
                                            R.string.album -> metadata.copy(newAlbum = value)
                                            R.string.artist -> metadata.copy(newArtist = value)
                                            R.string.genre -> metadata.copy(newGenre = value)
                                            R.string.composer -> metadata.copy(newComposer = value)
                                            R.string.year -> metadata.copy(newYear = value)
                                            else -> throw IllegalArgumentException("Unknown metadata field")
                                        },
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                label = {
                                    Text(stringResource(it.first.first))
                                },
                                leadingIcon = {
                                    Icon(it.first.second, null)
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}
