package younesbouhouche.musicplayer.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

fun <T> LazyListScope.settingsRadioItems(
    list: List<T>,
    selected: Int,
    onSelectedChange: (Int) -> Unit,
    label: @Composable (T) -> Unit
) {
    itemsIndexed(list) { index, item ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onSelectedChange(index) }
                .padding(4.dp, 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selected == index,
                onClick = { onSelectedChange(index) },
                colors = RadioButtonDefaults.colors(
                    selectedColor = MaterialTheme.colorScheme.primary,
                    unselectedColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            )
            Spacer(modifier = Modifier.width(4.dp))
            label(item)
        }
    }
}