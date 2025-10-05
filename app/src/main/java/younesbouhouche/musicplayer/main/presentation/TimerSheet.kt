package younesbouhouche.musicplayer.main.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import soup.compose.material.motion.animation.materialSharedAxisZ
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.core.presentation.util.ExpressiveButton
import younesbouhouche.musicplayer.main.domain.events.TimerType
import younesbouhouche.musicplayer.main.presentation.util.plus

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TimerSheet(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    timer: TimerType,
    onSetTimer: (TimerType) -> Unit,
    modifier: Modifier = Modifier
) {
    var selected by remember { mutableStateOf(timer) }
    val state = rememberModalBottomSheetState(true)
    if (visible) {
        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            modifier = modifier,
            sheetState = state,
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            dragHandle = { BottomSheetDefaults.DragHandle(
                color = MaterialTheme.colorScheme.primary
            ) },
            contentWindowInsets = {
                BottomSheetDefaults.windowInsets.exclude(WindowInsets.navigationBars)
            }
        ) {
            LazyColumn(
                Modifier.fillMaxSize(),
                contentPadding = WindowInsets.navigationBars.asPaddingValues() + PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Row(Modifier
                        .clip(RoundedCornerShape(100))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .padding(16.dp)
                        .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            stringResource(R.string.sleep_timer),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                        Switch(
                            selected != TimerType.Disabled,
                            {
                                selected = if (it) TimerType.Duration(60 * 1000) else TimerType.Disabled
                            }
                        )
                    }
                }
                item {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        repeat(3) { index ->
                            val checked = when (index) {
                                0 -> selected is TimerType.Duration
                                1 -> selected is TimerType.Time
                                else -> selected is TimerType.End
                            }
                            val res = when (index) {
                                0 -> R.string.duration
                                1 -> R.string.time
                                else -> R.string.end
                            }
                            val interactionSource = remember { MutableInteractionSource() }
                            val pressed by interactionSource.collectIsPressedAsState()
                            val weight by animateFloatAsState(
                                if (pressed) 1.4f else 1f
                            )
                            ToggleButton(
                                checked,
                                {
                                    selected = when (index) {
                                        0 -> TimerType.Duration(60 * 1000)
                                        1 -> TimerType.Time(0, 1)
                                        else -> TimerType.End(2)
                                    }
                                },
                                Modifier
                                    .weight(weight)
                                    .height(ButtonDefaults.MediumContainerHeight),
                                shapes = when(index) {
                                    0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                                    1 -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                                    else -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                                },
                                colors = ToggleButtonDefaults.toggleButtonColors(
                                    checkedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    checkedContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
                                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                interactionSource = interactionSource,
                                contentPadding = ButtonDefaults.contentPaddingFor(ButtonDefaults.MediumContainerHeight)
                            ) {
                                Text(stringResource(res))
                            }
                        }
                    }
                }
                if (selected != TimerType.Disabled)
                    item {
                        Surface(
                            Modifier.fillMaxWidth().animateItem(),
                            shape = MaterialTheme.shapes.extraLarge,
                            color = MaterialTheme.colorScheme.surfaceContainerLow
                        ) {
                            AnimatedContent(
                                selected,
                                transitionSpec = { materialSharedAxisZ(true) }
                            ) {
                                when(it) {
                                    TimerType.Disabled -> Unit
                                    is TimerType.Duration -> {

                                    }
                                    is TimerType.End -> {

                                    }
                                    is TimerType.Time -> {

                                    }
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
                                if (it == 0) R.string.cancel to onDismissRequest
                                else R.string.ok to {
                                    onSetTimer(selected)
                                    onDismissRequest()
                                }
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
                                interactionSource = interactionSource
                            )
                        }
                    }
                }
            }
        }
    }
}