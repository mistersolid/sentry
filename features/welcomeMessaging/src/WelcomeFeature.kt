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
 * This class provides the capability to generate and send personalized welcome messages
 * for users in a guild, based on their roles and applicable prompts. It listens to member
 * updates and triggers the welcome message process when a member completes their pending state.
 *
 * @property store The configuration store used to retrieve guild-specific settings.
 */
class WelcomeFeature(private val store: GuildConfigStore) {
    suspend fun buildWelcome(member: Member): String {
        val profile = member.toProfile()
        return choosePrompt(profile).joinToString("\n")
    }

    companion object {

        /**
         * Converts [Member] into a [Profile] based on their assigned role IDs.
         */
        fun Member.toProfile(): Profile =
            Profile.fromIds(roleIds.map { it.value.toLong() })
    }

    fun install(kord: Kord) {
        kord.on<MemberUpdateEvent> {
            val wasPending = old?.isPending ?: return@on
            if (wasPending && !member.isPending) { buildWelcome(member) }
        }
    }
}