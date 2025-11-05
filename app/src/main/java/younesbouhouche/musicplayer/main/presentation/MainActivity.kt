package younesbouhouche.musicplayer.main.presentation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import org.koin.android.ext.android.get
import org.koin.compose.viewmodel.koinViewModel
import younesbouhouche.musicplayer.core.domain.models.Playlist
import younesbouhouche.musicplayer.main.domain.events.PlayerEvent
import younesbouhouche.musicplayer.main.domain.events.PlaylistEvent
import younesbouhouche.musicplayer.main.presentation.constants.Permissions
import younesbouhouche.musicplayer.main.presentation.layout.AppScreen
import younesbouhouche.musicplayer.main.presentation.states.StartupEvent
import younesbouhouche.musicplayer.main.presentation.util.Event
import younesbouhouche.musicplayer.main.presentation.util.composables.CollectEvents
import younesbouhouche.musicplayer.main.presentation.util.composables.SetSystemBarColors
import younesbouhouche.musicplayer.main.presentation.util.createTempFile
import younesbouhouche.musicplayer.main.presentation.util.isPermissionGranted
import younesbouhouche.musicplayer.main.presentation.util.parsePlaylistFile
import younesbouhouche.musicplayer.main.presentation.util.requestPermission
import younesbouhouche.musicplayer.main.presentation.util.shareFile
import younesbouhouche.musicplayer.main.presentation.util.toStartupEvent
import younesbouhouche.musicplayer.main.presentation.viewmodel.MainViewModel
import younesbouhouche.musicplayer.main.presentation.viewmodel.SearchVM
import younesbouhouche.musicplayer.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    private lateinit var mainVM: MainViewModel
    private lateinit var searchVM: SearchVM
    private val permission = Permissions.audioPermission
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val isGranted = isPermissionGranted(permission)
        val startupEvent = intent.getStringExtra("type").toStartupEvent()
        setContent {
            SetSystemBarColors(dataStore = get())
            mainVM = koinViewModel<MainViewModel>()
            searchVM = koinViewModel<SearchVM>()
            val granted by mainVM.granted.collectAsState()
            val navController = rememberNavController()
            var savePlaylist: Playlist? = null
            val launcher =
                rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestPermission(),
                ) { isGranted: Boolean ->
                    if (isGranted) mainVM.setGranted(startupEvent)
                }
            LaunchedEffect(Unit) {
                if (isGranted) mainVM.setGranted(startupEvent)
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
                        val content = contentResolver.openInputStream(uri)?.bufferedReader()?.readText() ?: return@let
                        val (name, items) = content.parsePlaylistFile()
                        mainVM.onPlaylistEvent(PlaylistEvent.CreateNew(name, items, null))
                    }
                }
            CollectEvents { event ->
                when (event) {
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

                    is Event.Navigate -> navController.navigate(event.route)

                    Event.LaunchPlaylistDialog -> {
                        importPlaylistLauncher.launch(arrayOf("audio/x-mpegurl", "audio/x-scpls", "text/plain"))
                    }

                    else -> return@CollectEvents
                }
            }
            AppTheme {
                Surface(Modifier.fillMaxSize()) {
                    AnimatedContent(granted) {
                        if (it)
                            AppScreen(mainVM, searchVM, navController)
                        else
                            RequestPermissionScreen {
                                requestPermission(
                                    
                                    permission,
                                    { mainVM.setGranted(startupEvent) },
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
                    }
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