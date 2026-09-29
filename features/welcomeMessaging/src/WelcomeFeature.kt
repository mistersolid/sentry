// PACKAGE
package features.welcomeMessaging

// IMPORT
import core.Profile
import dev.kord.core.Kord
import dev.kord.core.entity.Member
import dev.kord.core.event.guild.MemberUpdateEvent
import dev.kord.core.on
import persistence.GuildConfigStore

// CLASS
/**
 * A feature implementing welcome message functionality for guild members.
 *
 * This class provides the ability to generate and send welcome messages
 * for users in a guild, based on their roles and applicable prompts. It listens to member
 * updates and triggers the welcome message process when a member completes their pending state.
 *
 * @property store The configuration store used to retrieve guild-specific settings.
 */
class WelcomeFeature(private val store: GuildConfigStore) {
    fun build(member: Member): String {
        val profile = member.toProfile()
        val lines = choosePrompt(profile)
        return "${member.mention} " + lines.joinToString("\n")
    }

    companion object {

        /**
         * Converts [Member] into a [Profile] based on their assigned role IDs.
         */
        fun Member.toProfile(): Profile =
            Profile.fromIds(roleIds.map { it.value.toLong() })
    }

    /**
     * Installs the welcome message handler for the specified [Kord] instance.
     *
     * This method listens for [MemberUpdateEvent]s, specifically checking if a guild member
     * transitions from a "pending" state to a confirmed state. When this happens, it sends
     * a welcome message to the guild's system channel if the feature is enabled
     * for the corresponding guild.
     *
     * @param kord The [Kord] instance used to handle events and send messages.
     */
    fun install(kord: Kord) {
        kord.on<MemberUpdateEvent> {
            val wasPending = old?.isPending ?: return@on
            if (wasPending && !member.isPending) {
                val guildId = member.guildId.value.toLong()
                if (!store.isWelcomeEnabled(guildId)) return@on

                val guild = member.getGuild()
                val channel = guild.systemChannel ?: return@on

                val text = build(member)

                channel.createMessage(text)
            }
        }
    }
}