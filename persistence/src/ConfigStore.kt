// PACKAGE
package persistence

// IMPORT
import dev.kord.common.entity.Snowflake
import org.jetbrains.exposed.sql.Table

// OBJECT
object GuildConfigs : Table("guild_configs") {
    // Guild
    val guildID = long("guild_id")
    override val primaryKey = PrimaryKey(guildID)

    // Welcome Messaging Feature
    val welcomeEnabled = bool("welcome_enabled").default(true)
    val welcomeChannel = long("welcome_channel").nullable()

    // pHash Matching Feature
}

// CLASS
class ConfigStore(path: String = "bot.db") : GuildConfigStore {
    override suspend fun isWelcomeEnabled(guildId: Snowflake): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun setWelcomeEnabled(guildId: Snowflake, enabled: Boolean) {
        TODO("Not yet implemented")
    }

    override suspend fun welcomeChannel(guildId: Snowflake): Snowflake? {
        TODO("Not yet implemented")
    }
}

// INTERFACE
interface GuildConfigStore {
    suspend fun isWelcomeEnabled(guildId: Snowflake): Boolean
    suspend fun setWelcomeEnabled(guildId: Snowflake, enabled: Boolean)
    suspend fun welcomeChannel(guildId: Snowflake): Snowflake?
}