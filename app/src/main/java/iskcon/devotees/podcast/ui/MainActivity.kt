package iskcon.devotees.podcast.ui

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.media3.session.MediaBrowser
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import iskcon.devotees.podcast.ui.media3.Media3Activity
import iskcon.devotees.podcast.ui.media3.PlaybackService
import iskcon.devotees.podcast.ui.model.MediaContent
import iskcon.devotees.podcast.ui.theme.AppTheme
import iskcon.devotees.podcast.ui.utils.listSubTitleWithTextStyle
import iskcon.devotees.podcast.ui.utils.listTitleWithTextStyle

class MainActivity : ComponentActivity() {
    private lateinit var browserFuture: ListenableFuture<MediaBrowser>
    private val browser: MediaBrowser?
        get() =
            if (browserFuture.isDone) browserFuture.get() else null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MediaContent.initialize(assets)
        setContent {
            ScreenView()
        }
    }

    private fun initializeMediaBrowser() {
        browserFuture = MediaBrowser.Builder(
            this,
            SessionToken(this, ComponentName(this, PlaybackService::class.java))
        ).buildAsync()
//        browserFuture.addListener({
//            this.browser?.setMediaItems(MediaContent.mediaContentList)
//            Log.e(
//                "media item",
//                "${this.browser?.mediaItemCount} --- ${MediaContent.mediaContentList.size}"
//            )
//        }, ContextCompat.getMainExecutor(this))

    }

    private fun releaseBrowser() {
        MediaBrowser.releaseFuture(browserFuture)
    }

    override fun onStart() {
        super.onStart()
        initializeMediaBrowser()
    }

    override fun onStop() {
        super.onStop()
        releaseBrowser()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ScreenView() {
        AppTheme {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(text = "Srila Prabhupada Bhajan Collections", fontSize = 18.sp)
                        },
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            titleContentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    )
                }
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.background,
                    modifier = Modifier.padding(
                        horizontal = 16.dp,
                        vertical = it.calculateTopPadding()
                    )
                ) {
                    DrawList()
                }
            }

        }
    }

    @Composable
    fun DrawList() {
        LazyColumn(modifier = Modifier.padding(vertical = 4.dp)) {
            /*  TODO fetch items from browser*/
            Log.e("media item drawlist", MediaContent.mediaContentList.size.toString())
            items(items = MediaContent.mediaContentList) { item ->
                ListItem(item)
            }
        }
    }

    @Composable
    private fun ListItem(item: MediaItem) {
        val actContext = LocalContext.current
        val isFav = remember {
            mutableStateOf(false)
        }
        Card(
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Row(modifier = Modifier
                .clickable {
                    val browser = browser ?: return@clickable
                    browser.run {
                        setMediaItem(item)
                        prepare()
                        play()
                    }
                    actContext.startActivity(
                        Intent(actContext, Media3Activity::class.java)
                    )
                }
                .padding(12.dp)) {
                Column(
                    modifier = Modifier.weight(1f, fill = true)
                ) {
                    listTitleWithTextStyle(
                        item.mediaMetadata.title.toString(),
                    )
                    listSubTitleWithTextStyle(text = item.mediaMetadata.albumTitle.toString())
                }
                IconToggleButton(
                    checked = isFav.value,
                    onCheckedChange = {
                        isFav.value = !isFav.value
                    },
                    colors = IconButtonDefaults.iconToggleButtonColors(
                        contentColor = MaterialTheme.colorScheme.surface,
                        checkedContentColor = MaterialTheme.colorScheme.tertiary,
                    )
                ) {
                    Icon(Icons.Filled.Favorite, contentDescription = "save as favourite")

                }
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun DrawListPreview() {
        ScreenView()
    }

}