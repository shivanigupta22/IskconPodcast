package iskcon.devotees.podcast.ui.model

import android.content.res.AssetManager
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaItem.RequestMetadata
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.Util
import org.json.JSONObject

object MediaContent {
    val mediaContentList = mutableListOf<MediaItem>()

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    private fun loadJSONFromAsset(assets: AssetManager): String {
        val buffer = assets.open("catalog.json").use { Util.toByteArray(it) }
        return String(buffer, Charsets.UTF_8)
    }

    fun initialize(assets: AssetManager) {
        val jsonObject = JSONObject(loadJSONFromAsset(assets))
        val mediaList = jsonObject.getJSONArray("media")

        for (i in 0 until mediaList.length()) {
            addMediaToMediaContent(mediaList.getJSONObject(i))
        }
    }

    private fun addMediaToMediaContent(mediaObject: JSONObject) {
        val id = mediaObject.getString("id")
        val album = mediaObject.getString("album")
        val title = mediaObject.getString("title")
        val artist = mediaObject.getString("artist")
        val genre = mediaObject.getString("genre")
        val subtitleConfigurations: MutableList<MediaItem.SubtitleConfiguration> = mutableListOf()
        if (mediaObject.has("subtitles")) {
            val subtitlesJson = mediaObject.getJSONArray("subtitles")
            for (i in 0 until subtitlesJson.length()) {
                val subtitleObject = subtitlesJson.getJSONObject(i)
                subtitleConfigurations.add(
                    MediaItem.SubtitleConfiguration.Builder(Uri.parse(subtitleObject.getString("subtitle_uri")))
                        .setMimeType(subtitleObject.getString("subtitle_mime_type"))
                        .setLanguage(subtitleObject.getString("subtitle_lang"))
                        .build()
                )
            }
        }
        val sourceUri = Uri.parse(mediaObject.getString("source"))
        val imageUri = Uri.parse(mediaObject.getString("image"))
        mediaContentList.add(
            buildMediaItem(
                title = title,
                mediaId = id,
                isPlayable = true,
                subtitleConfigurations = subtitleConfigurations,
                album = album,
                artist = artist,
                genre = genre,
                sourceUri = sourceUri,
                imageUri = imageUri
            )
        )
    }

    private fun buildMediaItem(
        title: String,
        mediaId: String,
        isPlayable: Boolean,
        isBrowsable: Boolean = false,
        mediaType: @MediaMetadata.MediaType Int? = null,
        subtitleConfigurations: List<MediaItem.SubtitleConfiguration> = mutableListOf(),
        album: String? = null,
        artist: String? = null,
        genre: String? = null,
        sourceUri: Uri? = null,
        imageUri: Uri? = null
    ): MediaItem {
        val metadata =
            MediaMetadata.Builder()
                .setAlbumTitle(album)
                .setTitle(title)
                .setArtist(artist)
                .setGenre(genre)
                .setIsPlayable(isPlayable)
                .setArtworkUri(imageUri)
                .build()

        val requestMetadata = RequestMetadata.Builder().setMediaUri(sourceUri).build()

        return MediaItem.Builder()
            .setMediaId(mediaId)
            .setSubtitleConfigurations(subtitleConfigurations)
            .setMediaMetadata(metadata)
            .setRequestMetadata(requestMetadata)
            .setUri(sourceUri)
            .build()
    }

}