// PACKAGE
package persistence

// IMPORT
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

// OBJECT
internal object GuildConfig : Table("guild_config") {
    // Guild
    val guildID = long("guild_id")
    override val primaryKey = PrimaryKey(guildID)

    // Welcome Messaging Feature
    val welcomeEnabled = bool("welcome_enabled").default(true)

    // pHash Matching Feature
    val phashEnabled = bool("phash_enabled").default(true)
    val phashValues = Table("phash_values")
}

// CLASS
internal class ConfigStore: GuildConfigStore {
    @Suppress("UNUSED")
    override suspend fun isWelcomeEnabled(guildId: Long): Boolean {
        TODO("Not yet implemented")
    }

    @Suppress("UNUSED")
    override suspend fun setWelcomeEnabled(guildId: Long, enabled: Boolean) {
        TODO("Not yet implemented")
    }

    override suspend fun isPhashEnabled(guildId: Long): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun setPhashEnabled(guildId: Long, enabled: Boolean) {
        TODO("Not yet implemented")
    }
}

// OBJECT
internal object Database {
    fun connect(path: String = "bot.db") {
        Database.connect("jdbc:sqlite:$path", driver = "org.sqlite.JDBC")
        transaction {
            SchemaUtils.create(GuildConfig)
        }
    }
}