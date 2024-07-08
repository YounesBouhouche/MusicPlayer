package younesbouhouche.musicplayer.core.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun RadioContainer(
    modifier: Modifier = Modifier,
    selected: Boolean,
    onSelected: () -> Unit = {},
    text: String,
    trailingContent: @Composable RowScope.() -> Unit = {},
) {
    Box(modifier.fillMaxWidth().clickable(onClick = onSelected)) {
        Row(
            Modifier.fillMaxWidth().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            RadioButton(selected = selected, onClick = onSelected)
            Text(text, Modifier.fillMaxWidth().weight(1f))
            trailingContent()
        }
    }
}
