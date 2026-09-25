// PACKAGE
package persistence

// IMPORT
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

// OBJECT
object ValuesConfig : Table("phash_values") {
    val id = integer("id").autoIncrement()
    val guildID = long("guild_id").references(GuildConfig.guildID)
    val hashValue = varchar("hash_value", 16)

    override val primaryKey = PrimaryKey(id)
}

/**
 * Implementation of the [GuildValuesStore] interface for retrieving stored hash values.
 *
 * This class interacts with the `phash_values` table to fetch guild-specific hash strings.
 * The fetched values are primarily used for pHash matching or other hash-based operations.
 *
 * The retrieval operation is executed within a coroutine and leverages the `newSuspendedTransaction` function
 * to ensure all database operations are performed on a separate thread as specified by `Dispatchers.IO`.
 *
 * This class is intended to be used internally and should not be exposed outside the `persistence` package.
 */
internal class ValuesStore: GuildValuesStore {
    /**
     * Retrieves a list of hash values from the configured data source.
     *
     * This method executes a suspended database transaction to fetch all stored hash values
     * from the `phash_values` table. The operation is performed using a specified dispatcher
     * to ensure the database interaction is executed on a separate IO thread.
     *
     * @return A list of hash values as strings retrieved from the database.
     */
    override suspend fun getValues(): List<String> {
        return newSuspendedTransaction(Dispatchers.IO) {
            ValuesConfig.select(ValuesConfig.hashValue)
                .map { it[ValuesConfig.hashValue] }
        }
    }
}

// OBJECT
internal object Values {
    fun connect(path: String = "values.db") {
        Database.connect("jdbc:sqlite:$path", driver = "org.sqlite.JDBC")
        transaction {
            SchemaUtils.create(ValuesConfig)
        }
    }
}