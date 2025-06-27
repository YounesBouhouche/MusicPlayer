package younesbouhouche.musicplayer

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.koin.android.ext.android.get
import org.koin.compose.KoinContext
import org.koin.compose.viewmodel.koinViewModel
import younesbouhouche.musicplayer.core.domain.models.Playlist
import younesbouhouche.musicplayer.main.domain.events.PlayerEvent.PlayFavorites
import younesbouhouche.musicplayer.main.domain.events.PlayerEvent.PlayMostPlayed
import younesbouhouche.musicplayer.main.domain.events.PlayerEvent.PlayPlaylist
import younesbouhouche.musicplayer.main.presentation.AppScreen
import younesbouhouche.musicplayer.main.presentation.constants.Permissions
import younesbouhouche.musicplayer.main.presentation.states.StartupEvent
import younesbouhouche.musicplayer.main.presentation.util.Event
import younesbouhouche.musicplayer.main.presentation.util.composables.CollectEvents
import younesbouhouche.musicplayer.main.presentation.util.composables.SetSystemBarColors
import younesbouhouche.musicplayer.main.presentation.util.createTempFile
import younesbouhouche.musicplayer.main.presentation.util.isPermissionGranted
import younesbouhouche.musicplayer.main.presentation.util.isRouteParent
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
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            val isParent = currentRoute.isRouteParent
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
                    else -> return@CollectEvents
                }
            }
            AppTheme {
                AppScreen(
                    granted,
                    {
                        requestPermission(permission, { mainVM.setGranted(startupEvent) }) {
                            launcher.launch(permission)
                        }
                    },
                    mainVM,
                    searchVM,
                    navController,
                    isParent,
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.hasExtra("type")) {
            when (intent.getStringExtra("type").toStartupEvent()) {
                StartupEvent.None -> Unit
                StartupEvent.PlayFavorites -> mainVM.onPlayerEvent(PlayFavorites)
                StartupEvent.PlayMostPlayed -> mainVM.onPlayerEvent(PlayMostPlayed)
                is StartupEvent.PlayPlaylist -> mainVM.onPlayerEvent(
                    PlayPlaylist(intent.getIntExtra("id", -1)),
                )
            }
        }
    }
}
