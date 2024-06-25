package younesbouhouche.musicplayer.settings

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

fun LazyListScope.settingsLabel(
    text: String
) {
    item {
        Spacer(Modifier.height(16.dp))
        Text(text, modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(16.dp))
    }
}

fun LazyListScope.settingsLabel(
    text: Int
) {
    item {
        Spacer(Modifier.height(16.dp))
        Text(stringResource(text), modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.primary)
        Spacer(Modifier.height(16.dp))
    }
}