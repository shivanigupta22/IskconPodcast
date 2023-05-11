package iskcon.devotees.podcast.ui.media3

import androidx.media3.common.AudioAttributes
import androidx.media3.common.MimeTypes
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import iskcon.devotees.podcast.R

class PlaybackService : MediaSessionService() {
    private var mediaSession: MediaSession? = null
    private var exoPlayer: ExoPlayer? = null

    override fun onCreate() {
        super.onCreate()
        initializePlayer()
    }

    private fun initializePlayer() {
        exoPlayer =
            ExoPlayer.Builder(this)
                .setAudioAttributes(AudioAttributes.DEFAULT, true).build().also {
                    it.addMediaItemList(
                        arrayListOf(
                            createMediaItem(
                                getString(R.string.media_url_mp3),
                                MimeTypes.AUDIO_MP4
                            ), createMediaItem(getString(R.string.media_url_mp3_2))
                        )
                    )
                }
        exoPlayer?.let {
            mediaSession = MediaSession.Builder(this, it).build()
        }
    }

    override fun onDestroy() {
        releasePlayer()
        super.onDestroy()
    }

    private fun releasePlayer() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        exoPlayer = null
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? =
        mediaSession
}