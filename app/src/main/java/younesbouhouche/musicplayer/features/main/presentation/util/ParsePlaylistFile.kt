package younesbouhouche.musicplayer.features.main.presentation.util

fun String.parsePlaylistFile(): Pair<String, List<String>> {
    val lines = lines().filter { it.isNotBlank() }
    val items = mutableListOf<String>()
    var name = "Imported Playlist"
    if (lines.firstOrNull()?.startsWith("#EXTM3U") == true) {
        // M3U format
        for (line in lines) {
            if (line.startsWith("#EXTINF:")) {
                val info = line.substringAfter(":").substringAfter(",")
                if (info.isNotBlank()) name = info
            } else if (!line.startsWith("#")) {
                items.add(line)
            }
        }
    } else if (lines.any { it.startsWith("[playlist]") }) {
        // PLS format
        for (line in lines) {
            if (line.startsWith("File")) {
                items.add(line.substringAfter("=").trim())
            } else if (line.startsWith("Title")) {
                name = line.substringAfter("=").trim()
            }
        }
    } else {
        // Fallback: treat each line as a path
        items.addAll(lines)
    }
    return name to items
}