package younesbouhouche.musicplayer.core.data.database.entities

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AlbumEntity(
    @PrimaryKey(autoGenerate = false)
    val name: String = "",
    val cover: Uri? = null
)