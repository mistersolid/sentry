// IMPORT
import dev.kord.core.Kord

// MAIN
/**
 * Creates a new [Kord] instance using [loadConfig] to pass the bot token, guild ID, and owner ID.
 */
suspend fun client() {
    val config = loadConfig()
    val kord = Kord(config.token)

    kord.login()
}