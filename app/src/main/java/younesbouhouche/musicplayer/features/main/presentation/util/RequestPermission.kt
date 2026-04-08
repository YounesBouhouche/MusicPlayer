package younesbouhouche.musicplayer.features.main.presentation.util

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

fun Activity.requestPermission(
    permission: String,
    onGranted: () -> Unit,
    onDenied: () -> Unit,
    onRequest: () -> Unit,
) = when {
    ContextCompat.checkSelfPermission(this, permission) ==
        PackageManager.PERMISSION_GRANTED -> onGranted()
    ActivityCompat.shouldShowRequestPermissionRationale(
        this,
        permission,
    ) -> onDenied()
    else -> onRequest()
}
