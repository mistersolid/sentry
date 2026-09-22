// PACKAGE
package core

// CLASS
/**
 * Represents various roles within a guild, each associated with an ID and a category.
 *
 * @property id The unique Discord snowflake identifier of the role.
 * @property category The category of the role, distinguishing its type.
 *
 * @constructor Creates a `GuildRole` with a specific ID and associated category.
 *
 * The roles are divided into two primary categories:
 * - `SUBJECT`: Includes roles related to disciplines or fields of study.
 * - `BACKGROUND`: Includes roles indicating educational background or level.
 *
 * Companion Object:
 * Provides utility functions, such as finding a role by its ID.
 *
 * Enumeration values:
 * - `SOCIAL_SCIENCES`, `BIOLOGY`, `CHEMISTRY`, `ENGINEERING`, `COMPUTER_SCIENCE`, `DATA_SCIENCE`,
 *   `MATHEMATICS`, `PHYSICS`, `PLANETARY_SCIENCE` (Subject-related roles)
 * - `HIGH_SCHOOL`, `UNDERGRAD`, `BACHELORS`, `GRAD_STUDENT`, `MASTERS`, `PHD` (Background-related roles)
 */
enum class GuildRole(val id: Long, val category: RoleCategory) {
    // SUBJECT
    SOCIAL_SCIENCES(1193909132986687551, RoleCategory.SUBJECT),
    BIOLOGY(1187806497338560552, RoleCategory.SUBJECT),
    CHEMISTRY(1187806494331244625, RoleCategory.SUBJECT),
    ENGINEERING(1187806643136774305, RoleCategory.SUBJECT),
    COMPUTER_SCIENCE(1187806499301503076, RoleCategory.SUBJECT),
    DATA_SCIENCE(1187806693443248128, RoleCategory.SUBJECT),
    MATHEMATICS(1187806471728136323, RoleCategory.SUBJECT),
    PHYSICS(1187806492540281004, RoleCategory.SUBJECT),
    PLANETARY_SCIENCE(1187806672861806662, RoleCategory.SUBJECT),

    // BACKGROUND
    HIGH_SCHOOL(1145221595422531665, RoleCategory.BACKGROUND),
    UNDERGRAD(1145222927059517551, RoleCategory.BACKGROUND),
    BACHELORS(1145224700155396127, RoleCategory.BACKGROUND),
    GRAD_STUDENT(1145223621527228417, RoleCategory.BACKGROUND),
    MASTERS(1193908865952120923, RoleCategory.BACKGROUND),
    PHD(1145225226507014245, RoleCategory.BACKGROUND),
    ;

    // CATEGORY
    enum class RoleCategory {
        SUBJECT, BACKGROUND
    }

    // COMPANION
    companion object {
        private val byId = entries.associateBy { it.id }

        /**
         * Find a [GuildRole] by its Discord [`snowflake`](https://docs.discord.com/developers/reference#snowflakes).
         */
        fun fromId(id: Long): GuildRole? = byId[id]
    }
}