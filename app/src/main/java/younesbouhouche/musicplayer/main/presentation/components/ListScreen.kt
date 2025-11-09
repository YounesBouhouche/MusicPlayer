package younesbouhouche.musicplayer.main.presentation.components

import android.graphics.drawable.BitmapDrawable
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.kmpalette.rememberPaletteState
import kotlinx.coroutines.launch
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.core.domain.models.MusicCard
import younesbouhouche.musicplayer.core.presentation.util.ExpressiveIconButton
import younesbouhouche.musicplayer.main.presentation.util.SortBottomSheet
import younesbouhouche.musicplayer.main.presentation.util.SortState
import younesbouhouche.musicplayer.main.presentation.util.SortType
import younesbouhouche.musicplayer.main.presentation.util.expressiveRectShape
import younesbouhouche.musicplayer.main.presentation.util.plus
import younesbouhouche.musicplayer.main.presentation.util.topAppBarIconButtonColors
import younesbouhouche.musicplayer.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun <T>ListScreen(
    title: String,
    items: List<MusicCard>,
    cover: Any?,
    icon: ImageVector,
    sortState: SortState<T>,
    onSortStateChange: (SortState<T>) -> Unit,
    options: List<T>,
    iconCallback: @Composable ((T) -> ImageVector),
    textCallback: @Composable ((T) -> Int),
    modifier: Modifier = Modifier,
    iconShape: Shape = MaterialShapes.Cookie12Sided.toShape(),
    subtitle: String = pluralStringResource(R.plurals.item_s, items.size, items.size),
    actions: @Composable RowScope.() -> Unit = {},
    contentPadding: PaddingValues = PaddingValues(),
    onShowBottomSheet: (MusicCard) -> Unit,
    onPlay: (index: Int, shuffle: Boolean) -> Unit,
    listState: LazyListState = rememberLazyListState(),
    itemContent: @Composable (LazyItemScope.(Int, MusicCard) -> Unit) = { index, card ->
        MusicCardListItem(
            card,
            shape = expressiveRectShape(index, items.size),
            modifier = Modifier.animateItem(),
            onLongClick = {
                onShowBottomSheet(card)
            }
        ) {
            onPlay(index, false)
        }
    }
) {
    val palette = rememberPaletteState()
    val scope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current
    AppTheme(palette) {
        Scaffold(
            modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBar(
                    title = {

                    },
                    navigationIcon = {
                        ExpressiveIconButton(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            size = IconButtonDefaults.mediumIconSize,
                            colors = topAppBarIconButtonColors()
                        ) {
                            backDispatcher?.onBackPressedDispatcher?.onBackPressed()
                        }
                    },
                    actions = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            actions()
                            ExpressiveIconButton(
                                Icons.AutoMirrored.Filled.Sort,
                                size = IconButtonDefaults.mediumIconSize,
                                colors = topAppBarIconButtonColors()
                            ) {
                                onSortStateChange(sortState.copy(expanded = true))
                            }
                        }
                    },
                    scrollBehavior = scrollBehavior,
                    contentPadding = PaddingValues(8.dp)
                )
            },
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = listState,
                contentPadding = paddingValues + contentPadding + PaddingValues(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Column(
                        Modifier.padding(top = 8.dp, bottom = 32.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            MyImage(
                                cover,
                                icon = icon,
                                modifier = Modifier.size(160.dp),
                                shape = iconShape,
                                onSuccess = {
                                    ((it.result.drawable as? BitmapDrawable)?.bitmap)?.let { bitmap ->
                                        scope.launch {
                                            palette.generate(bitmap.asImageBitmap())
                                        }
                                    } ?: palette.reset()
                                }
                            )
                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                repeat(2) {
                                    val colors =
                                        if (it == 0)
                                            IconButtonDefaults.filledIconButtonColors()
                                        else
                                            IconButtonDefaults.filledTonalIconButtonColors()
                                    val icon = if (it == 0) Icons.Default.PlayArrow else Icons.Default.Shuffle
                                    val interactionSource = remember { MutableInteractionSource() }
                                    val pressed by interactionSource.collectIsPressedAsState()
                                    val space by animateDpAsState(
                                        if (pressed) 0.dp else 32.dp,
                                    )
                                    ExpressiveIconButton(
                                        icon = icon,
                                        size = IconButtonDefaults.mediumIconSize,
                                        modifier = Modifier.fillMaxWidth()
                                            .padding(
                                                start = if (it == 1) space else 0.dp,
                                                end = if (it == 0) space else 0.dp,
                                            )
                                            .height(80.dp),
                                        colors = colors,
                                        interactionSource = interactionSource
                                    ) {
                                        onPlay(0, it == 1)
                                    }
                                }
                            }
                        }
                        Text(
                            title,
                            Modifier.fillMaxWidth().padding(top = 24.dp),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Italic,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            subtitle,
                            Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(.7f),
                        )
                    }
                }
                itemsIndexed(items, { _, it -> it.id }, itemContent = itemContent)
            }
        }
        SortBottomSheet(
            sortState,
            options,
            iconCallback,
            textCallback,
            onSortStateChange
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ListScreen(
    title: String,
    items: List<MusicCard>,
    cover: Any?,
    icon: ImageVector,
    sortState: SortState<SortType>,
    onSortStateChange: (SortState<SortType>) -> Unit,
    modifier: Modifier = Modifier,
    iconShape: Shape = MaterialShapes.Cookie12Sided.toShape(),
    subtitle: String = pluralStringResource(R.plurals.item_s, items.size, items.size),
    actions: @Composable RowScope.() -> Unit = {},
    contentPadding: PaddingValues = PaddingValues(),
    onShowBottomSheet: (MusicCard) -> Unit = {},
    onPlay: (index: Int, shuffle: Boolean) -> Unit,
) = ListScreen(
    title,
    items,
    cover,
    icon,
    sortState,
    onSortStateChange,
    SortType.entries,
    {
        it.icon
    },
    {
        it.label
    },
    modifier,
    iconShape,
    subtitle,
    actions,
    contentPadding,
    onShowBottomSheet,
    onPlay
)