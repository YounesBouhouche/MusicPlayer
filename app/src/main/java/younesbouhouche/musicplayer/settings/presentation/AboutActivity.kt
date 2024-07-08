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
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.rounded.Apps
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background),
                ) {
                    Scaffold(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .nestedScroll(scrollBehavior.nestedScrollConnection),
                        contentWindowInsets = WindowInsets(0, 0, 0, 0),
                        topBar = {
                            Column {
                                LargeTopAppBar(
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
                                    .padding(paddingValues),
                            state = listState,
                        ) {
                            item {
                                OutlinedCard(
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                ) {
                                    Spacer(Modifier.height(16.dp))
                                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                        Spacer(Modifier.width(4.dp))
                                        // Icon(ImageVector.vectorResource(R.drawable.ic_app_icon), null, modifier = Modifier.size(60.dp))
                                        Spacer(Modifier.width(12.dp))
                                        Column(
                                            modifier =
                                                Modifier
                                                    .fillMaxWidth()
                                                    .weight(1f),
                                        ) {
                                            Text(
                                                text = stringResource(id = R.string.app_name),
                                                style = MaterialTheme.typography.bodyLarge,
                                            )
                                            Spacer(Modifier.height(2.dp))
                                            Text(
                                                (
                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                                        context.packageManager.getPackageInfo(
                                                            context.packageName,
                                                            PackageManager.PackageInfoFlags.of(0),
                                                        )
                                                    } else {
                                                        context.packageManager.getPackageInfo(context.packageName, 0)
                                                    }
                                                ).versionName,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.outline,
                                            )
                                        }
                                    }
                                    Spacer(Modifier.height(16.dp))
                                    Row(
                                        modifier =
                                            Modifier
                                                .fillMaxWidth()
                                                .horizontalScroll(rememberScrollState()),
                                    ) {
                                        Spacer(Modifier.width(16.dp))
                                        OutlinedButton(onClick = {
                                        }) {
                                            Icon(ImageVector.vectorResource(R.drawable.ic_telegram_app), null)
                                            Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                                            Text(stringResource(R.string.group_chat_coming_soon))
                                        }
                                        Spacer(Modifier.width(16.dp))
                                        OutlinedButton(onClick = {
                                        }) {
                                            Icon(Icons.Rounded.Apps, null)
                                            Spacer(Modifier.width(ButtonDefaults.IconSpacing))
                                            Text(stringResource(R.string.more_apps_coming_soon))
                                        }
                                        Spacer(Modifier.width(16.dp))
                                    }
                                    Spacer(Modifier.height(16.dp))
                                }
                            }
                            item {
                                OutlinedCard(
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                ) {
                                    Column(
                                        modifier =
                                            Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                    ) {
                                        Spacer(Modifier.width(4.dp))
                                        Icon(Icons.Rounded.Person, null, Modifier.size(64.dp))
                                        Spacer(Modifier.width(12.dp))
                                        Text(
                                            text = stringResource(R.string.younes_bouhouche),
                                            color = MaterialTheme.colorScheme.onSurface,
                                            style = MaterialTheme.typography.bodyLarge,
                                        )
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            stringResource(R.string.developer_description),
                                            textAlign = TextAlign.Center,
                                            color = MaterialTheme.colorScheme.outline,
                                            style = MaterialTheme.typography.bodyMedium,
                                        )
                                        Spacer(Modifier.height(16.dp))
                                        Row(
                                            Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.Center,
                                        ) {
                                            OutlinedIconButton(onClick = {}) {
                                                Icon(Icons.Default.Link, null)
                                            }
                                            Spacer(Modifier.width(8.dp))
                                            OutlinedIconButton(onClick = {
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
                                            }) {
                                                Icon(Icons.Default.Email, null)
                                            }
                                            Spacer(Modifier.width(8.dp))
                                            OutlinedIconButton(onClick = {
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
                                            Spacer(Modifier.width(8.dp))
                                            OutlinedIconButton(onClick = {
                                                with(
                                                    Intent(
                                                        Intent.ACTION_VIEW,
                                                        Uri.parse("fb://facewebmodal/f?href=https://www.facebook.com/younesbouh_05"),
                                                    ),
                                                ) {
                                                    if (this.resolveActivity(context.packageManager) != null) {
                                                        context.startActivity(this)
                                                    } else {
                                                        context.startActivity(
                                                            Intent(
                                                                Intent.ACTION_VIEW,
                                                                Uri.parse("https://facebook.com/younesbouh_05"),
                                                            ),
                                                        )
                                                    }
                                                }
                                            }) {
                                                Icon(
                                                    ImageVector.vectorResource(id = R.drawable.ic_facebook),
                                                    null,
                                                )
                                            }
                                            Spacer(Modifier.width(8.dp))
                                            OutlinedIconButton(onClick = {
                                                context.startActivity(
                                                    Intent(
                                                        Intent.ACTION_VIEW,
                                                        Uri.parse("https://www.instagram.com/younesbouh_05"),
                                                    ),
                                                )
                                            }) {
                                                Icon(
                                                    ImageVector.vectorResource(id = R.drawable.ic_instagram),
                                                    null,
                                                )
                                            }
                                            Spacer(Modifier.width(8.dp))
                                            OutlinedIconButton(onClick = {
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
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
