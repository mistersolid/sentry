// PACKAGE
package core

// CLASS
// TODO: Write doc
/**
 *
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