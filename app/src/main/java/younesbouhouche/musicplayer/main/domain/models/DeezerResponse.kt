package younesbouhouche.musicplayer.main.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Data(
    @SerialName("id")
    val id: Int,
    @SerialName("link")
    val link: String,
    @SerialName("name")
    val name: String,
    @SerialName("nb_album")
    val nbAlbum: Int,
    @SerialName("nb_fan")
    val nbFan: Int,
    @SerialName("picture")
    val picture: String,
    @SerialName("picture_big")
    val pictureBig: String,
    @SerialName("picture_medium")
    val pictureMedium: String,
    @SerialName("picture_small")
    val pictureSmall: String,
    @SerialName("picture_xl")
    val pictureXl: String,
    @SerialName("radio")
    val radio: Boolean,
    @SerialName("tracklist")
    val tracklist: String,
    @SerialName("type")
    val type: String
)

@Serializable
data class DeezerResponse(
    @SerialName("data")
    val data: List<Data>,
    @SerialName("total")
    val total: Int,
    @SerialName("next")
    val next: String = "",
)
