package younesbouhouche.musicplayer.features.permissions.presentation

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.core.content.ContextCompat

enum class Permissions(
    val permission: String
) {
    AUDIO(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_AUDIO
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
    ),
    NOTIFICATIONS(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.POST_NOTIFICATIONS
        } else {
            Manifest.permission.ACCESS_NOTIFICATION_POLICY
        }
    );

    fun isGranted(context: Context): Boolean =
        ContextCompat.checkSelfPermission(context, permission) ==
                android.content.pm.PackageManager.PERMISSION_GRANTED

    fun isDenied(context: Context): Boolean =
        ContextCompat.checkSelfPermission(context, permission) ==
                android.content.pm.PackageManager.PERMISSION_DENIED

    companion object {
        fun getGrantedPermissions(context: Context): Set<Permissions> =
            Permissions.entries.filter { it.isGranted(context) }.toSet()

        fun getDeniedPermissions(context: Context): Set<Permissions> =
            Permissions.entries.filter { it.isDenied(context) }.toSet()

        fun fromPermissionString(permission: String): Permissions? =
            Permissions.entries.find { it.permission == permission }
    }
}