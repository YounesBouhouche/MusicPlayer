package younesbouhouche.musicplayer.features.main.data.networking

import younesbouhouche.musicplayer.BuildConfig

fun constructUrl(url: String) =
    when {
        url.contains(BuildConfig.BASE_URL) -> url
        url.startsWith("/") -> "${BuildConfig.BASE_URL}${url.drop(1)}"
        else -> "${BuildConfig.BASE_URL}$url"
    }
