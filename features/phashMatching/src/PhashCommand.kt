// PACKAGE
package features.phashMatching

// IMPORT
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.respondEphemeral
import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent
import dev.kord.rest.builder.interaction.boolean
import framework.Command
import persistence.GuildConfigStore

// CLASS
/**
 * A command that allows toggling automatic pHash matching for image uploads in a guild.
 *
 * This command interacts with a provided [GuildConfigStore] to enable or disable
 * phash matching for a specific guild. When executed, it will respond to the user
 * with the current status of the welcome message configuration.
 *
 * @property store The configuration store used to persist the welcome message state for guilds.
 */
class PhashCommand(private val store: GuildConfigStore): Command {
    override val name = "phash-matching"
    override val description = "Toggle automatic image monitoring and moderation upon image upload"

    override suspend fun register(kord: Kord) {
        kord.createGlobalChatInputCommand(name, description) {
            boolean("toggle", "Enable or disable welcome messages") {
                required = true
            }
        }
    }

    override suspend fun action(event: ChatInputCommandInteractionCreateEvent) {
        val guildId = event.interaction.data.guildId.value ?: return
        val toggle = event.interaction.command.booleans["toggle"] ?: return

        store.setPhashEnabled(guildId.value.toLong(), toggle)

        event.interaction.respondEphemeral {
            content = if (toggle) "pHash matching on." else "pHash matching off."
        }
    }
}