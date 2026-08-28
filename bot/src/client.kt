// IMPORT
import dev.kord.core.Kord

// MAIN
suspend fun client() {
    val config = loadConfig()
    val kord = Kord(config.token)
}
