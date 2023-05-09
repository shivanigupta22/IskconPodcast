package iskcon.devotees.podcast.ui.media3

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

import androidx.media3.common.MimeTypes
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import iskcon.devotees.podcast.R
import iskcon.devotees.podcast.databinding.ActivityMedia3Binding

class Media3Activity : AppCompatActivity() {
    private val viewBinding by lazy {
        ActivityMedia3Binding.inflate(layoutInflater)
    }
    private var mediaPlayer: ExoPlayer? = null
    private var playWhenReady = true
    private var currentItem = 0
    private var playbackPosition = 0L

    private val playbackStateListener = object : Player.Listener {
        override fun onPlaybackStateChanged(playbackState: Int) {
            val stateString: String = when (playbackState) {
                ExoPlayer.STATE_IDLE -> "ExoPlayer.STATE_IDLE      -"
                ExoPlayer.STATE_BUFFERING -> "ExoPlayer.STATE_BUFFERING -"
                ExoPlayer.STATE_READY -> "ExoPlayer.STATE_READY     -"
                ExoPlayer.STATE_ENDED -> "ExoPlayer.STATE_ENDED     -"
                else -> "UNKNOWN_STATE             -"
            }
            Log.d("Playback state", "changed state to $stateString")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
    }

    override fun onStart() {
        super.onStart()
        initializePlayer()
    }

    override fun onResume() {
        super.onResume()
        hideSystemUI()
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    private fun initializePlayer() {
        mediaPlayer = ExoPlayer.Builder(this)
            .build().also { exoplayer ->
                viewBinding.videoView.player = exoplayer
                exoplayer.addMediaItemList(
                    arrayListOf(
                        createMediaItem(
                            getString(R.string.media_url_mp3),
                            MimeTypes.AUDIO_MP4
                        ), createMediaItem(getString(R.string.media_url_mp3_2))
                    )
                )
                exoplayer.playWhenReady = playWhenReady
                exoplayer.seekTo(currentItem, playbackPosition)
                exoplayer.addListener(playbackStateListener)
                exoplayer.prepare()
            }
    }

    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, viewBinding.videoView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun releasePlayer() {
        mediaPlayer?.let { exoPlayer ->
            playbackPosition = exoPlayer.currentPosition
            currentItem = exoPlayer.currentMediaItemIndex
            playWhenReady = exoPlayer.playWhenReady
            exoPlayer.release()
            exoPlayer.removeListener(playbackStateListener)
        }
        mediaPlayer = null
    }
}