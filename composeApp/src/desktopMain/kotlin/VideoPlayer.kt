import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay
import uk.co.caprica.vlcj.factory.discovery.NativeDiscovery
import uk.co.caprica.vlcj.player.base.MediaPlayer
import uk.co.caprica.vlcj.player.component.CallbackMediaPlayerComponent
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent
import java.awt.Component
import java.util.Locale

@Composable
fun VideoPlayerImpl(
    url: String,
    option: String? = null , // e.g. 'start-time=60'
    modifier: Modifier,
) {
    val mediaPlayerComponent = remember { initializeMediaPlayerComponent() }
    val mediaPlayer = remember { mediaPlayerComponent.mediaPlayer() }
    val factory = remember { { mediaPlayerComponent } }

    val mediaApi = remember { mediaPlayer.media() }

    LaunchedEffect(url) {
        println("*** NJD: player: playing video @ $url")
        mediaApi.play(url)
    }

    LaunchedEffect(option) {
        if (option != null) {
            if (option.indexOf("pause-time") > -1) {
                println("*** NJD: player: pausing video @ $url with option: $option")
                mediaApi.startPaused(url, option.replace("pause-time", "start-time"))
            } else {
                println("*** NJD: player: playing video @ $url with option: $option")
                mediaApi.play(url, option)
            }
        }
    }

    DisposableEffect(Unit) {
        println("*** NJD: dispose player")
        onDispose(mediaPlayer::release)
    }
    SwingPanel(
        factory = factory,
        background = Color.Transparent,
        modifier = modifier
    )
}

private fun initializeMediaPlayerComponent(): Component {
    NativeDiscovery().discover()
    return if (isMacOS()) {
        CallbackMediaPlayerComponent()
    } else {
        EmbeddedMediaPlayerComponent()
    }
}


/**
 * Returns [MediaPlayer] from player components.
 * The method names are the same, but they don't share the same parent/interface.
 * That's why we need this method.
 */
private fun Component.mediaPlayer() = when (this) {
    is CallbackMediaPlayerComponent -> mediaPlayer()
    is EmbeddedMediaPlayerComponent -> mediaPlayer()
    else -> error("mediaPlayer() can only be called on vlcj player components")
}

private fun isMacOS(): Boolean {
    val os = System
        .getProperty("os.name", "generic")
        .lowercase(Locale.ENGLISH)
    return "mac" in os || "darwin" in os
}