package younesbouhouche.musicplayer.features.main.presentation.util.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun TitleText(
    text: String,
    modifier: Modifier = Modifier,
    background: Color = MaterialTheme.colorScheme.surface.copy(.8f)
) = Text(
    text = text,
    style = MaterialTheme.typography.titleLarge,
    fontWeight = FontWeight.Medium,
    maxLines = 1,
    overflow = TextOverflow.Ellipsis,
    modifier = modifier
        .clip(RoundedCornerShape(100))
        .background(background)
        .padding(vertical = 10.dp, horizontal = 20.dp)
)