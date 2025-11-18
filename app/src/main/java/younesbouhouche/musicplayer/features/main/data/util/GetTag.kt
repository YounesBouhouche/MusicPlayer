package younesbouhouche.musicplayer.features.main.data.util

import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import java.io.File

fun File.getTag(fieldKey: FieldKey): String = try {
    AudioFileIO.read(this).tag.getFirst(fieldKey)
} catch (e: Exception) {
    e.printStackTrace()
    ""
}