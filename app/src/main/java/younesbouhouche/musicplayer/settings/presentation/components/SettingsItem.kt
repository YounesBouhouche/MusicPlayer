package younesbouhouche.musicplayer.settings.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import younesbouhouche.musicplayer.settings.presentation.util.Checked
import younesbouhouche.musicplayer.settings.presentation.util.SettingData


@Composable
fun SettingsItem(
    headline: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    checked: Checked? = null,
    role: Role? = null,
    supporting: String? = null,
    background: Color = MaterialTheme.colorScheme.surfaceContainer,
    shape: Shape = MaterialTheme.shapes.large,
    onClick: () -> Unit = {},
    separator: Boolean = false,
    useCheckbox: Boolean = false,
    leadingContent: (@Composable () -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null,
) {
    val alpha by animateFloatAsState(if (enabled) 1f else .5f)
    Row(
        modifier
            .fillMaxWidth()
            .clip(shape)
            .background(background)
            .alpha(alpha)
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                role = role,
                onClick = onClick,
                enabled = enabled
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
    ) {
        leadingContent?.invoke()
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = headline,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            supporting?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        if (separator) VerticalDivider(Modifier.fillMaxHeight(.5f))
        trailingContent?.invoke() ?: checked?.let {
            if (it.radio)
                RadioButton(
                    it.checked,
                    { it.onCheckedChange(null) },
                    interactionSource = interactionSource
                )
            else if (useCheckbox)
                Checkbox(
                    it.checked,
                    it.onCheckedChange,
                    interactionSource = interactionSource
                )
            else
                Switch(
                    it.checked,
                    it.onCheckedChange,
                    interactionSource = interactionSource
                )
        }
    }
}

@Composable
fun SettingsItem(
    headline: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    checked: Checked? = null,
    role: Role? = null,
    supporting: String? = null,
    background: Color = MaterialTheme.colorScheme.surfaceContainer,
    iconTint: Color = MaterialTheme.colorScheme.onSurface,
    iconBackground: Color = Color.Transparent,
    shape: Shape = MaterialTheme.shapes.large,
    onClick: () -> Unit = {},
    large: Boolean = false,
    useCheckbox: Boolean = false,
    separator: Boolean = false,
    trailingContent: (@Composable () -> Unit)? = null,
) = SettingsItem(
    headline,
    modifier,
    enabled,
    interactionSource,
    checked,
    role,
    supporting,
    background,
    shape,
    onClick,
    separator,
    useCheckbox = useCheckbox,
    leadingContent = icon?.let {
        {
            Box(Modifier.clip(CircleShape)
                .background(iconBackground)
                .size(if (large) 60.dp else 48.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(if (large) 32.dp else 24.dp)
                )
            }
        }
    },
    trailingContent,
)

@Composable
fun SettingsItem(
    data: SettingData,
    modifier: Modifier = Modifier,
    background: Color = MaterialTheme.colorScheme.surfaceContainer,
    iconTint: Color = MaterialTheme.colorScheme.onSurface,
    iconBackground: Color = Color.Transparent,
    shape: Shape = MaterialTheme.shapes.large,
    separator: Boolean = false,
) {
    val context = LocalContext.current
    val interactionSource = remember { MutableInteractionSource() }
    SettingsItem(
        icon = data.icon,
        headline = stringResource(data.headline),
        supporting = data.supporting?.let { stringResource(it) },
        modifier = modifier,
        enabled = data.enabled,
        interactionSource = interactionSource,
        role = data.checked?.let { if (it.radio) Role.RadioButton else Role.Checkbox },
        checked = data.checked,
        background = background,
        iconTint = iconTint,
        iconBackground = iconBackground,
        shape = shape,
        onClick = {
            data.onClick(context)
        },
        large = data.large,
        separator = separator,
        trailingContent = data.trailingContent
    )
}

@Preview
@Composable
private fun SettingsItemPreview() {
}