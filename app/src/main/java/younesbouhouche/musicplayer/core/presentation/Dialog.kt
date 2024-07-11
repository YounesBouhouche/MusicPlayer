package younesbouhouche.musicplayer.core.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.core.presentation.util.composables.isCompact

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Dialog(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    header: @Composable (ColumnScope.() -> Unit)? = null,
    title: String,
    trailingContent: @Composable (RowScope.() -> Unit)? = null,
    centerTitle: Boolean = false,
    cancelListener: (() -> Unit)? = null,
    cancelText: String = stringResource(R.string.cancel),
    okListener: (() -> Unit)? = null,
    neutral: @Composable (() -> Unit)? = null,
    content: @Composable (ColumnScope.() -> Unit),
) {
    if (visible) {
        BasicAlertDialog(
            onDismissRequest = onDismissRequest,
            modifier =
                Modifier
                    .background(
                        MaterialTheme.colorScheme.background,
                        RoundedCornerShape(24.dp),
                    )
                    .clip(RoundedCornerShape(24.dp))
                    .clipToBounds()
                    .fillMaxWidth(0.9f)
                    .widthIn(min = 280.dp, max = 560.dp),
            properties =
                DialogProperties(
                    usePlatformDefaultWidth = !isCompact,
                ),
        ) {
            Surface(
                contentColor = MaterialTheme.colorScheme.onBackground,
                color = Color.Transparent,
            ) {
                Column(Modifier.fillMaxWidth()) {
                    if (header == null) {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleLarge,
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .weight(1f),
                                textAlign = if (centerTitle) TextAlign.Center else null,
                            )
                            if (trailingContent != null) {
                                trailingContent(this)
                            }
                        }
                    } else {
                        header(this)
                    }
                    content(this)
                    DialogButtons(cancelListener, cancelText, okListener, neutral)
                }
            }
        }
    }
}
