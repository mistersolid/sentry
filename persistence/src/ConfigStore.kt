// PACKAGE
package persistence

// IMPORT
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

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
    @Suppress("UNUSED")
    override suspend fun isWelcomeEnabled(guildId: Long): Boolean {
        TODO("Not yet implemented")
    }

    @Suppress("UNUSED")
    override suspend fun toggleWelcome(guildId: Long, enabled: Boolean) {
        if (enabled) {
            GuildConfig.update({ GuildConfig.guildID eq guildId }) {
                it[welcomeEnabled] = true
            }
        } else {
            GuildConfig.update({ GuildConfig.guildID eq guildId }) {
                it[welcomeEnabled] = false
            }
        }
    }

    override suspend fun isPhashEnabled(guildId: Long): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun togglePhash(guildId: Long, enabled: Boolean) {
        if (enabled) {
            GuildConfig.update( { GuildConfig.guildID eq guildId }) {
                it[phashEnabled] = true
            }
        } else {
            GuildConfig.update({ GuildConfig.guildID eq guildId }) {
                it[phashEnabled] = false
            }
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