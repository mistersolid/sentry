// PACKAGE
package features.welcomeMessaging

// IMPORT
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.respondEphemeral
import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent
import dev.kord.rest.builder.interaction.boolean
import framework.Command
import persistence.GuildConfigStore

// CLASS
/**
 * A command that allows toggling automatic welcome messages for new members in a guild.
 *
 * This command interacts with a provided [GuildConfigStore] to enable or disable
 * welcome messages for a specific guild. When executed, it will respond to the user
 * with the current status of the welcome message configuration.
 *
 * @property store The configuration store used to persist the welcome message state for guilds.
 */
class WelcomeCommand(private val store: GuildConfigStore): Command {
    override val name = "welcome-messaging"
    override val description = "Toggle automatic welcome messages for new members"

    override suspend fun register(kord: Kord) {
        kord.createGlobalChatInputCommand(name, description) {
            boolean("toggle", "Enable or disable welcome messages") {
                required = true
            }
        }
    }

    override suspend fun execute(event: ChatInputCommandInteractionCreateEvent) {
        val guildId = event.interaction.data.guildId.value ?: return
        val toggle = event.interaction.command.booleans["toggle"] ?: return

        store.setWelcomeEnabled(guildId.value.toLong(), toggle)

        event.interaction.respondEphemeral {
            content = if (toggle) "Welcome messages on." else "Welcome messages off."
        }
    }
}