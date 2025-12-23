package younesbouhouche.musicplayer.features.main.presentation.routes.metadata_editor

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Lyrics
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheetDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.glance.action.action
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.younesb.mydesignsystem.presentation.components.ButtonsRow
import com.younesb.mydesignsystem.presentation.components.ExpressiveButton
import com.younesb.mydesignsystem.presentation.components.ExpressiveIconButton
import com.younesb.mydesignsystem.presentation.components.ImagePicker
import com.younesb.mydesignsystem.presentation.components.TextField
import com.younesb.mydesignsystem.presentation.util.plus
import com.younesb.mydesignsystem.presentation.util.validation.InputError
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.core.presentation.TopBar

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MetadataEditorScreen(
    songId: Long,
    modifier: Modifier = Modifier,
    onBack: () -> Unit
) {
    val viewModel = koinViewModel<MetadataEditorViewModel> {
        parametersOf(songId)
    }
    val state by viewModel.state.collectAsStateWithLifecycle()
    val colors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
        focusedBorderColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        unfocusedBorderColor = MaterialTheme.colorScheme.surfaceContainer,
        focusedLeadingIconColor = MaterialTheme.colorScheme.primary
    )
    Scaffold(
        topBar = {
            TopBar(
                title = { Text(stringResource(R.string.edit_metadata)) },
                actions = {
                    ExpressiveIconButton(
                        icon = Icons.Default.Restore,
                        colors = IconButtonDefaults.filledTonalIconButtonColors(),
                        widthOption = IconButtonDefaults.IconButtonWidthOption.Narrow,
                        size = IconButtonDefaults.mediumIconSize,
                        onClick = viewModel::resetState
                    )
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = BottomSheetDefaults.ContainerColor
            ) {
                ButtonsRow(
                    count = 2,
                    icon = { null },
                    text = {
                        stringResource(if (it == 0) R.string.cancel else R.string.ok)
                    },
                    outlined = { it == 0 },
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        if (it == 1) viewModel.confirm()
                        onBack()
                    }
                )
            }
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = paddingValues + PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                ImagePicker(
                    image = state.image,
                    onImageChange = { image ->
                        viewModel.updateState {
                            it.copy(image = image)
                        }
                    },
                    modifier = Modifier.size(200.dp),
                    shape = MaterialShapes.Cookie4Sided.toShape(),
                    fraction = .3f,
                    background = MaterialTheme.colorScheme.tertiaryContainer,
                    iconTint = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
            items(fieldsRows) { row ->
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    row.forEach { (value, onValueChange, label, error, placeholder, icon, keyboardType, singleLine) ->
                        val interactionSource = remember {
                            MutableInteractionSource()
                        }
                        val pressed by interactionSource.collectIsPressedAsState()
                        val weight by animateFloatAsState(
                            if (pressed) 1.4f else 1f
                        )
                        val currentValue = value(state)
                        LaunchedEffect(currentValue) {
                            println("MetadataEditorScreen: $label = $currentValue")
                        }
                        TextField(
                            value = value(state),
                            onValueChange = { value ->
                                viewModel.updateState {
                                    onValueChange(state, value)
                                }
                            },
                            modifier = Modifier.weight(weight).then(
                                if (singleLine) Modifier
                                else Modifier.height(240.dp)
                            ),
                            label = label,
                            placeholder = placeholder ?: "",
                            leadingIcon = icon,
                            error = error(state)?.toString(),
                            imeAction = ImeAction.Next,
                            keyboardType = keyboardType,
                            singleLine = singleLine,
                            interactionSource = interactionSource,
                            shape = MaterialTheme.shapes.extraLarge,
                            colors = colors,
                            contentPadding = PaddingValues(24.dp)
                        )
                    }
                }
            }
        }
    }
}

internal data class InputField(
    val value: (UiState) -> String,
    val onValueChange: (UiState, String) -> UiState,
    val label: String,
    val error: (UiState) -> InputError? = { null },
    val placeholder: String? = null,
    val icon: ImageVector? = null,
    val keyboardType: KeyboardType = KeyboardType.Text,
    val singleLine: Boolean = true,
)

internal val fieldsRows = listOf(
    listOf(
        InputField(
            value = { it.title },
            onValueChange = { state, value -> state.copy(title = value) },
            label = "Title",
            icon = Icons.Default.MusicNote
        ),
    ),
    listOf(
        InputField(
            value = { it.artist },
            onValueChange = { state, value -> state.copy(artist = value) },
            label = "Artist",
            icon = Icons.Default.Person
        ),
    ),
    listOf(
        InputField(
            value = { it.album },
            onValueChange = { state, value -> state.copy(album = value) },
            label = "Album",
            icon = Icons.Default.Album
        ),
    ),
    listOf(
        InputField(
            value = { it.trackNumber },
            onValueChange = { state, value -> state.copy(trackNumber = value) },
            label = "Track Number",
            icon = Icons.Default.Numbers
        ),
        InputField(
            value = { it.discNumber },
            onValueChange = { state, value -> state.copy(discNumber = value) },
            label = "Disc Number",
            icon = Icons.Default.Numbers
        ),
    ),
    listOf(
        InputField(
            value = { it.composer },
            onValueChange = { state, value -> state.copy(composer = value) },
            label = "Composer",
            icon = Icons.Default.Person
        ),
    ),
    listOf(
        InputField(
            value = { it.genre },
            onValueChange = { state, value -> state.copy(genre = value) },
            label = "Genre",
            icon = Icons.Default.Category
        ),
    ),
    listOf(
        InputField(
            value = { it.year },
            onValueChange = { state, value -> state.copy(year = value) },
            label = "Year",
            icon = Icons.Default.CalendarToday,
            keyboardType = KeyboardType.Number
        ),
    ),
    listOf(
        InputField(
            value = { it.lyrics },
            onValueChange = { state, value -> state.copy(lyrics = value) },
            label = "Lyrics",
            icon = Icons.Default.Lyrics,
            keyboardType = KeyboardType.Number,
            singleLine = false
        ),
    ),
)