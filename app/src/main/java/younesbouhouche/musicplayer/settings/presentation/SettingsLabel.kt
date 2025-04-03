package younesbouhouche.musicplayer.settings.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

fun LazyListScope.settingsLabel(text: String) =
    item {
        Text(
            text.uppercase(),
            Modifier.padding(16.dp),
            style =
                MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Medium,
                ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }

fun LazyListScope.settingsLabel(text: @Composable () -> String) =
    item {
        Text(
            text().uppercase(),
            Modifier.padding(16.dp),
            style =
                MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Medium,
                ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }

fun LazyListScope.settingsLabel(text: @Composable () -> String, trailing: @Composable () -> String) =
    item {
        ProvideTextStyle(
            MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        ) {
            Row(
                Modifier.padding(16.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text().uppercase())
                Text(trailing())
            }
        }
    }
