package younesbouhouche.musicplayer.core.presentation.theme

import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import younesbouhouche.musicplayer.R


@OptIn(ExperimentalTextApi::class)
val topAppBarTitleFont = TextStyle(
    fontFamily = FontFamily(
        Font(
            R.font.robotoflex,
            FontWeight.Bold,
            variationSettings =
                FontVariation.Settings(
                    FontVariation.weight(FontWeight.Bold.weight),
                    FontVariation.width(150f),
                ),
        ),
    ),
    fontWeight = FontWeight.Bold,
    fontSize = 22.sp,
)
