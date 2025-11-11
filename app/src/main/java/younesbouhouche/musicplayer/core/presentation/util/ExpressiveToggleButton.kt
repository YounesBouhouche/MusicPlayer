package younesbouhouche.musicplayer.core.presentation.util

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedToggleButton
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonColors
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExpressiveToggleButton(
    checked: Boolean,
    text: (@Composable () -> Unit)?,
    size: Dp,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    loading: Boolean = false,
    enabled: Boolean = true,
    outlined: Boolean = false,
    colors: ToggleButtonColors =
        if (outlined)
            ToggleButtonDefaults.outlinedToggleButtonColors(
                checkedContainerColor = MaterialTheme.colorScheme.primary,
                checkedContentColor = MaterialTheme.colorScheme.onPrimary,
            )
        else
            ToggleButtonDefaults.toggleButtonColors(),
    interactionSource: MutableInteractionSource? = null,
    contentPadding: PaddingValues = ButtonDefaults.contentPaddingFor(size),
    onCheckedChange: (Boolean) -> Unit
) {
    if (outlined)
        OutlinedToggleButton(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = modifier.heightIn(size),
            contentPadding = contentPadding,
            enabled = enabled,
            colors = colors,
            shapes = ToggleButtonDefaults.shapesFor(size),
            interactionSource = interactionSource
        ) {
            AnimatedContent(loading) { isLoading ->
                if (isLoading)
                    Row {
                        LoadingIndicator(
                            Modifier.size(ButtonDefaults.iconSizeFor(size)),
                            color = ButtonDefaults.buttonColors().contentColor
                        )
                    }
                else {
                    Row {
                        icon?.let {
                            Icon(
                                icon,
                                contentDescription = null,
                                modifier = Modifier.size(ButtonDefaults.iconSizeFor(size)),
                            )
                        }
                        if ((icon != null) and (text != null))
                            Spacer(Modifier.size(ButtonDefaults.iconSpacingFor(size)))
                        text?.let {
                            ProvideTextStyle(
                                ButtonDefaults.textStyleFor(size),
                                it
                            )
                        }
                    }
                }
            }
        }
    else
        ToggleButton(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = modifier.heightIn(size),
            contentPadding = contentPadding,
            enabled = enabled,
            colors = colors,
            shapes = ToggleButtonDefaults.shapesFor(size),
            interactionSource = interactionSource
        ) {
            AnimatedContent(loading) { isLoading ->
                if (isLoading)
                    Row {
                        LoadingIndicator(
                            Modifier.size(ButtonDefaults.iconSizeFor(size)),
                            color = ButtonDefaults.buttonColors().contentColor
                        )
                    }
                else {
                    Row {
                        icon?.let {
                            Icon(
                                icon,
                                contentDescription = null,
                                modifier = Modifier.size(ButtonDefaults.iconSizeFor(size)),
                            )
                            Spacer(Modifier.size(ButtonDefaults.iconSpacingFor(size)))
                        }
                        text?.let {
                            ProvideTextStyle(
                                ButtonDefaults.textStyleFor(size),
                                it
                            )
                        }
                    }
                }
            }
        }
}

