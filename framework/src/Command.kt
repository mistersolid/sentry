// PACKAGE
package framework

// IMPORT
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent

// INTERFACE
/**
 * Represents a slash command that can be registered with Discord and executed
 * in response to user interactions. Slash commands allow users to interact
 * with bots using commands prefixed with a forward slash (/).
 *
 * Each implementation of this interface should define its specific behavior
 * during registration and execution.
 * @param name The name of the slash command.
 * @param description A brief description of what the slash command does.
 */
interface Command {
    val name: String
    val description: String
    suspend fun register(kord: Kord, guildID: Snowflake?)
    suspend fun execute(event: ChatInputCommandInteractionCreateEvent)
}