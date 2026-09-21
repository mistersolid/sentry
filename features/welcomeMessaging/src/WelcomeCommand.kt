// PACKAGE
package features.welcomeMessaging

// IMPORT
import dev.kord.core.Kord
import dev.kord.common.entity.Snowflake
import dev.kord.core.behavior.interaction.respondEphemeral
import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent
import framework.Command
import persistence.GuildConfigStore

// CLASS
class WelcomeCommand(private val store: GuildConfigStore) : Command {
    override val name = "welcome-messaging"
    override val description = "Toggle automatic welcome messages for new members"

    override suspend fun register(kord: Kord, guildID: Snowflake?) {
        // Sub commands
    }

    override suspend fun execute(event: ChatInputCommandInteractionCreateEvent) {
        val guildId = event.interaction.data.guildId.value ?: return
        val enable = event.interaction.command.booleans["enabled"] ?: return
        store.setWelcomeEnabled(guildId, enable)
        event.interaction.respondEphemeral {
            content = if (enable) "Welcome messages on." else "Welcome messages off."
        }
    }
}