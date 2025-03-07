package younesbouhouche.musicplayer.settings.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

fun <T> LazyListScope.settingsRadioItems(
    items: List<T>,
    selected: Int,
    onSelectedChange: (Int) -> Unit,
    label: @Composable (T) -> Unit,
) {
    itemsIndexed(items) { index, item ->
        val topShape =
            if (index == 0) MaterialTheme.shapes.large
            else MaterialTheme.shapes.extraSmall
        val bottomShape =
            if (index == items.size - 1) MaterialTheme.shapes.large
            else MaterialTheme.shapes.extraSmall
        if (index != 0) Spacer(Modifier.height(2.dp))
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = topShape.topStart,
                            topEnd = topShape.topEnd,
                            bottomStart = bottomShape.bottomStart,
                            bottomEnd = bottomShape.bottomEnd,
                        )
                    )
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .clickable { onSelectedChange(index) }
                    .padding(start = 16.dp, end = 4.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            ProvideTextStyle(MaterialTheme.typography.bodyLarge) {
                label(item)
            }
            RadioButton(
                selected = selected == index,
                onClick = { onSelectedChange(index) },
                colors =
                RadioButtonDefaults.colors(
                    selectedColor = MaterialTheme.colorScheme.primary,
                    unselectedColor = MaterialTheme.colorScheme.onSecondaryContainer,
                ),
            )
        }
    }
}
