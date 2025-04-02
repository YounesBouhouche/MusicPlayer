package younesbouhouche.musicplayer.glance.presentation

import android.graphics.drawable.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.ContentScale
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.size
import androidx.media3.session.R
import younesbouhouche.musicplayer.glance.presentation.util.toBitmap


@Composable
fun MyImage(
    model: ByteArray?,
    modifier: GlanceModifier = GlanceModifier,
    size: Dp = 64.dp
) {
    val context = LocalContext.current
    val bitmap = model?.toBitmap()
    Box(
        modifier.size(size)
            .cornerRadius(size / 4)
            .background(GlanceTheme.colors.primaryContainer),
        contentAlignment = Alignment.Center) {
        if ((model?.isEmpty() != false) or (bitmap == null)) {
            Image(
                provider = ImageProvider(
                    Icon.createWithResource(
                        context,
                        R.drawable.media_session_service_notification_ic_music_note
                    )
                ),
                contentDescription = "",
                modifier = GlanceModifier.size(size / 2),
                contentScale = ContentScale.Fit,
            )
        } else {
            Image(
                provider = ImageProvider(bitmap!!),
                contentDescription = "",
                modifier = GlanceModifier.fillMaxSize().cornerRadius(16.dp),
                contentScale = ContentScale.Fit,
            )
        }
    }
}
