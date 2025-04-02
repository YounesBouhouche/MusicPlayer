package younesbouhouche.musicplayer.glance.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextDefaults.defaultTextStyle
import androidx.glance.unit.ColorProvider

@Composable
fun WidgetText(
    text: String,
    modifier: GlanceModifier = GlanceModifier,
    fontWeight: FontWeight = FontWeight.Normal,
    fontSize: Float? = null,
    textAlign: TextAlign = TextAlign.Start,
    color: ColorProvider = GlanceTheme.colors.onBackground
) {
    Text(
        text,
        modifier,
        style = defaultTextStyle.copy(
            color = color,
            fontWeight = fontWeight,
            fontSize = fontSize?.let { TextUnit(fontSize, TextUnitType.Sp) },
            textAlign = textAlign,
        ),
        maxLines = 1,
    )
}