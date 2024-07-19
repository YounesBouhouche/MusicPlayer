package younesbouhouche.musicplayer.settings.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

fun LazyListScope.settingsLabel(text: String) =
    item {
        Text(text, Modifier.padding(16.dp))
    }