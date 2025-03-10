package younesbouhouche.musicplayer.settings.presentation

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import org.koin.android.ext.android.get
import org.koin.compose.KoinContext
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.core.presentation.util.composables.SetSystemBarColors
import younesbouhouche.musicplayer.core.presentation.util.getAppVersion
import younesbouhouche.musicplayer.ui.theme.AppTheme


class AboutActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            KoinContext {
                val listState = rememberLazyListState()
                val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
                val context = LocalContext.current
                SetSystemBarColors(dataStore = get())
                AppTheme {
                    Scaffold(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .nestedScroll(scrollBehavior.nestedScrollConnection),
                        topBar = {
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
                        },
                    ) { paddingValues ->
                        LazyColumn(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp),
                            state = listState,
                            contentPadding = paddingValues,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
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
                                Button(onClick = {}) {
                                    Text("v${getAppVersion()}")
                                }
                            }
                            item {
                                AboutCard(
                                    stringResource(R.string.developer),
                                    stringResource(R.string.younes_bouhouche),
                                    Icons.Default.Person,
                                ) {
                                }
                            }
                            item {
                                AboutCard(
                                    stringResource(R.string.project),
                                    stringResource(R.string.source_code),
                                    Icons.Default.Code,
                                ) {
                                }
                            }
                            item {
                                AboutCard(
                                    stringResource(R.string.send_feedback),
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
                                                "\nApp Version:${getAppVersion()}," +
                                                    "\nAPI Level:${Build.VERSION.SDK_INT}",
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
                                    stringResource(R.string.social_media),
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
                            item {
                                AboutCard(
                                    stringResource(R.string.translation),
                                    stringResource(R.string.contribute_in_app_translation),
                                    Icons.Default.Translate,
                                ) {
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
