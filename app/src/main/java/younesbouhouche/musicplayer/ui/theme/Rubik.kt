package younesbouhouche.musicplayer.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import younesbouhouche.musicplayer.R

@OptIn(ExperimentalTextApi::class)
val Rubik =
    FontFamily(
        Font(
            R.font.rubik,
            FontWeight.Normal,
            variationSettings =
                FontVariation.Settings(
                    FontVariation.weight(FontWeight.Normal.weight),
                ),
        ),
        Font(
            R.font.rubik,
            FontWeight.SemiBold,
            variationSettings =
                FontVariation.Settings(
                    FontVariation.weight(FontWeight.SemiBold.weight),
                ),
        ),
        Font(
            R.font.rubik,
            FontWeight.Medium,
            variationSettings =
                FontVariation.Settings(
                    FontVariation.weight(FontWeight.Medium.weight),
                ),
        ),
        Font(
            R.font.rubik,
            FontWeight.Bold,
            variationSettings =
                FontVariation.Settings(
                    FontVariation.weight(FontWeight.Bold.weight),
                ),
        ),
        Font(
            R.font.rubik,
            FontWeight.ExtraBold,
            variationSettings =
                FontVariation.Settings(
                    FontVariation.weight(FontWeight.ExtraBold.weight),
                ),
        ),
        Font(
            R.font.rubik,
            FontWeight.Black,
            variationSettings =
                FontVariation.Settings(
                    FontVariation.weight(FontWeight.Black.weight),
                ),
        ),
        Font(
            R.font.rubik,
            FontWeight.Light,
            variationSettings =
                FontVariation.Settings(
                    FontVariation.weight(FontWeight.Light.weight),
                ),
        ),
        Font(
            R.font.rubik,
            FontWeight.ExtraLight,
            variationSettings =
                FontVariation.Settings(
                    FontVariation.weight(FontWeight.ExtraLight.weight),
                ),
        ),
        Font(
            R.font.rubik,
            FontWeight.Thin,
            variationSettings =
                FontVariation.Settings(
                    FontVariation.weight(FontWeight.Thin.weight),
                ),
        ),
    )

fun rubikTypography(typography: Typography) =
    Typography(
        displayLarge =
            typography.displayLarge.copy(
                fontFamily = Rubik,
            ),
        displayMedium =
            typography.displayMedium.copy(
                fontFamily = Rubik,
            ),
        displaySmall =
            typography.displaySmall.copy(
                fontFamily = Rubik,
            ),
        headlineLarge =
            typography.headlineLarge.copy(
                fontFamily = Rubik,
            ),
        headlineMedium =
            typography.headlineMedium.copy(
                fontFamily = Rubik,
            ),
        headlineSmall =
            typography.headlineSmall.copy(
                fontFamily = Rubik,
            ),
        titleLarge =
            typography.titleLarge.copy(
                fontFamily = Rubik,
            ),
        titleMedium =
            typography.titleMedium.copy(
                fontFamily = Rubik,
            ),
        titleSmall =
            typography.titleSmall.copy(
                fontFamily = Rubik,
            ),
        bodyLarge =
            typography.bodyLarge.copy(
                fontFamily = Rubik,
            ),
        bodyMedium =
            typography.bodyMedium.copy(
                fontFamily = Rubik,
            ),
        bodySmall =
            typography.bodySmall.copy(
                fontFamily = Rubik,
            ),
        labelLarge =
            typography.labelLarge.copy(
                fontFamily = Rubik,
            ),
        labelMedium =
            typography.labelMedium.copy(
                fontFamily = Rubik,
            ),
        labelSmall =
            typography.labelSmall.copy(
                fontFamily = Rubik,
            ),
    )
