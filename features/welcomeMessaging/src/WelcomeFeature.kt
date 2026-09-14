// PACKAGE
package features.welcomeMessaging

// IMPORT
import core.Profile
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.interaction.respondPublic
import dev.kord.core.entity.Member
import dev.kord.core.entity.interaction.ChatInputCommandInteraction

// CLASS
class WelcomeFeature {
    suspend fun execute(interaction: ChatInputCommandInteraction, guildID: Snowflake) {
        val member = interaction.user.asMember(guildID)
        val profile = member.toProfile()
        val prompts = choosePrompt(profile)

        interaction.respondPublic {
            content = prompts.joinToString("\n")
        }
    }

    companion object {

        /**
         * Converts [Member] into a [Profile] based on their assigned role IDs.
         */
        fun Member.toProfile(): Profile =
            Profile.fromIds(roleIds.map { it.value.toLong() })
    }
}