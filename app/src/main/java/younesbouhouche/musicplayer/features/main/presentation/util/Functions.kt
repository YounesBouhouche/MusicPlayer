package younesbouhouche.musicplayer.features.main.presentation.util

import android.app.Activity
import android.app.LocaleManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.LocaleList
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.LocaleManagerCompat
import androidx.core.os.LocaleListCompat
import younesbouhouche.musicplayer.core.domain.models.MusicCard
import younesbouhouche.musicplayer.features.settings.models.Language
import java.time.ZonedDateTime

fun (Pair<String, String>).containEachOther() = first.contains(second, true) or second.contains(first, true)

fun String.removeLeadingTime(): String =
    when {
        matches(Regex("^(\\[(\\d{2}:\\d{2}:\\d{2}([.:])\\d{2})])\\s(\\w|\\s)*")) and (length >= 12) -> removeRange(0..11)
        matches(Regex("^(\\[(\\d{2}:\\d{2}([.:])\\d{2})])\\s(\\w|\\s)*")) and (length >= 10) -> removeRange(0..9)
        else -> this
    }.trimStart()

fun MusicCard.search(query: String) =
    (title to query).containEachOther() or
        (path to query).containEachOther() or
        (album to query).containEachOther() or
        (artist to query).containEachOther()

fun getCurrentTime(): Long = ZonedDateTime.now().toInstant().toEpochMilli()

fun Context.getAppVersion(): String =
    (
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getPackageInfo(
                packageName,
                PackageManager.PackageInfoFlags.of(0),
            )
        } else {
            packageManager.getPackageInfo(packageName, 0)
        }
    ).versionName ?: "Unknown"


fun Activity.setLanguage(language: Language) {
    runOnUiThread {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getSystemService(LocaleManager::class.java)
                .applicationLocales =
                LocaleList.forLanguageTags(
                    if (language == Language.SYSTEM) {
                        LocaleManagerCompat.getSystemLocales(this)[0]!!.language
                    } else {
                        language.tag
                    },
                )
        } else {
            AppCompatDelegate.setApplicationLocales(
                LocaleListCompat.forLanguageTags(language.tag)
            )
        }
    }
}