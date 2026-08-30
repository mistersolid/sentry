// PACKAGE
package core

// CLASS
// TODO: Write doc
/**
 *
 */
@JvmInline
value class Profile(val roles: Set<GuildRole>) {
    operator fun contains(role: GuildRole): Boolean = role in roles

    // COMPOSITE
    val natural: Boolean
        get() = GuildRole.BIOLOGY in this ||
                GuildRole.CHEMISTRY in this ||
                GuildRole.PHYSICS in this ||
                GuildRole.PLANETARY_SCIENCE in this

    val experimental: Boolean
        get() = natural ||
                GuildRole.SOCIAL_SCIENCES in this ||
                GuildRole.DATA_SCIENCE in this

    val student: Boolean
        get() = GuildRole.HIGH_SCHOOL in this ||
                GuildRole.UNDERGRAD in this ||
                GuildRole.GRAD_STUDENT in this

    val graduate: Boolean
        get() = GuildRole.BACHELORS in this ||
                GuildRole.GRAD_STUDENT in this ||
                GuildRole.MASTERS in this ||
                GuildRole.PHD in this

    // COMPANION
    companion object {
        fun fromIds(ids: Iterable<Long>): Profile =
            Profile(ids.mapNotNullTo(HashSet()) { GuildRole.fromId(it) })
    }
}