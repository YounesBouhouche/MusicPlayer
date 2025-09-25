package younesbouhouche.musicplayer.core.presentation.util

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExpressiveButton(
    text: (@Composable () -> Unit)?,
    size: Dp,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    loading: Boolean = false,
    enabled: Boolean = true,
    outlined: Boolean = false,
    colors: ButtonColors =
        if (outlined)
            ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface)
        else
            ButtonDefaults.buttonColors(),
    interactionSource: MutableInteractionSource? = null,
    onClick: () -> Unit
) {
    if (outlined)
        OutlinedButton(
            onClick = onClick,
            modifier = modifier.heightIn(size),
            contentPadding = ButtonDefaults.contentPaddingFor(size),
            enabled = enabled,
            colors = colors,
            shapes = ButtonDefaults.shapesFor(size),
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
        Button(
            onClick = onClick,
            modifier = modifier.heightIn(size),
            contentPadding = ButtonDefaults.contentPaddingFor(size),
            enabled = enabled,
            colors = colors,
            shapes = ButtonDefaults.shapesFor(size),
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


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ExpressiveButton(
    text: String,
    size: Dp,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    loading: Boolean = false,
    enabled: Boolean = true,
    outlined: Boolean = false,
    colors: ButtonColors =
        if (outlined)
            ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface)
        else
            ButtonDefaults.buttonColors(),
    interactionSource: MutableInteractionSource? = null,
    onClick: () -> Unit
) = ExpressiveButton(
    { Text(text, maxLines = 1, overflow = TextOverflow.Ellipsis) },
    size,
    modifier,
    icon,
    loading,
    enabled,
    outlined,
    colors,
    interactionSource,
    onClick
)
