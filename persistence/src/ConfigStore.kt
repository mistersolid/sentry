// PACKAGE
package persistence

// IMPORT
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

// OBJECT
internal object GuildConfig : Table("guild_config") {
    val key = varchar("key", 32)
    val enabled = bool("enabled")
    override val primaryKey = PrimaryKey(key)

    const val WELCOME = "welcome"
    const val PHASH = "phash"
    const val GUARD = "guard"
}

// CLASS
internal class ConfigStore(private val database: Database): GuildConfigStore {
    // WELCOME
    override suspend fun isWelcomeEnabled(): Boolean {
        return transaction(database) {
            isEnabled(GuildConfig.WELCOME)
        }
    }

    override suspend fun toggleWelcome(enabled: Boolean) {
        transaction(database) {
            setEnabled(GuildConfig.WELCOME, enabled)
        }
    }

    // PHASH
    override suspend fun isPhashEnabled(): Boolean {
        return transaction(database) {
            isEnabled(GuildConfig.PHASH)
        }
    }

    override suspend fun togglePhash(enabled: Boolean) {
        transaction(database) {
            setEnabled(GuildConfig.PHASH, enabled)
        }
    }

    // GUARD
    override suspend fun isGuardEnabled(guildId: Long): Boolean {
        return transaction(database) {
            isEnabled(guardKey(guildId))
        }
    }

    override suspend fun toggleGuard(guildId: Long, enabled: Boolean) {
        transaction(database) {
            setEnabled(guardKey(guildId), enabled)
        }
    }

    // GENERIC
    private fun isEnabled(feature: String): Boolean {
        return GuildConfig
            .select(GuildConfig.enabled)
            .where { GuildConfig.key eq feature }
            .singleOrNull()
            ?.get(GuildConfig.enabled)
            ?: false
    }

    private fun setEnabled(feature: String, enabled: Boolean) {
            val updated = GuildConfig.update({
                GuildConfig.key eq feature
            }) {
                it[GuildConfig.enabled] = enabled
            }

            if (updated == 0) {
                GuildConfig.insert {
                    it[GuildConfig.key] = feature
                    it[GuildConfig.enabled] = enabled
                }
            }
    }

    private fun guardKey(guildId: Long): String = "${GuildConfig.GUARD}:$guildId"
}

// OBJECT
internal object Config {
    fun connect(path: String = "bot.db"): Database {
        val database = Database.connect("jdbc:sqlite:$path", driver = "org.sqlite.JDBC")

        transaction(database) {
            SchemaUtils.create(GuildConfig)
        }
        return database
    }
}