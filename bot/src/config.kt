// IMPORT
import dev.kord.common.entity.Snowflake
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

// CLASS
/**
 * Model of config.json. Does not contain the bot token.
 */
@Serializable
data class FileConfig(
    val guildID: Snowflake,
    val ownerID: Snowflake,
    val prefix: String = "!",
)

/**
 * Fully resolved configuration for the bot including the bot token.
 */
class Config(
    val token: String,
    file: FileConfig,
) {
    val guildID = file.guildID
    val ownerID = file.ownerID
    val prefix = file.prefix

    /**
     * Returns a string representation of the object. Token deliberately omitted from logs; do not include it.
     */
    override fun toString() = "Config(guildID=$guildID, ownerID=$ownerID, prefix=$prefix)"
}

// FUNCTION
/**
 * Reads [path] and combines it with the `TOKEN` environment variable.
 *
 * @throws IllegalArgumentException - if the file is missing.
 * @throws IllegalStateException - if `TOKEN` is unset.
 * @throws kotlinx.serialization.SerializationException - if the file is malformed.
 * @throws IllegalArgumentException - if the decoded input is not a valid instance.
 */
fun loadConfig(path: String = "config.json"): Config {
    val f = File(path)
    require(f.exists()) { "Missing $path" }
    return Config(
        token = System.getenv("TOKEN") ?: error("TOKEN is not set"),
        file = Json.decodeFromString(f.readText()),
    )
}