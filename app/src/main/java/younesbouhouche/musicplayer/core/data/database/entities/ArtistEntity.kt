package younesbouhouche.musicplayer.core.data.database.entities

import android.net.Uri
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity
data class ArtistEntity(
    @PrimaryKey(autoGenerate = false)
    val name: String,
    val picture: String? = null,
    val coverUri: Uri? = null
)