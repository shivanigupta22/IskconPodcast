package iskcon.devotees.podcast.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import iskcon.devotees.podcast.R
import iskcon.devotees.podcast.ui.media3.Media3Activity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun launchMedia3(view: View) {
        startActivity(
            Intent(this, Media3Activity::class.java)
        )
    }
}