package younesbouhouche.musicplayer.main.presentation.routes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.core.domain.models.MusicCard
import younesbouhouche.musicplayer.core.presentation.util.IconContainer
import younesbouhouche.musicplayer.main.presentation.components.MusicCardListItem
import younesbouhouche.musicplayer.main.presentation.util.expressiveRectShape

@Composable
fun HomeScreen(
    favorites: List<MusicCard>,
    history: List<MusicCard>,
    modifier: Modifier = Modifier,
    onPlay: (List<MusicCard>, Int) -> Unit
) {
    LazyColumn(
        modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 260.dp)
    ) {
        item {
            Text(
                stringResource(R.string.welcome_back),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 32.dp, bottom = 32.dp, start = 8.dp)
            )
        }
        item {
            ListContainer(
                Icons.Default.Favorite,
                stringResource(R.string.favorites),
                stringResource(R.string.favorites_subtitles),
                favorites,
                listOf(
                    MaterialTheme.colorScheme.error,
                    MaterialTheme.colorScheme.error,
                ),
                MaterialTheme.colorScheme.onError,
                MaterialTheme.colorScheme.error,
                onPlay = onPlay,
            ) { }
        }
        item {
            ListContainer(
                Icons.Default.History,
                stringResource(R.string.history),
                stringResource(R.string.watch_your_recently_played_songs),
                history,
                listOf(
                    MaterialTheme.colorScheme.primary,
                    MaterialTheme.colorScheme.tertiary,
                ),
                MaterialTheme.colorScheme.onPrimary,
                MaterialTheme.colorScheme.primary,
                onPlay = onPlay,
            ) { }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ListContainer(
    icon: ImageVector,
    title: String,
    subtitle: String,
    items: List<MusicCard>,
    colorStops: List<Color>,
    contentColor: Color,
    iconButtonColor: Color,
    modifier: Modifier = Modifier,
    onPlay: (List<MusicCard>, Int) -> Unit,
    onClick: () -> Unit,
) {
    val itemsSliced = items.take(2)
    AnimatedVisibility(
        items.isNotEmpty(),
        enter = expandVertically(expandFrom = Alignment.Top),
        exit = shrinkVertically(shrinkTowards = Alignment.Top),
    ) {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier
                    .clip(expressiveRectShape(0, 2))
                    .background(Brush.linearGradient(colorStops))
                    .clickable(onClick = onClick)
                    .padding(18.dp, 24.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    icon,
                    null,
                    Modifier.size(36.dp),
                    tint = contentColor
                )
                Column(
                    Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        title,
                        style = MaterialTheme.typography.titleMedium,
                        color = contentColor,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = contentColor.copy(alpha = 0.7f)
                    )
                }
                IconContainer(
                    Icons.Default.PlayArrow,
                    Modifier.size(40.dp),
                    onClick = {
                        onPlay(items, 0)
                    },
                    shape = MaterialShapes.Cookie9Sided.toShape(),
                    iconTint = iconButtonColor,
                    background = contentColor
                )
            }
            itemsSliced.forEachIndexed { index, it ->
                MusicCardListItem(
                    it,
                    modifier = Modifier.fillMaxWidth(),
                    shape = expressiveRectShape(index + 1, itemsSliced.size + 1),
                    onClick = {
                        onPlay(items, index)
                    }
                )
            }
        }
    }
}