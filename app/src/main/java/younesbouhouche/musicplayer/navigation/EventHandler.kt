package younesbouhouche.musicplayer.navigation

import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import younesbouhouche.musicplayer.core.domain.models.Playlist
import younesbouhouche.musicplayer.features.main.presentation.util.Event
import younesbouhouche.musicplayer.features.main.presentation.util.composables.CollectEvents
import younesbouhouche.musicplayer.features.main.presentation.util.createTempFile
import younesbouhouche.musicplayer.features.main.presentation.util.parsePlaylistFile
import younesbouhouche.musicplayer.features.main.presentation.util.shareFile
import younesbouhouche.musicplayer.features.permissions.presentation.Permissions

@Composable
fun EventHandler(
    onCreatePlaylist: (String, List<String>) -> Unit,
    onShowSnackBar: (String) -> Unit,
    launchMainScreen: () -> Unit,
) {
    var savePlaylist: Playlist? = null
    var pendingWriteAction by remember { mutableStateOf<(() -> Unit)?>(null) }
    val context = LocalContext.current

    val writePermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartIntentSenderForResult(),
        ) { result ->
            if (result.resultCode == android.app.Activity.RESULT_OK) {
                pendingWriteAction?.invoke()
                pendingWriteAction = null
            } else {
                onShowSnackBar("Permission denied")
                pendingWriteAction = null
            }
        }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions(),
        ) { permissions ->
            if (permissions[Permissions.AUDIO.permission] == true) {
                launchMainScreen()
            }
        }
    val importPlaylistLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.OpenDocument(),
        ) { uri ->
            uri?.let {
                context.contentResolver.openInputStream(uri)?.bufferedReader().use { reader ->
                    val content = reader?.readText()
                    content?.let {
                        val (name, items) = content.parsePlaylistFile()
                        onCreatePlaylist(name, items)
                    }
                }
            }
        }
    val savePlaylistDialog =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.CreateDocument("audio/x-mpegurl"),
        ) {
            it?.let { uri ->
                savePlaylist?.let { playlist ->
                    context.contentResolver.openOutputStream(uri)?.run {
                        write(playlist.createM3UContent().toByteArray())
                        close()
                    }
                }
            }
        }
    CollectEvents { event ->
        when (event) {
            Event.Initiate -> {
                if (Permissions.AUDIO.isGranted(context)) {
                    launchMainScreen()
                } else {
                    permissionLauncher.launch(
                        arrayOf(
                            Permissions.AUDIO.permission,
                            Permissions.NOTIFICATIONS.permission,
                        )
                    )
                }
            }
            is Event.SavePlaylist -> {
                savePlaylist = event.playlist
                savePlaylistDialog.launch("${event.playlist.name}.m3u")
            }
            is Event.SharePlaylist -> {
                context.shareFile(
                    context.createTempFile(
                        "${event.playlist.name}.m3u",
                        event.playlist.createM3UContent()
                    ),
                    "audio/x-mpegurl"
                )
            }
            Event.LaunchPlaylistDialog -> {
                importPlaylistLauncher.launch(
                    arrayOf(
                        "audio/x-mpegurl",
                        "audio/x-scpls",
                        "text/plain"
                    )
                )
            }

            is Event.ShowSnackBar -> {
                onShowSnackBar(event.message)
            }

            is Event.RequestWritePermission -> {
                try {
                    val uris = listOf(event.uri)
                    val intentSender = MediaStore.createWriteRequest(
                        context.contentResolver,
                        uris
                    ).intentSender
                    pendingWriteAction = event.onGranted
                    writePermissionLauncher.launch(
                        IntentSenderRequest.Builder(intentSender).build()
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                    onShowSnackBar("Failed to request permission")
                }
            }
        }
    }
}