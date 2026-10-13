// PACKAGE
package features.spamGuarding

// IMPORT
import dev.kord.common.entity.Permission
import dev.kord.common.entity.Permissions
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.respondEphemeral
import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent
import dev.kord.rest.builder.interaction.boolean
import framework.Command
import persistence.GuildConfigStore

// CLASS
/**
 * A command that allows server administrators to toggle the spam guarding feature within their guild.
 * Spam guarding monitors multiple channels for potential spam, aiding in automatic moderation.
 *
 * This command interacts with the underlying `GuildConfigStore` to persist the enabled status
 * of the spam guarding feature and provides feedback to the user when the command is executed.
 *
 * @property store The configuration store used to persist the spam guarding toggle state.
 * @property values A values store for additional guild-related data. Unused in this implementation.
 * @implements Command Defines this as a Discord command, enabling registration and interaction handling.
 */
class GuardCommand(private val store: GuildConfigStore): Command {
    override val name = "spam-guarding"
    override val description = "Toggle automatic multi-channel spam monitoring"

    override suspend fun register(kord: Kord) {
        kord.createGlobalChatInputCommand(name, description) {
            defaultMemberPermissions = Permissions(Permission.ManageGuild)

            boolean("toggle", "Enable or disable spam guarding") {
                required = true
            }
        }
    }

    override suspend fun handle(event: ChatInputCommandInteractionCreateEvent) {
        val guildId = event.interaction.data.guildId.value?.toString()?.toLongOrNull() ?: return
        val toggle = event.interaction.command.booleans["toggle"] ?: return

        store.toggleGuard(guildId, toggle)

        event.interaction.respondEphemeral {
            content = if (toggle) "Spam guarding on." else "Spam guarding off."
        }
    }
}