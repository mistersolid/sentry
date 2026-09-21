// PACKAGE
package features.phashMatching

// IMPORT
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent
import framework.Command

// OBJECT
object PhashCommand : Command {
    override val name = "phash-matching"
    override val description = "Continuously monitors image uploads, converting them to a pHash and matching them to " +
            "existing values. Deletes any images that match a pHash and mutes the user."

    override suspend fun register(kord: Kord, guildID: Snowflake?) {
        TODO("Not yet implemented")
    }
    override suspend fun execute(event: ChatInputCommandInteractionCreateEvent) {
        TODO("Not yet implemented")
    }
}