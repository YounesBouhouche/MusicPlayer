package younesbouhouche.musicplayer

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import org.koin.android.ext.android.get
import org.koin.compose.viewmodel.koinViewModel
import younesbouhouche.musicplayer.core.domain.models.Playlist
import younesbouhouche.musicplayer.features.main.domain.events.PlayerEvent
import younesbouhouche.musicplayer.features.main.domain.events.PlaylistEvent
import younesbouhouche.musicplayer.features.main.presentation.constants.Permissions
import younesbouhouche.musicplayer.features.main.presentation.states.StartupEvent
import younesbouhouche.musicplayer.features.main.presentation.util.Event
import younesbouhouche.musicplayer.features.main.presentation.util.composables.CollectEvents
import younesbouhouche.musicplayer.features.main.presentation.util.composables.SetSystemBarColors
import younesbouhouche.musicplayer.features.main.presentation.util.createTempFile
import younesbouhouche.musicplayer.features.main.presentation.util.isPermissionGranted
import younesbouhouche.musicplayer.features.main.presentation.util.parsePlaylistFile
import younesbouhouche.musicplayer.features.main.presentation.util.requestPermission
import younesbouhouche.musicplayer.features.main.presentation.util.shareFile
import younesbouhouche.musicplayer.features.main.presentation.util.toStartupEvent
import younesbouhouche.musicplayer.features.main.presentation.viewmodel.MainViewModel
import younesbouhouche.musicplayer.navigation.AppNavGraph
import younesbouhouche.musicplayer.navigation.routes.Graph
import younesbouhouche.musicplayer.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    private lateinit var mainVM: MainViewModel
    private val permission = Permissions.audioPermission
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val isGranted = isPermissionGranted(permission)
        val startupEvent = intent.getStringExtra("type").toStartupEvent()
        setContent {
            SetSystemBarColors(dataStore = get())
            mainVM = koinViewModel<MainViewModel>()
            val navController = rememberNavController()
            var savePlaylist: Playlist? = null
            val launcher =
                rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestPermission(),
                ) { isGranted: Boolean ->
                    if (isGranted) {
                        mainVM.setGranted(startupEvent)
                        navController.navigate(Graph.Main) {
                            popUpTo(Graph.Permissions) {
                                inclusive = true
                            }
                        }
                    }
                }
            LaunchedEffect(Unit) {
                if (isGranted) {
                    mainVM.setGranted(startupEvent)
                    navController.navigate(Graph.Main) {
                        popUpTo(Graph.Permissions) {
                            inclusive = true
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
                            contentResolver.openOutputStream(uri)?.run {
                                write(playlist.createM3UText().toByteArray())
                                close()
                            }
                        }
                    }
                }
            val importPlaylistLauncher =
                rememberLauncherForActivityResult(
                    ActivityResultContracts.OpenDocument()
                ) { uri: Uri? ->
                    uri?.let {
                        val content =
                            contentResolver.openInputStream(uri)?.bufferedReader()?.readText()
                                ?: return@let
                        val (name, items) = content.parsePlaylistFile()
                        mainVM.onPlaylistEvent(PlaylistEvent.CreateNew(name, items, null))
                    }
                }
            CollectEvents { event ->
                when (event) {
                    Event.Initiate -> {
                        requestPermission(
                            permission,
                            {
                                navController.navigate(Graph.Main) {
                                    popUpTo(Graph.Permissions) {
                                        inclusive = true
                                    }
                                }
                                mainVM.setGranted(startupEvent)
                            },
                            {
                                startActivity(
                                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                        data = Uri.fromParts("package", packageName, null)
                                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    },
                                )
                            },
                        ) {
                            launcher.launch(permission)
                        }
                    }

                    is Event.SavePlaylist -> {
                        savePlaylist = event.playlist
                        savePlaylistDialog.launch("${event.playlist.name}.m3u")
                    }

                    is Event.SharePlaylist -> {
                        shareFile(
                            createTempFile(
                                "${event.playlist.name}.m3u",
                                event.playlist.createM3UText()
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
            AppTheme {
                Surface(Modifier.fillMaxSize()) {
                    AppNavGraph(navController, mainVM, Modifier.fillMaxSize())
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.hasExtra("type")) {
            when (intent.getStringExtra("type").toStartupEvent()) {
                StartupEvent.None -> Unit
                StartupEvent.PlayFavorites -> mainVM.onPlayerEvent(PlayerEvent.PlayFavorites)
                StartupEvent.PlayMostPlayed -> mainVM.onPlayerEvent(PlayerEvent.PlayMostPlayed)
                is StartupEvent.PlayPlaylist -> mainVM.onPlayerEvent(
                    PlayerEvent.PlayPlaylist(intent.getIntExtra("id", -1)),
                )
            }
        }
    }
}