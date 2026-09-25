// PACKAGE
package persistence

// IMPORT
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.Table.Dual.long

// OBJECT
internal object PhashValues : Table("phash_values") {
    val id = integer("id").autoIncrement()
    val hashValue = long("hash_value")

    override val primaryKey = PrimaryKey(id)
}