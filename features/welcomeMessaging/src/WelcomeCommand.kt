package features.welcomeMessaging

import core.Profile
import loadConfig
import dev.kord.core.behavior.interaction.respondPublic
import dev.kord.core.entity.Member
import dev.kord.core.entity.interaction.ChatInputCommandInteraction

@Suppress("unused")
class WelcomeCommand {
    suspend fun execute(interaction: ChatInputCommandInteraction) {
        val member = interaction.user.asMember(loadConfig().guildID)
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