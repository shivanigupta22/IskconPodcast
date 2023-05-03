package iskcon.devotees.podcast.ui.media3

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.MediaItem
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
        mediaPlayer = ExoPlayer.Builder(this).build().also { exoPlayer ->
            viewBinding.videoView.player = exoPlayer
            val mediaItem = MediaItem.fromUri(getString(R.string.media_url_mp3))
            exoPlayer.setMediaItem(mediaItem)
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
//            playbackPosition = exoPlayer.currentPosition
//            currentItem = exoPlayer.currentMediaItemIndex
//            playWhenReady = exoPlayer.playWhenReady
            exoPlayer.release()
        }
        mediaPlayer = null
    }
}