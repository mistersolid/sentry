// IMPORT
import dev.kord.common.entity.Snowflake
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

// CLASS
@Serializable
data class FileConfig(
    val guildID: Snowflake,
    val ownerID: Snowflake,
    val prefix: String = "!",
)

class Config(
    val token: String,
    file: FileConfig,
) {
    val guildID = file.guildID
    val ownerID = file.ownerID
    val prefix = file.prefix

    override fun toString() = "Config(guildID=$guildID, ownerID=$ownerID, prefix=$prefix)"
}

// FUNCTION
fun loadConfig(path: String = "config.json"): Config {
    val f = File(path)
    require(f.exists()) { "Missing $path" }
    return Config(
        token = System.getenv("TOKEN") ?: error("TOKEN is not set"),
        file = Json.decodeFromString(f.readText()),
    )
}