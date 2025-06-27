package younesbouhouche.musicplayer.main.presentation.util

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.collections.forEachIndexed

@Composable
fun <T>VerticalSegmentedButtons(
    items: List<T>,
    selected: T,
    onItemClick: (T) -> Unit,
    modifier: Modifier = Modifier,
    itemContent: @Composable RowScope.(T, Boolean) -> Unit
) {
    val colors = SegmentedButtonDefaults.colors()
    Column(modifier.fillMaxWidth().clip(MaterialTheme.shapes.extraLarge).border(1.dp,
        MaterialTheme.colorScheme.outline, MaterialTheme.shapes.extraLarge)) {
        items.forEachIndexed { index, item ->
            val active = item == selected
            Surface(
                color =
                if (active) colors.activeContainerColor
                else colors.inactiveContainerColor,
                contentColor =
                if (active) colors.activeContentColor
                else colors.inactiveContentColor
            ) {
                Row(
                    Modifier
                        .background(
                            if (active) colors.activeContainerColor
                            else colors.inactiveContainerColor
                        )
                        .fillMaxWidth()
                        .clickable {
                            onItemClick(item)
                        }
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.Start)
                ) {
                    ProvideTextStyle(MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    )) {
                        itemContent(item, active)
                    }
                }
            }
        }
    }
}