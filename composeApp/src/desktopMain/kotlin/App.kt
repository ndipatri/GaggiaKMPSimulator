import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import gaggiakmpsimulator.composeapp.generated.resources.Res
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.ui.tooling.preview.Preview
import java.io.File
import java.net.JarURLConnection
import java.net.URL

@OptIn(ExperimentalResourceApi::class)
@Composable
@Preview
fun App() {
    MaterialTheme {
        val simulator = remember { GaggiaSimulator() }

        val localVideoURL = remember {
            URL(Res.getUri("files/robo_gaggia_small.mp4")).let {
                (it.openConnection() as JarURLConnection).let { connection ->
                    File(connection.jarFileURL.toURI()).absolutePath
                }
            }
        }

        var pauseVideoAtSeconds by remember { mutableStateOf(0) }

        val telemetryState = simulator.currentTelemetryStateFlow.collectAsState()

        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceAround) {
                    Column {
                        var option = telemetryState.value.state.videoStartSeconds?.let {
                            "start-time=${it}"
                        }
                        if (pauseVideoAtSeconds > 0) {
                            option = "pause-time=${pauseVideoAtSeconds}"
                        }

                        // will only change player if values are changed...
                        VideoPlayerImpl(
                            url = localVideoURL,
                            option = option,
                            modifier = Modifier.fillMaxWidth().height(400.dp)
                        )

                        // when we have new state,
                        // then we stop the video after a duration
                        LaunchedEffect(telemetryState.value.state) {
                            // need to reset this.
                            pauseVideoAtSeconds = 0

                            telemetryState.value.state.videoEndSeconds?.let { endSeconds ->
                                telemetryState.value.state.videoStartSeconds?.let { startSeconds ->
                                    println("*** NJD: Sleeping for ($endSeconds - $startSeconds) seconds")
                                    delay((endSeconds - startSeconds) * 1000L)
                                    println("*** NJD: awake @ $endSeconds")
                                    pauseVideoAtSeconds = endSeconds
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}