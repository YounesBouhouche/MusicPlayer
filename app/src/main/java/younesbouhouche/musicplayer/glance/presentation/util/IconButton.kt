package younesbouhouche.musicplayer.glance.presentation.util

import android.annotation.SuppressLint
import android.graphics.drawable.Icon
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.RowScope
import androidx.glance.layout.height
import androidx.glance.layout.size
import androidx.glance.unit.ColorProvider

@Composable
fun IconButton(
    @DrawableRes
    icon: Int,
    size: Dp,
    modifier: GlanceModifier = GlanceModifier,
    iconSize: Dp = size / 3,
    @SuppressLint("RestrictedApi") containerColor: ColorProvider = ColorProvider(Color.Transparent),
    contentColor: ColorProvider = GlanceTheme.colors.onSurface,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    Box(
        modifier
            .clickable(onClick)
            .cornerRadius(size)
            .background(containerColor)
            .height(size),
        contentAlignment = Alignment.Center
    ) {
        Image(
            ImageProvider(Icon.createWithResource(context, icon)),
            "",
            GlanceModifier.size(iconSize),
            colorFilter = ColorFilter.tint(contentColor)
        )
    }
}


@Composable
fun RowScope.RowIconButton(
    @DrawableRes
    icon: Int,
    size: Dp,
    modifier: GlanceModifier = GlanceModifier,
    iconSize: Dp = size / 3,
    @SuppressLint("RestrictedApi") containerColor: ColorProvider = ColorProvider(Color.Transparent),
    contentColor: ColorProvider = GlanceTheme.colors.onSurface,
    onClick: () -> Unit
) = IconButton(
    icon,
    size,
    modifier.defaultWeight(),
    iconSize,
    containerColor,
    contentColor,
    onClick
)
