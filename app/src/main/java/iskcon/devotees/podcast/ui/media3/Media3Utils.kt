package iskcon.devotees.podcast.ui.media3

import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

fun createMediaItem(url: String, mimeType: String? = null): MediaItem {
    return MediaItem.Builder().setUri(url).also { builder ->
        mimeType?.let {
            builder.setMimeType(it)
        }
    }.build()
}

fun ExoPlayer.addMediaItemList(mediaList: ArrayList<MediaItem>) {
    if (mediaList.size == 1) this.addMediaItem(mediaList[0])
    else this.addMediaItems(mediaList)
}