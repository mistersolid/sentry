// IMPORT
import dev.kord.core.Kord
import kotlinx.serialization.json.Json
import java.io.File

// MAIN
suspend fun client() {
    val config = loadConfig()
    val kord = Kord(config.token)
}
