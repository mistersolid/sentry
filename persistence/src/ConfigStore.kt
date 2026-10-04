// PACKAGE
package persistence

// IMPORT
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import persistence.GuildConfig.welcomeEnabled

// OBJECT
internal object GuildConfig : Table("guild_config") {
    // Guild
    val guildID = long("guild_id")
    override val primaryKey = PrimaryKey(guildID)

    // Welcome Messaging Feature
    val welcomeEnabled = bool("welcome_enabled").default(true)

    // pHash Matching Feature
    val phashEnabled = bool("phash_enabled").default(true)
}

// CLASS
internal class ConfigStore: GuildConfigStore {
    override suspend fun isWelcomeEnabled(guildId: Long): Boolean {
        return GuildConfig
            .select(GuildConfig.welcomeEnabled)
            .where { GuildConfig.guildID eq guildId }
            .singleOrNull()
            ?.get(GuildConfig.welcomeEnabled)
            ?: false
    }

    override suspend fun toggleWelcome(guildId: Long, enabled: Boolean) {
        GuildConfig.update({ GuildConfig.guildID eq guildId }) {
            it[welcomeEnabled] = enabled
        }
    }

    override suspend fun isPhashEnabled(guildId: Long): Boolean {
        return GuildConfig
            .select(GuildConfig.phashEnabled)
            .where { GuildConfig.guildID eq guildId}
            .singleOrNull()
            ?.get(GuildConfig.phashEnabled)
            ?: false
    }

    override suspend fun togglePhash(guildId: Long, enabled: Boolean) {
        GuildConfig.update( { GuildConfig.guildID eq guildId }) {
            it[phashEnabled] = enabled
        }
    }
}

// OBJECT
internal object Config {
    fun connect(path: String = "bot.db") {
        Database.connect("jdbc:sqlite:$path", driver = "org.sqlite.JDBC")
        transaction {
            SchemaUtils.create(GuildConfig)
        }
    }
}