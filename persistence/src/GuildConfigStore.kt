// PACKAGE
package persistence

// INTERFACE
/**
 * Interface for storing and retrieving configuration data for guilds.
 * This interface provides a contract to maintain separation of concerns between [persistence] and all other modules.
 */
interface GuildConfigStore {
    suspend fun isWelcomeEnabled(guildId: Long): Boolean
    suspend fun toggleWelcome(guildId: Long, enabled: Boolean)
    suspend fun isPhashEnabled(guildId: Long): Boolean
    suspend fun togglePhash(guildId: Long, enabled: Boolean)
}

/**
 * Factory object responsible for creating instances of [GuildConfigStore].
 * This factory simplifies the instantiation of the configuration store by initializing the underlying database
 * and returning an implementation of [GuildConfigStore].
 *
 * The [create] function connects to the SQLite database at the specified file path and initializes the required schema.
 * By default, the database file is named "bot.db".
 *
 * Intended to be used by modules that need to interact with guild-specific configurations.
 */
object GuildConfigStoreFactory {
    /**
     * Creates and initializes a configuration store for guild configurations.
     *
     * This method connects to the SQLite database located at the specified file path
     * and sets up the schema required for storing guild configuration data.
     * By default, the database file is named "bot.db".
     *
     * @param path The file path of the SQLite database to connect to. Defaults to "bot.db".
     * @return An implementation of [GuildConfigStore] for managing guild-specific configurations.
     */
    fun create(path: String = "bot.db"): GuildConfigStore {
        Config.connect(path)

        return ConfigStore()
    }
}