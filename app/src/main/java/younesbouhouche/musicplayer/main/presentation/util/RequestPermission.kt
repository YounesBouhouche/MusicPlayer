package younesbouhouche.musicplayer.main.presentation.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

fun Activity.requestPermission(
    permission: String,
    onGranted: () -> Unit,
    onDenied: () -> Unit
) {
    when {
        ContextCompat.checkSelfPermission(this, permission) ==
                PackageManager.PERMISSION_GRANTED -> onGranted()
        ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            permission,
        ) -> {
            startActivity(
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                },
            )
        }
        else -> onDenied()
    }
}