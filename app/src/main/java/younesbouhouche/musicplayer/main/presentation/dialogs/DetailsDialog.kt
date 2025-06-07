package younesbouhouche.musicplayer.main.presentation.dialogs

import android.content.ClipData
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.core.domain.models.MusicCard
import younesbouhouche.musicplayer.main.presentation.components.Dialog
import younesbouhouche.musicplayer.main.presentation.util.timeString
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun DetailsDialog(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    file: MusicCard,
) {
    Dialog(
        visible = visible,
        onDismissRequest = onDismissRequest,
        title = stringResource(R.string.details),
        okListener = onDismissRequest,
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            modifier =
                Modifier
                    .padding(horizontal = 8.dp)
                    .clip(MaterialTheme.shapes.large)
                    .clipToBounds(),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Item(stringResource(R.string.title), file.title)
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                Item(stringResource(R.string.file_path), file.path)
            }
            item {
                Item(stringResource(R.string.duration), file.duration.timeString)
            }
            item {
                Item(
                    stringResource(R.string.date_modified),
                    LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(file.date),
                        ZoneId.systemDefault(),
                    ).format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)),
                )
            }
            item {
                Item(stringResource(R.string.album), file.album)
            }
            item {
                Item(stringResource(R.string.artist), if (file.artist == "<unknown>") "" else file.artist)
            }
            item {
                Item(stringResource(R.string.genre), file.genre)
            }
            item {
                Item(stringResource(R.string.composer), file.composer)
            }
        }
    }
}

@Composable
fun Item(
    title: String,
    text: String,
    modifier: Modifier = Modifier,
) {
    val clipboardManager = LocalClipboard.current
    val scope = rememberCoroutineScope()
    Column(
        modifier =
            modifier
                .background(
                    MaterialTheme.colorScheme.surfaceContainer,
                    MaterialTheme.shapes.extraSmall,
                )
                .clip(MaterialTheme.shapes.extraSmall)
                .clipToBounds()
                .clickable {
                    scope.launch {
                        clipboardManager.setClipEntry(ClipEntry(ClipData.newPlainText("", text)))
                    }
                }
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = text.ifEmpty { stringResource(R.string.unspecified) },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
