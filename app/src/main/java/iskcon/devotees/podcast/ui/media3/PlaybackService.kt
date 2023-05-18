package iskcon.devotees.podcast.ui.media3

import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Intent
import androidx.media3.common.AudioAttributes
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import iskcon.devotees.podcast.ui.model.MediaContent

class PlaybackService : MediaSessionService(), MediaSession.Callback {
    private var mediaSession: MediaSession? = null
    private var exoPlayer: ExoPlayer? = null

    override fun onCreate() {
        super.onCreate()
        initializePlayer()
    }

    private fun initializePlayer() {
        MediaContent.initialize(assets)
        exoPlayer =
            ExoPlayer.Builder(this)
                .setAudioAttributes(AudioAttributes.DEFAULT, true).build().also {
                    it.addMediaItemList(MediaContent.mediaContentList)
                }
        val sessionActivityPendingIntent =
            TaskStackBuilder.create(this).run {
                addNextIntent(Intent(this@PlaybackService, Media3Activity::class.java))
                getPendingIntent(
                    0,
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
            }
        exoPlayer?.let {
            //to add media using controller ----
            //implement media session in service
            //add callback for media session
            // add media using controller method
            // override onAddMediaItems or onSetMediaItems methods in service
            // mediaSession = MediaSession.Builder(this, it).setCallback(this).build()
            mediaSession =
                MediaSession.Builder(this, it).setSessionActivity(sessionActivityPendingIntent)
                    .build()
        }
    }

    override fun onDestroy() {
        releasePlayer()
        super.onDestroy()
    }

    private fun releasePlayer() {
        exoPlayer?.release()
        exoPlayer = null
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? =
        mediaSession
//
//    override fun onAddMediaItems(
//        mediaSession: MediaSession,
//        controller: MediaSession.ControllerInfo,
//        mediaItems: MutableList<MediaItem>
//    ): ListenableFuture<MutableList<MediaItem>> {
//        val updatedMediaItems =
//            mediaItems.map { it.buildUpon().setUri(it.mediaId).build() }.toMutableList()
//        return Futures.immediateFuture(updatedMediaItems)
//    }
}