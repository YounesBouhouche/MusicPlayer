package younesbouhouche.musicplayer.main.presentation.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import soup.compose.material.motion.animation.materialSharedAxisZ

@Composable
fun EmptyContainer(
    empty: Boolean,
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    AnimatedContent(
        empty,
        modifier = modifier,
        transitionSpec = { materialSharedAxisZ(true) },
    ) {
        if (it)
            Column(
                Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    icon,
                    contentDescription = "",
                    modifier = Modifier.fillMaxWidth(.4f).aspectRatio(1f),
                    tint = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text,
                    modifier = Modifier.fillMaxWidth(.75f),
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        else
            Box(Modifier.fillMaxSize(), content = content)
    }
}