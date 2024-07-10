package younesbouhouche.musicplayer.settings.presentation

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.settings.data.SettingsDataStore
import younesbouhouche.musicplayer.ui.theme.AppTheme

class AboutActivity : AppCompatActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val listState = rememberLazyListState()
            val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
            val context = LocalContext.current
            val dataStore = SettingsDataStore(LocalContext.current)
            val isDark =
                when (dataStore.theme.collectAsState(initial = "system").value) {
                    "light" -> false
                    "dark" -> true
                    else -> isSystemInDarkTheme()
                }
            DisposableEffect(isDark) {
                enableEdgeToEdge(
                    statusBarStyle =
                        if (!isDark) {
                            SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
                        } else {
                            SystemBarStyle.dark(Color.TRANSPARENT)
                        },
                    navigationBarStyle =
                        if (!isDark) {
                            SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
                        } else {
                            SystemBarStyle.dark(Color.TRANSPARENT)
                        },
                )
                onDispose { }
            }
            AppTheme {
                Scaffold(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .nestedScroll(scrollBehavior.nestedScrollConnection),
                    contentWindowInsets = WindowInsets(0, 0, 0, 0),
                    topBar = {
                        Column {
                            TopAppBar(
                                title = {
                                    Text(
                                        stringResource(id = R.string.about),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                },
                                navigationIcon = {
                                    IconButton(onClick = { (context as Activity).finish() }) {
                                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                                    }
                                },
                                scrollBehavior = scrollBehavior,
                            )
                        }
                    },
                ) { paddingValues ->
                    LazyColumn(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(paddingValues)
                                .padding(horizontal = 16.dp),
                        state = listState,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        item {
                            AppIcon()
                        }
                        item {
                            Text(
                                "MusicPlayer",
                                Modifier.fillMaxWidth(),
                                style = MaterialTheme.typography.headlineLarge,
                                textAlign = TextAlign.Center,
                            )
                        }
                        item {
                            AboutCard(
                                "Developer",
                                "Younes Bouhouche",
                                Icons.Default.Person,
                            ) {
                            }
                        }
                        item {
                            AboutCard(
                                "Project",
                                "Source Code",
                                Icons.Default.Code,
                            ) {
                            }
                        }
                        item {
                            AboutCard(
                                "Contact me",
                                "younes.bouhouche12@gmail.com",
                                Icons.Default.Mail,
                            ) {
                                with(
                                    Intent(Intent.ACTION_SENDTO).apply {
                                        data = Uri.parse("mailto:")
                                        putExtra(
                                            Intent.EXTRA_EMAIL,
                                            arrayOf("younes.bouhouche12@gmail.com"),
                                        )
                                        putExtra(
                                            Intent.EXTRA_SUBJECT,
                                            "Feedback about Music Player app",
                                        )
                                        putExtra(
                                            Intent.EXTRA_TEXT,
                                            "\nApp Version:${
                                                (
                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                                        context.packageManager.getPackageInfo(
                                                            context.packageName,
                                                            PackageManager.PackageInfoFlags.of(0),
                                                        )
                                                    } else {
                                                        context.packageManager.getPackageInfo(context.packageName, 0)
                                                    }
                                                ).versionName
                                            }\nAPI Level:${Build.VERSION.SDK_INT}",
                                        )
                                    },
                                ) {
                                    if (this.resolveActivity(context.packageManager) != null) {
                                        context.startActivity(this)
                                    }
                                }
                            }
                        }
                        item {
                            AboutCard(
                                "Social Media",
                                "@younesbouh_05",
                                Icons.Default.Link,
                                trailingContent = {
                                    IconButton(onClick = {
                                        with(
                                            Intent(
                                                Intent.ACTION_VIEW,
                                                Uri.parse("twitter://user?screen_name=younesbouh_05"),
                                            ),
                                        ) {
                                            if (this.resolveActivity(context.packageManager) != null) {
                                                context.startActivity(this)
                                            } else {
                                                context.startActivity(
                                                    Intent(
                                                        Intent.ACTION_VIEW,
                                                        Uri.parse("https://twitter.com/younesbouh_05"),
                                                    ),
                                                )
                                            }
                                        }
                                    }) {
                                        Icon(
                                            ImageVector.vectorResource(id = R.drawable.ic_twitter),
                                            null,
                                        )
                                    }
                                    IconButton(onClick = {
                                        context.startActivity(
                                            Intent(
                                                Intent.ACTION_VIEW,
                                                Uri.parse("tg://resolve?domain=younesbouh_05"),
                                            ),
                                        )
                                    }) {
                                        Icon(
                                            ImageVector.vectorResource(id = R.drawable.ic_telegram_app),
                                            null,
                                        )
                                    }
                                },
                            ) {}
                        }
                    }
                }
            }
        }
    }
}
