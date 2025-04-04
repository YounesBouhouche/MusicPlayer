package younesbouhouche.musicplayer.main.presentation.player

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lyrics
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.main.domain.events.PlayerEvent
import younesbouhouche.musicplayer.main.domain.events.UiEvent
import younesbouhouche.musicplayer.main.presentation.util.toMs

@Composable
fun Lyrics(
    lyrics: String,
    syncing: Boolean,
    time: Long,
    onPlayerEvent: (PlayerEvent) -> Unit,
    onUiEvent: (UiEvent) -> Unit,
) {
    val timeRegex = Regex("\\[((\\d{2}:)?\\d{2}:\\d{2}([.:])\\d{2})]")
    val positions = timeRegex.findAll(lyrics).toList()
    val synced = positions.isNotEmpty()
    val lyricsLines by remember {
        derivedStateOf {
            lyrics.splitToSequence(timeRegex).toMutableList().apply {
                firstOrNull()?.let { if (it.isEmpty()) removeAt(0) }
            }
        }
    }
    var currentLine by remember { mutableIntStateOf(0) }
    val lyricsListState = rememberLazyListState()
    val isDragged by lyricsListState.interactionSource.collectIsDraggedAsState()
    LaunchedEffect(currentLine, syncing) {
        if (syncing) {
            lyricsListState.animateScrollToItem(currentLine)
        }
    }
    LaunchedEffect(key1 = isDragged) {
        if (isDragged) onUiEvent(UiEvent.DisableSyncing)
    }
    LaunchedEffect(lyrics) {
        println("Lyrics: \n $lyrics")
    }
    val scope = rememberCoroutineScope()
    if (lyrics.isEmpty()) {
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(Icons.Default.Lyrics, null, Modifier.size(60.dp))
            Text(
                text = stringResource(R.string.no_lyrics_available),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
            )
        }
    } else if (synced) {
        val segments =
            positions.map {
                it.value.removeSurrounding("[", "]").toMs()
            }
        LaunchedEffect(key1 = time) {
            if (syncing) currentLine = getIndex(segments, time)
        }
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            LazyColumn(
                Modifier
                    .fillMaxSize()
                    .weight(1f),
                state = lyricsListState,
            ) {
                itemsIndexed(lyricsLines) { index, line ->
                    val scale by animateFloatAsState(
                        if ((!syncing) or (index == currentLine)) {
                            1f
                        } else {
                            0.5f
                        },
                        label = "",
                    )
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .alpha(scale)
                            .clickable {
                                scope.launch {
                                    segments
                                        .getOrNull(index)
                                        ?.let {
                                            onPlayerEvent(PlayerEvent.SeekTime(it))
                                        }
                                    onUiEvent(UiEvent.EnableSyncing)
                                }
                            },
                    ) {
                        Text(
                            text = line.trim(),
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                            style = MaterialTheme.typography.headlineLarge,
                            textAlign = TextAlign.Start,
                        )
                    }
                }
            }
            AnimatedVisibility(visible = !syncing) {
                Button(onClick = { onUiEvent(UiEvent.EnableSyncing) }) {
                    Icon(Icons.Default.Timer, null)
                    Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                    Text(stringResource(R.string.sync_to_current_time))
                }
            }
        }
    } else {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = lyrics,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
            )
        }
    }
}
