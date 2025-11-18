package younesbouhouche.musicplayer.features.main.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.younesb.mydesignsystem.presentation.components.ButtonsRow
import younesbouhouche.musicplayer.R

@Composable
fun DialogButtons(
    modifier: Modifier = Modifier,
    cancelListener: (() -> Unit)? = null,
    cancelText: String = stringResource(R.string.cancel),
    okListener: (() -> Unit)? = null,
    neutral: (@Composable RowScope.() -> Unit)? = null,
) {
    Row(
        modifier.fillMaxWidth().padding(24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        val buttons = remember { mutableListOf<Triple<String, Boolean, () -> Unit>>() }
        cancelListener?.let {
            buttons += Triple(cancelText, true, it)
        }
        okListener?.let {
            buttons += Triple(stringResource(R.string.ok), false, it)
        }
        neutral?.invoke(this)
        ButtonsRow(
            buttons.size,
            { null },
            { buttons[it].first },
            modifier = Modifier.fillMaxWidth(),
            outlined = { buttons[it].second },
            onClick = { buttons[it].third() },
        )
    }
}
