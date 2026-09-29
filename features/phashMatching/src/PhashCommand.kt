// PACKAGE
package features.phashMatching

// IMPORT
import dev.kord.common.entity.Permission
import dev.kord.common.entity.Permissions
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.respondEphemeral
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.entity.interaction.SubCommand
import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent
import dev.kord.rest.builder.interaction.attachment
import dev.kord.rest.builder.interaction.boolean
import dev.kord.rest.builder.interaction.subCommand
import framework.Command
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import persistence.GuildConfigStore
import persistence.GuildValuesStore

// CLASS
/**
 * Represents a command for managing perceptual hash (pHash) matching functionality in Discord guilds.
 *
 * This command allows toggling the pHash matching capability for automatic image monitoring and moderation,
 * as well as adding new images to the pHash database for similarity checks during moderation.
 *
 * @constructor Creates a new instance of the `PhashCommand` with the specified configuration and data stores.
 * @param store A `GuildConfigStore` instance for managing guild-specific configuration settings.
 * @param values A `GuildValuesStore` instance for storing and retrieving pHashes for comparison.
 */
class PhashCommand(private val store: GuildConfigStore, private val values: GuildValuesStore): Command {
    override val name = "phash-matching"
    override val description = "Toggle automatic image monitoring and moderation upon image upload"

    override suspend fun register(kord: Kord) {
        kord.createGlobalChatInputCommand(name, description) {
            defaultMemberPermissions = Permissions(Permission.ManageGuild)

            subCommand("toggle", "Enable or disable pHash matching") {
                boolean("enabled", "Enable or disable") { required = true }
            }
            subCommand("add", "Add an image to the pHash database") {
                attachment("image", "Image to add") { required = true }
            }
        }
    }

    override suspend fun handle(event: ChatInputCommandInteractionCreateEvent) {
        val cmd = event.interaction.command as? SubCommand ?: return

        when (cmd.name) {
            "toggle" -> handleToggle(event, cmd)
            "add" -> handleAdd(event, cmd)
        }
    }

    private suspend fun handleToggle(event: ChatInputCommandInteractionCreateEvent, cmd: SubCommand) {
        val guildId = event.interaction.data.guildId.value ?: return
        val enabled = cmd.booleans["enabled"] ?: return

        store.togglePhash(guildId.value.toLong(), enabled)
        event.interaction.respondEphemeral {
            content = if (enabled) "pHash matching on." else "pHash matching off."
        }
    }

    /**
     * Handles the addition of a new perceptual hash (pHash) for an image uploaded through a chat interaction command.
     *
     * This method validates the attached file to ensure it is an image, computes its pHash, and determines whether
     * the image is similar to any existing entries in the database based on a predefined similarity threshold.
     * If the image is already present, an appropriate response is sent. Otherwise, it adds the new image's pHash to the database.
     *
     * @param event The event triggered by the chat interaction command.
     * @param cmd The subcommand containing the attachment and other input data.
     */
    private suspend fun handleAdd(event: ChatInputCommandInteractionCreateEvent, cmd: SubCommand) {
        val attachment = cmd.attachments["image"] ?: return

        // Hashing downloads the image, so defer to avoid the 3s interaction timeout
        val response = event.interaction.deferEphemeralResponse()

        if (attachment.contentType?.startsWith("image/") != true) {
            response.respond { content = "Selected file isn't an image." }
            return
        }

        val newHash = try {
            withContext(Dispatchers.IO) { perceptualHash(attachment.url) }
        } catch (_: Exception) {
            response.respond { content = "Couldn't process selected image." }
            return
        }

        val exists = values.getValues().any { hammingDistance(it, newHash) <= HASH_THRESHOLD }
        if (exists) {
            response.respond { content = "This image already exists in the pHash matching database." }
        } else {
            values.addValue(newHash)
            response.respond { content = "The image has been added to the pHash matching database." }
        }
    }
}