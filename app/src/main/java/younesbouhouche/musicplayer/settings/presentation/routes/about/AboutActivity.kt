package younesbouhouche.musicplayer.settings.presentation.routes.about

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
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
import androidx.core.net.toUri
import androidx.core.view.WindowCompat
import org.koin.android.ext.android.get
import org.koin.compose.KoinContext
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.core.presentation.util.ExpressiveButton
import younesbouhouche.musicplayer.main.presentation.util.composables.SetSystemBarColors
import younesbouhouche.musicplayer.main.presentation.util.getAppVersion
import younesbouhouche.musicplayer.settings.presentation.AppIcon
import younesbouhouche.musicplayer.settings.presentation.components.SettingsItem
import younesbouhouche.musicplayer.settings.presentation.components.SettingsList
import younesbouhouche.musicplayer.settings.presentation.components.listItemShape
import younesbouhouche.musicplayer.settings.presentation.util.Category
import younesbouhouche.musicplayer.settings.presentation.util.SettingData
import younesbouhouche.musicplayer.ui.theme.AppTheme

class AboutActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val listState = rememberLazyListState()
            val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
            val context = LocalContext.current
            SetSystemBarColors(dataStore = get())
            val categories = listOf(
                Category(
                    R.string.developer,
                    listOf(
                        SettingData(
                            R.string.younes_bouhouche,
                            R.string.younes_bouhouche,
                            Icons.Default.Person,
                        ) {
                            // No action needed
                        },
                        SettingData(
                            R.string.email,
                            R.string.developer_email,
                            Icons.Default.Mail,
                        ) {
                            with(
                                Intent(Intent.ACTION_SENDTO).apply {
                                    data = "mailto:".toUri()
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
                    ),
                ),
                Category(
                    R.string.social_media,
                    listOf(
                        SettingData(
                            R.string.telegram,
                            R.string.telegram_channel,
                            ImageVector.vectorResource(id = R.drawable.ic_telegram_app),
                        ) {
                            it.startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    "https://t.me/younesbouhouche".toUri(),
                                ),
                            )
                        },
                        SettingData(
                            R.string.twitter,
                            R.string.twitter_account,
                            ImageVector.vectorResource(id = R.drawable.ic_twitter),
                        ) {
                            with(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    "twitter://user?screen_name=younesbouh_05".toUri(),
                                ),
                            ) {
                                if (this.resolveActivity(context.packageManager) != null) {
                                    it.startActivity(this)
                                } else {
                                    it.startActivity(
                                        Intent(
                                            Intent.ACTION_VIEW,
                                            "https://twitter.com/younesbouh_05".toUri(),
                                        ),
                                    )
                                }
                            }
                        },
                    ),
                ),
            )
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
                        verticalArrangement = Arrangement.spacedBy(
                            16.dp,
                            Alignment.CenterVertically
                        ),
                    ) {
                        item {
                            AppIcon()
                        }
                        item {
                            Text(
                                stringResource(R.string.app_name),
                                Modifier.fillMaxWidth(),
                                style = MaterialTheme.typography.headlineLarge,
                                textAlign = TextAlign.Center,
                            )
                        }
                        item {
                            ExpressiveButton(
                                "v${getAppVersion()}",
                                ButtonDefaults.MediumContainerHeight,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                    contentColor = MaterialTheme.colorScheme.tertiary
                                )
                            ) {

                            }
                        }
                        items(categories) {
                            SettingsList(it.name) {
                                it.items.forEachIndexed { index, setting ->
                                    SettingsItem(
                                        setting,
                                        shape = listItemShape(index , it.items.size),
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