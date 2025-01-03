package younesbouhouche.musicplayer

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import younesbouhouche.musicplayer.core.presentation.util.composables.SetSystemBarColors
import younesbouhouche.musicplayer.main.domain.events.PlayerEvent
import younesbouhouche.musicplayer.main.domain.models.Routes
import younesbouhouche.musicplayer.main.presentation.AppScreen
import younesbouhouche.musicplayer.main.presentation.states.StartupEvent
import younesbouhouche.musicplayer.main.presentation.viewmodel.MainVM
import younesbouhouche.musicplayer.main.presentation.viewmodel.NavigationVM
import younesbouhouche.musicplayer.settings.data.SettingsDataStore
import younesbouhouche.musicplayer.ui.theme.AppTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var settingsDataStore: SettingsDataStore
    private lateinit var mainVM: MainVM
    private val permission =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            android.Manifest.permission.READ_MEDIA_AUDIO
        } else {
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val isGranted =
            ContextCompat
                .checkSelfPermission(this@MainActivity, permission) == PackageManager.PERMISSION_GRANTED
        val startupEvent =
            when (intent.getStringExtra("type")) {
                "favorites" -> StartupEvent.PlayFavorites
                "mostPlayed" -> StartupEvent.PlayMostPlayed
                "playlist" -> StartupEvent.PlayPlaylist(intent.getIntExtra("id", -1))
                else -> StartupEvent.None
            }
        enableEdgeToEdge()
        setContent {
            SetSystemBarColors(dataStore = settingsDataStore)
            mainVM = hiltViewModel<MainVM>()
            val playerState by mainVM.playerState.collectAsState()
            val granted by mainVM.granted.collectAsState()
            val navController = rememberNavController()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            val isParent =
                currentRoute?.let { route ->
                    Routes.entries.map { it.destination.javaClass.kotlin.qualifiedName }.contains(route)
                } != false
            val navigationVM = hiltViewModel<NavigationVM>()
            val navigationState by navigationVM.state.collectAsState()
            val launcher =
                rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestPermission(),
                ) { isGranted: Boolean ->
                    if (isGranted) mainVM.setGranted(startupEvent)
                }
            LaunchedEffect(key1 = Unit) {
                println("ViewModel PlayState: ${playerState.playState}")
            }
            LaunchedEffect(key1 = currentRoute) {
                Routes
                    .entries
                    .firstOrNull { currentRoute == it.destination::class.qualifiedName }
                    ?.let {
                        navigationVM.update(it.index)
                    }
            }
            LaunchedEffect(Unit) {
                if (isGranted) mainVM.setGranted(startupEvent)
            }
            AppTheme {
                AppScreen(
                    granted,
                    {
                        when {
                            ContextCompat.checkSelfPermission(this, permission) ==
                                PackageManager.PERMISSION_GRANTED ->
                                mainVM.setGranted(startupEvent)
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
                            else -> launcher.launch(permission)
                        }
                    },
                    mainVM,
                    navController,
                    isParent,
                    navigationState,
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.hasExtra("type")) {
            when (intent.getStringExtra("type")) {
                "favorites" -> mainVM.onPlayerEvent(PlayerEvent.PlayFavorites)
                "mostPlayed" -> mainVM.onPlayerEvent(PlayerEvent.PlayMostPlayed)
                "playlist" ->
                    mainVM.onPlayerEvent(
                        PlayerEvent.PlayPlaylist(intent.getIntExtra("id", -1)),
                    )
            }
        }
    }
}
