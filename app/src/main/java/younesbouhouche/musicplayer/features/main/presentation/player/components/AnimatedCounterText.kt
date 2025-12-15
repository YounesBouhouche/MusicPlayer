package younesbouhouche.musicplayer.features.main.presentation.player.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import younesbouhouche.musicplayer.features.main.presentation.util.intUpDownTransSpec


@Composable
fun AnimatedCounterText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    color: Color = LocalTextStyle.current.color,
    textAlign: TextAlign = LocalTextStyle.current.textAlign,
) {
    Row(
        modifier,
        horizontalArrangement = when (textAlign) {
            TextAlign.Start -> Arrangement.Start
            TextAlign.End -> Arrangement.End
            TextAlign.Center -> Arrangement.Center
            else -> Arrangement.Start
        }
    ) {
        text.forEach { char ->
            if (char.isDigit()) {
                AnimatedContent(
                    targetState = char.digitToInt(),
                    transitionSpec = {
                        intUpDownTransSpec() using SizeTransform(clip = false)
                    },
                ) { targetChar ->
                    Text(
                        targetChar.toString(),
                        style = style,
                        color = color,
                    )
                }
            } else {
                Text(
                    char.toString(),
                    style = style,
                    color = color,
                )
            }
        }
    }
}