// PACKAGE
package framework

// IMPORT
import core.Profile
import features.welcomeMessaging.choosePrompt
import dev.kord.core.behavior.interaction.respondPublic
import dev.kord.core.entity.Member
import dev.kord.core.entity.interaction.ChatInputCommandInteraction
import loadConfig

// COMMAND
//class LoggingCommand {
//
//}
//
//class roleManagementCommand {
//
//}
//
//class phashMatchingCommand {
//
//}

@Suppress("unused")
class PromptCommand {
    suspend fun execute(interaction: ChatInputCommandInteraction) {
        val member = interaction.user.asMember(loadConfig().guildID)
        val profile = member.toProfile()
        val prompts = choosePrompt(profile)

        interaction.respondPublic {
            content = prompts.joinToString("\n")
        }
    }

    companion object {

        // TODO: Write doc
        /**
         *
         */
        fun Member.toProfile(): Profile =
            Profile.fromIds(roleIds.map { it.value.toLong() })
    }
}