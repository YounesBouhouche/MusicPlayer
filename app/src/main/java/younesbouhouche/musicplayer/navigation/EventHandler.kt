package younesbouhouche.musicplayer.navigation

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
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
    launchMainScreen: () -> Unit,
) {
    var savePlaylist: Playlist? = null
    val context = LocalContext.current
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
            is Event.RequestPermissions -> {
                permissionLauncher.launch(event.permissions.toTypedArray())
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
        }
    }
}