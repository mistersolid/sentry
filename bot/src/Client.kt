// IMPORT
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.respondEphemeral
import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent
import dev.kord.core.on
import features.phashMatching.PhashCommand
import features.welcomeMessaging.WelcomeCommand
import framework.Command
import persistence.GuildConfigStoreFactory

// MAIN
suspend fun client() {
    val config = loadConfig()
    val kord = Kord(config.token)
    val store = GuildConfigStoreFactory.create()

    val commands: List<Command> = listOf(
        WelcomeCommand(store),
        PhashCommand
    )
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