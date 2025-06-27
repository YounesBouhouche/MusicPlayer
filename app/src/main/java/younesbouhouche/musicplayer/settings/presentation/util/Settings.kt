package younesbouhouche.musicplayer.settings.presentation.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import com.materialkolor.ktx.harmonize
import org.koin.compose.koinInject
import younesbouhouche.musicplayer.R
import younesbouhouche.musicplayer.settings.data.SettingsDataStore
import younesbouhouche.musicplayer.settings.presentation.routes.about.AboutActivity
import younesbouhouche.musicplayer.settings.presentation.routes.language.LanguageActivity
import younesbouhouche.musicplayer.settings.presentation.routes.playback.PlaybackActivity
import younesbouhouche.musicplayer.settings.presentation.routes.player.PlayerActivity
import younesbouhouche.musicplayer.settings.presentation.routes.theme.ThemeActivity
import younesbouhouche.musicplayer.ui.theme.BlueColors
import younesbouhouche.musicplayer.ui.theme.GreenColors
import younesbouhouche.musicplayer.ui.theme.PurpleColors
import younesbouhouche.musicplayer.ui.theme.RedColors

data class Category(
    val name: Int? = null,
    val items: List<SettingData>,
    val iconTint: Color = Color.Unspecified,
    val iconBackground: Color = Color.Unspecified,
)

object Settings {
    val general = listOf(
        SettingData(
            headline = R.string.language,
            supporting = R.string.language_desc,
            icon = Icons.Default.Language,
            large = true
        ) {
            it.startActivity<LanguageActivity>()
        }
    )
    val appearance = listOf(
        SettingData(
            headline = R.string.theme,
            supporting = R.string.theme_desc,
            icon = Icons.Default.Palette,
            large = true
        ) {
            it.startActivity<ThemeActivity>()
        },
        SettingData(
            headline = R.string.player,
            supporting = R.string.customize_player_desc,
            icon = Icons.Default.PlayCircleFilled,
            large = true
        ) {
            it.startActivity<PlayerActivity>()
        }
    )
    val playback = listOf(
        SettingData(
            headline = R.string.playback,
            supporting = R.string.playback_desc,
            icon = Icons.Default.MusicNote,
            large = true
        ) {
            it.startActivity<PlaybackActivity>()
        }
    )
    val library = listOf(
        SettingData(
            headline = R.string.folders,
            supporting = R.string.folders_desc,
            icon = Icons.Default.FolderOpen,
            large = true
        ) {
            // it.startActivity<LibraryActivity>()
        },
        SettingData(
            headline = R.string.storage,
            supporting = R.string.storage_desc,
            icon = Icons.Default.Storage,
            large = true
        ) {
            // it.startActivity<StorageActivity>()
        }
    )
    val about = listOf(
        SettingData(
            headline = R.string.about,
            supporting = R.string.about_desc,
            icon = Icons.Default.Info,
            large = true
        ) {
            it.startActivity<AboutActivity>()
        }
    )
    private val blueColors: ColorScheme
        @Composable get() {
            val dataStore = koinInject<SettingsDataStore>()
            val isDark by dataStore.isDark().collectAsState(initial = false)
            return if (isDark) BlueColors.darkScheme else BlueColors.lightScheme
        }
    private val greenColors: ColorScheme
        @Composable get() {
            val dataStore = koinInject<SettingsDataStore>()
            val isDark by dataStore.isDark().collectAsState(initial = false)
            return if (isDark) GreenColors.darkScheme else GreenColors.lightScheme
        }
    private val redColors: ColorScheme
        @Composable get() {
            val dataStore = koinInject<SettingsDataStore>()
            val isDark by dataStore.isDark().collectAsState(initial = false)
            return if (isDark) RedColors.darkScheme else RedColors.lightScheme
        }
    private val purpleColors: ColorScheme
        @Composable get() {
            val dataStore = koinInject<SettingsDataStore>()
            val isDark by dataStore.isDark().collectAsState(initial = false)
            return if (isDark) PurpleColors.darkScheme else PurpleColors.lightScheme
        }
    val categories
    @Composable get() = listOf(
        Category(
            items = appearance,
            iconTint = greenColors.onPrimary.harmonizeWithPrimary(),
            iconBackground = greenColors.primary.harmonizeWithPrimary()
            ),
        Category(
            items = general,
            iconTint = blueColors.onPrimary.harmonizeWithPrimary(),
            iconBackground = blueColors.primary.harmonizeWithPrimary()
        ),
        Category(
            items = playback,
            iconTint = redColors.onPrimary.harmonizeWithPrimary(),
            iconBackground = redColors.primary.harmonizeWithPrimary()
            ),
        Category(
            items = library,
            iconTint = purpleColors.onPrimary.harmonizeWithPrimary(),
            iconBackground = purpleColors.primary.harmonizeWithPrimary()
            ),
        Category(
            items = about,
            iconTint = MaterialTheme.colorScheme.surfaceVariant,
            iconBackground = MaterialTheme.colorScheme.onSurfaceVariant
            )
    )
}

@Composable
fun Color.harmonizeWithPrimary() = harmonize(MaterialTheme.colorScheme.primary)

inline fun <reified T : Activity> Context.startActivity() =
    startActivity(Intent(this, T::class.java))
