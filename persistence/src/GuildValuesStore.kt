// PACKAGE
package persistence

// IMPORT

// OBJECT
interface GuildValuesStore {
    suspend fun getValues(): List<String>
}

object GuildValuesFactory {
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
    fun create(path: String = "values.db"): GuildValuesStore {
        Values.connect(path)

        return ValuesStore()
    }
}