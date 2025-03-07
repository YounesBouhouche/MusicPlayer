package younesbouhouche.musicplayer.settings.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun LargeSettingsGroup(
    items: List<LargeSettingsItem>,
    modifier: Modifier = Modifier
) {
    Column(modifier.padding(horizontal = 8.dp)) {
        items.forEachIndexed { index, item ->
            val topShape =
                if (index == 0) MaterialTheme.shapes.large
                else MaterialTheme.shapes.extraSmall
            val bottomShape =
                if (index == items.size - 1) MaterialTheme.shapes.large
                else MaterialTheme.shapes.extraSmall
            if (index != 0) Spacer(Modifier.height(4.dp))
            LargeSettingsItem(
                item,
                Modifier.clip(
                    RoundedCornerShape(
                        topStart = topShape.topStart,
                        topEnd = topShape.topEnd,
                        bottomStart = bottomShape.bottomStart,
                        bottomEnd = bottomShape.bottomEnd,
                    )
                )
            )
        }
    }
}