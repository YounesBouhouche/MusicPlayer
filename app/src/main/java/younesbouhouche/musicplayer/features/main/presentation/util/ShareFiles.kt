package younesbouhouche.musicplayer.features.main.presentation.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import younesbouhouche.musicplayer.core.domain.models.MusicCard

@JvmName("shareUris")
fun Context.shareFiles(uris: List<Uri>) {
    startActivity(
        Intent.createChooser(
            Intent().apply {
                action = Intent.ACTION_SEND_MULTIPLE
                putParcelableArrayListExtra(
                    Intent.EXTRA_STREAM,
                    ArrayList(uris),
                )
                type = "audio/*"
            },
            null,
        ),
    )
}

@JvmName("shareMusicCards")
fun Context.shareFiles(files: List<MusicCard>) = shareFiles(files.map { it.contentUri })