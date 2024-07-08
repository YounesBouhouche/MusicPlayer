package younesbouhouche.musicplayer.main.presentation.util

fun String.toMs(): Long =
    if (matches(Regex("\\d{2}:\\d{2}:\\d{2}([.:])\\d{2}"))) {
        (
            (
                (substring(0, 2).toLongOrNull() ?: 0) * 3600 +
                    (substring(3, 5).toLongOrNull() ?: 0) * 60 +
                    (substring(6, 8).toLongOrNull() ?: 0)
            ) * 1000 +
                (substring(9, 11).toLongOrNull() ?: 0)
        )
    } else if (matches(Regex("\\d{2}:\\d{2}([.:])\\d{2}"))) {
        (
            (
                (substring(0, 2).toLongOrNull() ?: 0) * 60 +
                    (substring(3, 5).toLongOrNull() ?: 0)
            ) * 1000 +
                (substring(6, 8).toLongOrNull() ?: 0)
        )
    } else {
        0
    }
