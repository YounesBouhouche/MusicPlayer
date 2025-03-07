package younesbouhouche.musicplayer.settings.presentation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Switch
import androidx.compose.material3.VerticalDivider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

fun LazyListScope.checkSettingsItem(
    icon: ImageVector,
    title: Int,
    text: Int,
    onClick: (() -> Unit)? = null,
    checked: Boolean,
    visible: Boolean = true,
    onCheckedChange: (Boolean) -> Unit,
) {
    settingsItem(
        icon,
        title,
        text,
        visible,
        {
            if (onClick == null) {
                onCheckedChange(!checked)
            } else {
                onClick()
            }
        },
    ) {
        if (onClick != null) VerticalDivider(Modifier.fillMaxHeight(.5f))
        Spacer(Modifier.width(8.dp))
        Switch(checked, onCheckedChange)
        Spacer(Modifier.width(16.dp))
    }
}
