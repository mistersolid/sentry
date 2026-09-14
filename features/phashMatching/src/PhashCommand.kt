// PACKAGE
package features.phashMatching

import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent
import framework.Command

// IMPORT

// OBJECT
object PhashCommand : Command {
    override val name = "Phash Matching Feature"
    override val description = "Automatically welcomes new members to the server with random questions " +
            "questions from prompt catalog."

    override suspend fun register(kord: Kord, guildID: Snowflake?) {
        TODO("Not yet implemented")
    }
    override suspend fun execute(event: ChatInputCommandInteractionCreateEvent) {
        TODO("Not yet implemented")
    }
}