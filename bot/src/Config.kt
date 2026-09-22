// IMPORT
import dev.kord.common.entity.Snowflake
import dev.kord.gateway.Intent
import dev.kord.gateway.Intents
import dev.kord.gateway.NON_PRIVILEGED
import dev.kord.gateway.PrivilegedIntent
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
 * Defines the intents used by the bot to specify events that it will listen for.
 *
 * The intents include both non-privileged and privileged intents:
 * - `Intents.NON_PRIVILEGED`: Standard intents accessible without special permissions.
 * - `Intent.MessageContent`: Grants access to the content of messages in guilds.
 * - `Intent.GuildMembers`: Grants access to information about guild members.
 *
 * Privileged intents require explicit enabling in the Discord Developer Portal.
 */
@OptIn(PrivilegedIntent::class)
val intentConfig = Intents {
    // Non-privileged (Bulk)
    +Intents.NON_PRIVILEGED

    // Privileged (Granular)
    +Intent.MessageContent
    +Intent.GuildMembers
}

/**
 * Represents the configuration for the bot.
 *
 * This class encapsulates the bot's runtime configuration, including:
 * - The bot token, retrieved from the environment.
 * - Guild-specific identifiers such as `guildID` and `ownerID`, derived from a configuration file.
 * - A command prefix used to identify bot commands in messages.
 * - Intents that define the Discord events the bot listens to, leveraging both non-privileged and privileged intents.
 *
 * @property token The bot token used for authentication with the Discord API.
 * @property guildID The Snowflake ID of the guild associated with the bot, extracted from the configuration file.
 * @property ownerID The Snowflake ID of the bot owner, extracted from the configuration file.
 * @property prefix The command prefix for the bot, extracted from the configuration file.
 * @property intents The set of intents specifying the Discord events the bot will monitor.
 */
class Config(
    val token: String,
    file: FileConfig,
    val intents: Intents
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
    require(f.exists()) { "Missing ${f.absolutePath}" }

    return Config(
        token = System.getenv("TOKEN") ?: error("TOKEN is not set"),
        file = Json.decodeFromString(f.readText()),
        intents = intentConfig
    )
}