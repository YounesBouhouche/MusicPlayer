package younesbouhouche.musicplayer.features.glance.presentation.util

import android.graphics.BitmapFactory
import android.graphics.drawable.Icon
import android.net.Uri
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


@Composable
fun MyImage(
    model: Uri?,
    modifier: GlanceModifier = GlanceModifier,
    opacity: Float,
    size: Dp = 64.dp
) {
    val context = LocalContext.current
    val bitmap = model?.let {
        try {
            val inputStream = context.contentResolver.openInputStream(it)
            BitmapFactory.decodeStream(inputStream)
        } catch (_: Exception) {
            null
        }
    }
    Box(
        modifier.size(size)
            .cornerRadius(size / 4)
            .background(
                GlanceTheme.colors.primaryContainer.getColor(context).copy(alpha = opacity)
            ),
        contentAlignment = Alignment.Center
    ) {
        if (bitmap == null) {
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
                provider = ImageProvider(bitmap),
                contentDescription = "",
                modifier = GlanceModifier.fillMaxSize().cornerRadius(16.dp),
                contentScale = ContentScale.Fit,
            )
        }
    }
}