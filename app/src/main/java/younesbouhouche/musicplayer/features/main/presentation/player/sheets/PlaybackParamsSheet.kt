package younesbouhouche.musicplayer.features.main.presentation.player.sheets

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import younesbouhouche.musicplayer.R
import com.younesb.mydesignsystem.presentation.components.ExpressiveButton
import younesbouhouche.musicplayer.features.main.presentation.util.composables.TitleText
import younesbouhouche.musicplayer.features.main.presentation.util.expressiveRectShape
import younesbouhouche.musicplayer.features.main.presentation.util.intUpDownTransSpec
import younesbouhouche.musicplayer.features.main.presentation.util.plus
import younesbouhouche.musicplayer.features.main.presentation.util.round
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PlaybackParamsSheet(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    speed: Float,
    onSetSpeed: (Float) -> Unit,
    pitch: Float,
    onSetPitch: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
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
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    TitleText(
                        text = stringResource(R.string.playback_params),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
                repeat(2) {
                    val value = when(it) {
                        0 -> speed
                        else -> pitch
                    }
                    val valueRange = when(it) {
                        0 -> 0.25f..3f
                        else -> 0.5f..2f
                    }
                    val roundTo = when(it) {
                        0 -> 0.25f
                        else -> 0.1f
                    }
                    val onValueChange = when(it) {
                        0 -> onSetSpeed
                        else -> onSetPitch
                    }
                    val label = when(it) {
                        0 -> R.string.speed
                        else -> R.string.pitch
                    }
                    val icon = when(it) {
                        0 -> Icons.Default.Speed
                        else -> Icons.Default.GraphicEq
                    }
                    item {
                        SliderContainer(
                            value,
                            onValueChange,
                            stringResource(label),
                            icon,
                            valueRange = valueRange,
                            roundTo = roundTo,
                            shape = expressiveRectShape(
                                it,
                                2,
                                MaterialTheme.shapes.extraSmall,
                                MaterialTheme.shapes.large,
                            )
                        )
                    }
                }
                item {
                    Row(
                        Modifier.fillMaxWidth().padding(top = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        repeat(2) {
                            val (res, onClick) =
                                if (it == 0) R.string.reset to {
                                    onSetSpeed(1f)
                                    onSetPitch(1f)
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

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun SliderContainer(
    value: Float,
    onValueChange: (Float) -> Unit,
    label: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = .25f..3f,
    roundTo: Float = 0.25f,
    steps: Int = ((valueRange.endInclusive - valueRange.start) / roundTo).toInt() - 1,
    shape: Shape = MaterialTheme.shapes.extraLarge,
    scale: Int = 2,
    valueFormat: (String) -> String = { it },
) {
    var selectedValue by remember { mutableFloatStateOf(value) }
    val sliderState =
        rememberSliderState(
            value = value,
            valueRange = valueRange,
            onValueChangeFinished = {
                onValueChange(selectedValue)
            },
            steps = steps,
        ).apply {
            this.onValueChange = {
                selectedValue = (it / roundTo).roundToInt() * roundTo
                this.value = selectedValue
            }
        }
    val interactionSource = remember { MutableInteractionSource() }
    val painter = rememberVectorPainter(icon)
    LaunchedEffect(value) {
        selectedValue = value
        sliderState.value = value
    }
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        shape = shape
    ) {
        Column(
            modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    label,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Box(
                    Modifier
                        .clip(RoundedCornerShape(100))
                        .background(MaterialTheme.colorScheme.surfaceContainerLow)
                ) {
                    AnimatedCounterText(
                        text = valueFormat(selectedValue.round(scale)),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = modifier
                            .padding(12.dp, 4.dp),
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                }
            }
            Slider(
                state = sliderState,
                interactionSource = interactionSource,
                modifier = modifier.fillMaxWidth(),
                track = {
                    val iconSize = DpSize(20.dp, 20.dp)
                    val iconPadding = 10.dp
                    val thumbTrackGapSize = 6.dp
                    val activeIconColor = SliderDefaults.colors().activeTickColor
                    val inactiveIconColor = SliderDefaults.colors().inactiveTickColor
                    val trackIcon: DrawScope.(Offset, Color) -> Unit = { offset, color ->
                        translate(offset.x + iconPadding.toPx(), offset.y) {
                            with(painter) {
                                draw(iconSize.toSize(), colorFilter = ColorFilter.tint(color))
                            }
                        }
                    }
                    SliderDefaults.Track(
                        sliderState = sliderState,
                        modifier =
                            Modifier.height(36.dp).drawWithContent {
                                drawContent()

                                val yOffset = size.height / 2 - iconSize.toSize().height / 2
                                val activeTrackStart = 0f
                                val activeTrackEnd =
                                    size.width * sliderState.coercedValueAsFraction -
                                            thumbTrackGapSize.toPx()
                                val inactiveTrackStart = activeTrackEnd + thumbTrackGapSize.toPx() * 2
                                val inactiveTrackEnd = size.width

                                val activeTrackWidth = activeTrackEnd - activeTrackStart
                                val inactiveTrackWidth = inactiveTrackEnd - inactiveTrackStart
                                if (
                                    iconSize.toSize().width < activeTrackWidth - iconPadding.toPx() * 2
                                ) {
                                    trackIcon(Offset(activeTrackStart, yOffset), activeIconColor)
                                }
                                if (
                                    iconSize.toSize().width <
                                    inactiveTrackWidth - iconPadding.toPx() * 2
                                ) {
                                    trackIcon(
                                        Offset(inactiveTrackStart, yOffset),
                                        inactiveIconColor,
                                    )
                                }
                            },
                        trackCornerSize = 12.dp,
                        drawStopIndicator = null,
                        thumbTrackGapSize = thumbTrackGapSize,
                        drawTick = { _, _ -> }
                    )
                },
            )
        }
    }
}

@Composable
internal fun AnimatedCounterText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    color: Color = MaterialTheme.colorScheme.onSurface,
    textAlign: TextAlign = TextAlign.Start,
) {
    Row(
        modifier,
        horizontalArrangement = when (textAlign) {
            TextAlign.Start -> Arrangement.Start
            TextAlign.End -> Arrangement.End
            TextAlign.Center -> Arrangement.Center
            else -> Arrangement.Start
        }
    ) {
        text.forEach { char ->
            if (char.isDigit()) {
                AnimatedContent(
                    targetState = char.digitToInt(),
                    transitionSpec = {
                        intUpDownTransSpec() using SizeTransform(clip = false)
                    },
                ) { targetChar ->
                    Text(
                        targetChar.toString(),
                        style = style,
                        color = color,
                    )
                }
            } else {
                Text(
                    char.toString(),
                    style = style,
                    color = color,
                )
            }
        }
    }
}

@Preview
@Composable
private fun SliderContainerPreview() {
    SliderContainer(
        value = 1.25f,
        onValueChange = {},
        valueFormat = { it },
        icon = Icons.Default.MusicNote,
        label = "Sound"
    )
}