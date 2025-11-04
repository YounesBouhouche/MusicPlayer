package younesbouhouche.musicplayer.main.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.core.presentation.util.ExpressiveButton
import younesbouhouche.musicplayer.main.presentation.util.composables.TitleText
import younesbouhouche.musicplayer.main.presentation.util.expressiveRectShape
import younesbouhouche.musicplayer.main.presentation.util.floatUpDownTransSpec
import younesbouhouche.musicplayer.main.presentation.util.plus
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SpeedSheet(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    speed: Float,
    onSetSpeed: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedSpeed by remember { mutableFloatStateOf(speed) }
    LaunchedEffect(speed) {
        selectedSpeed = speed
    }
    val state = rememberModalBottomSheetState(true)
    if (visible) {
        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            modifier = modifier,
            sheetState = state,
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            dragHandle = {
                BottomSheetDefaults.DragHandle(
                    color = MaterialTheme.colorScheme.primary
                )
            },
            contentWindowInsets = {
                BottomSheetDefaults.windowInsets.exclude(WindowInsets.navigationBars)
            }
        ) {
            LazyColumn(
                Modifier.fillMaxWidth(),
                contentPadding = WindowInsets.navigationBars.asPaddingValues() + PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    TitleText(text = stringResource(R.string.playback_speed))
                }
                item {
                    Column(
                        modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = expressiveRectShape(
                                0,
                                2,
                                MaterialTheme.shapes.small,
                                MaterialTheme.shapes.large,
                                )
                        ) {
                            Slider(
                                valueRange = .25f..3f,
                                value = selectedSpeed,
                                onValueChange = {
                                    selectedSpeed = (it * 4).toInt() / 4f
                                },
                                onValueChangeFinished = {
                                    onSetSpeed(selectedSpeed)
                                },
                                modifier = modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                steps = 11
                            )
                        }
                        Surface(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = expressiveRectShape(
                                1,
                                2,
                                MaterialTheme.shapes.small,
                                MaterialTheme.shapes.large
                            )
                        ) {
                            AnimatedContent(
                                selectedSpeed,
                                transitionSpec = {
                                    floatUpDownTransSpec().using(SizeTransform(false))
                                }
                            ) {
                                Text(
                                    text = stringResource(
                                        R.string.speed_x,
                                        "%.2f".format(Locale.getDefault(), it)
                                    ),
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = modifier
                                        .fillMaxWidth()
                                        .padding(18.dp),
                                    color = MaterialTheme.colorScheme.primary,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
                item {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        repeat(2) {
                            val (res, onClick) =
                                if (it == 0) R.string.reset to {
                                    onSetSpeed(1f)
                                    onDismissRequest()
                                }
                                else R.string.ok to onDismissRequest
                            val interactionSource = remember { MutableInteractionSource() }
                            val pressed by interactionSource.collectIsPressedAsState()
                            val weight by animateFloatAsState(
                                if (pressed) 1.4f else 1f
                            )
                            ExpressiveButton(
                                stringResource(res),
                                ButtonDefaults.MediumContainerHeight,
                                Modifier.weight(weight),
                                onClick = onClick,
                                outlined = it == 0,
                                interactionSource = interactionSource,
                            )
                        }
                    }
                }
            }
        }
    }
}