import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun App() {
    MaterialTheme {
        var showContent by remember { mutableStateOf(false) }
        val greeting = remember { Greeting().greet() }
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = {

                runRequest()
            }) {
                Text("Click me!")
            }
            AnimatedVisibility(showContent) {
                Column(
                    Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(painterResource("compose-multiplatform.xml"), null)
                    Text("Compose: $greeting")
                }
            }
        }
    }
}


private fun runRequest() {

    val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            }
            )
        }
    }
    val handler = CoroutineExceptionHandler(handler = { _, exception ->
        Logger.e("MyTag") { "CoroutineExceptionHandler got $exception" }
    })

    CoroutineScope(Dispatchers.IO).launch(handler) {
        Logger.d("MyTag") { "runRequest" }
        try {
            val response: HttpResponse = client.request(
                "https://api.themoviedb.org/3/movie/now_playing"
            ) {
                header(
                    "Authorization",
                    "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiIzNjg4MDU3NjMyMjViMWYxZTU1MWZiNmRiM2VmMzBlYiIsInN1YiI6IjY1YTUxMWQ3MDU4MjI0MDEzMmZkZTc4ZiIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.SluhdSzeZvLdmLxNFtcZn_h23cvJ8lfYKXhfWZlxs2w"
                )
            }
            val apiResponse = response.body<ApiResponse>()
            Logger.d("MyTag") { "response = $apiResponse" }
        } catch (e: Exception) {
            Logger.e("MyTag") { "Exception = $e" }
        }
    }
}

@Serializable
data class ApiResponse(
    val dates: Dates?,
    val page: Int?,
    val results: List<Movie>?,
    val total_pages: Int?,
    val total_results: Int?
)

@Serializable
data class Dates(
    val maximum: String?,
    val minimum: String?
)

@Serializable
data class Movie(
    val adult: Boolean?,
    val backdrop_path: String?,
    val genre_ids: List<Int>?,
    val id: Int?,
    val original_language: String?,
    val original_title: String?,
    val overview: String?,
    val popularity: Double?,
    val poster_path: String?,
    val release_date: String?,
    val title: String?,
    val video: Boolean?,
    val vote_average: Double?,
    val vote_count: Int?
)