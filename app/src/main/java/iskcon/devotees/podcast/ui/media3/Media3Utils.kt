package iskcon.devotees.podcast.ui.media3

import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

fun ExoPlayer.addMediaItemList(mediaList: MutableList<MediaItem>) {
    if (mediaList.size == 1) this.addMediaItem(mediaList[0])
    else this.addMediaItems(mediaList)
}