package iskcon.devotees.podcast.ui.media3

import android.content.ComponentName
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import iskcon.devotees.podcast.databinding.ActivityMedia3Binding

class Media3Activity : AppCompatActivity() {
    private val viewBinding by lazy {
        ActivityMedia3Binding.inflate(layoutInflater)
    }
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

    override fun onResume() {
        super.onResume()
        hideSystemUI()
    }

    override fun onStop() {
        super.onStop()
        releaseController()
    }

    private fun initializeController() {
        controllerFuture = MediaController.Builder(
            this,
            SessionToken(this, ComponentName(this, PlaybackService::class.java))
        ).buildAsync()
        controllerFuture.addListener(
            {
                viewBinding.videoView.player = this.controller
            }, MoreExecutors.directExecutor()
        )
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