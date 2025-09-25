package younesbouhouche.musicplayer.main.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.core.presentation.util.ExpressiveIconButton

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ListItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLongClick: (() -> Unit)? = null,
    shape: Shape = MaterialTheme.shapes.large,
    background: Color = MaterialTheme.colorScheme.surfaceContainerLow,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(12.dp),
    leadingContent: (@Composable RowScope.() -> Unit)? = null,
    trailingContent: (@Composable RowScope.() -> Unit)? = null,
    content: @Composable RowScope.() -> Unit,
) {
    Row(modifier
        .clip(shape)
        .background(background)
        .combinedClickable(onClick = onClick, onLongClick = onLongClick)
        .padding(12.dp),
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = Alignment.CenterVertically
    ) {
        leadingContent?.invoke(this)
        content()
        trailingContent?.invoke(this)
        onLongClick?.let {
            ExpressiveIconButton(
                Icons.Default.MoreVert,
                widthOption = IconButtonDefaults.IconButtonWidthOption.Narrow,
                size = IconButtonDefaults.mediumIconSize,
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                ),
                onClick = it
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ListItem(
    headline: String,
    supporting: String,
    cover: Any?,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onLongClick: (() -> Unit)? = null,
    shape: Shape = MaterialTheme.shapes.large,
    background: Color = MaterialTheme.colorScheme.surfaceContainerLow,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(12.dp),
    leadingContent: (@Composable RowScope.() -> Unit)? = null,
    trailingContent: (@Composable RowScope.() -> Unit)? = null,
    onClick: () -> Unit,
) = ListItem(
    onClick = onClick,
    modifier = modifier,
    onLongClick = onLongClick,
    shape = shape,
    background = background,
    horizontalArrangement = horizontalArrangement,
    leadingContent = {
        MyImage(
            cover,
            icon,
            Modifier.size(68.dp),
        )
        leadingContent?.invoke(this)
    },
    trailingContent = trailingContent
) {

    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically)
    ) {
        Text(
            headline,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            supporting,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodySmall
        )
    }
}
