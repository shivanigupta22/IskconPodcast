package iskcon.devotees.podcast.ui.media3

import android.content.ComponentName
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player.*
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import iskcon.devotees.podcast.databinding.ActivityMedia3Binding

class Media3Activity : AppCompatActivity() {
    private val viewBinding by lazy {
        ActivityMedia3Binding.inflate(layoutInflater)
    }

    //A Future that accepts completion listeners. Each listener has an associated executor,
    // and it is invoked using this executor once the future's computation is complete.
    // If the computation has already completed when the listener is added, the listener will execute immediately.
    private lateinit var controllerFuture: ListenableFuture<MediaController>
    private val controller: MediaController?
        get() =
            if (controllerFuture.isDone) controllerFuture.get() else null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
    }

    override fun onStart() {
        super.onStart()
        initializeController()
    }

    override fun onPause() {
        super.onPause()
        viewBinding.videoView.onPause()
    }

    override fun onResume() {
        super.onResume()
        viewBinding.videoView.onResume()
        hideSystemUI()
    }

    override fun onStop() {
        super.onStop()
        releaseController()
    }

    private fun initializeController() {
        //building controller is an async process hence returning a listenable future
        controllerFuture = MediaController.Builder(
            this,
            SessionToken(this, ComponentName(this, PlaybackService::class.java))
        ).buildAsync()
        controllerFuture.addListener(
            {
                viewBinding.videoView.player = controller
                controller?.playWhenReady = true
                controller?.addListener(object : Listener {

                    override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
                        super.onMediaMetadataChanged(mediaMetadata)
                        log(mediaMetadata.toString())
                    }

                    override fun onIsPlayingChanged(isPlaying: Boolean) {
                        super.onIsPlayingChanged(isPlaying)
                        log("onIsPlayingChanged=$isPlaying")
                    }

                    override fun onPlaybackStateChanged(playbackState: Int) {
                        super.onPlaybackStateChanged(playbackState)
                        log("onPlaybackStateChanged=${getStateName(playbackState)}")
                    }

                    override fun onPlayerError(error: PlaybackException) {
                        super.onPlayerError(error)
                        log("onPlayerError=${error.stackTraceToString()}")
                    }

                    override fun onPlayerErrorChanged(error: PlaybackException?) {
                        super.onPlayerErrorChanged(error)
                        log("onPlayerErrorChanged=${error?.stackTraceToString()}")
                    }
                })
                log("start=${getStateName(controller?.playbackState)}")
                log("COMMAND_PREPARE=${controller?.isCommandAvailable(COMMAND_PREPARE)}")
                log("COMMAND_SET_MEDIA_ITEM=${controller?.isCommandAvailable(COMMAND_SET_MEDIA_ITEM)}")
                log("COMMAND_PLAY_PAUSE=${controller?.isCommandAvailable(COMMAND_PLAY_PAUSE)}")
                play()
            }, MoreExecutors.directExecutor()
        )

    }

    private fun play() {
        controller?.prepare()
        controller?.play()
        log("after=${getStateName(controller?.playbackState)}")
    }

    private fun getStateName(i: Int?): String? {
        return when (i) {
            1 -> "STATE_IDLE"
            2 -> "STATE_BUFFERING"
            3 -> "STATE_READY"
            4 -> "STATE_ENDED"
            else -> null
        }
    }

    private fun log(mediaMetadata: String) {
        Log.e("Media 3", "onMediaMetadataChanged=$mediaMetadata")
    }

    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, viewBinding.videoView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun releaseController() {
        MediaController.releaseFuture(controllerFuture)
    }
}