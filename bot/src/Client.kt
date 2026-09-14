// IMPORT
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.respondEphemeral
import dev.kord.core.behavior.interaction.respondPublic
import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent
import dev.kord.core.on
import features.phashMatching.PhashCommand
import features.welcomeMessaging.WelcomeCommand
import framework.Command
import kotlinx.serialization.json.JsonNull.content

// MAIN
val commands: List<Command> = listOf(WelcomeCommand, PhashCommand)

suspend fun client() {
    val config = loadConfig()
    val kord = Kord(config.token)

    val byName = commands.associateBy { it.name }
    commands.forEach { it.register(kord, config.guildID) }

    kord.on<ChatInputCommandInteractionCreateEvent> {
        val handler = byName[interaction.command.rootName] ?: return@on
        try {
            handler.execute(this)
        } catch (e: Exception) {
            interaction.respondEphemeral {
                content = "An error occurred: ${e.message}"
            }
        }
    }

    kord.login {
        presence { playing("") }
        intents = config.intents
    }
}