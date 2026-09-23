// IMPORT
import dev.kord.core.Kord
import dev.kord.core.behavior.interaction.respondEphemeral
import dev.kord.core.event.interaction.ChatInputCommandInteractionCreateEvent
import dev.kord.core.on
import features.phashMatching.PhashCommand
import features.welcomeMessaging.WelcomeCommand
import features.welcomeMessaging.WelcomeFeature
import framework.Command
import org.slf4j.LoggerFactory
import persistence.GuildConfigStoreFactory

// VALUE
private val logger = LoggerFactory.getLogger("client")

// MAIN
suspend fun client() {
    // VALUE
    val config = loadConfig()
    val kord = Kord(config.token)
    val store = GuildConfigStoreFactory.create()

    // COMMAND
    val commands: List<Command> = listOf(
        WelcomeCommand(store),
        PhashCommand
    )
    val byName = commands.associateBy { it.name }
    commands.forEach { it.register(kord) }

    kord.on<ChatInputCommandInteractionCreateEvent> {
        val handler = byName[interaction.command.rootName] ?: return@on
        try {
            handler.execute(this)
        } catch (e: Exception) {
            logger.error("Command '${handler.name}' failed", e)
            interaction.respondEphemeral {
                content = "An error occurred: ${e.message}"
            }
        }
    }

    // FEATURE
    WelcomeFeature(store).install(kord)

    // INSTANCE
    kord.login {
        presence { playing("Untitled Goose Game") }
        intents = config.intents
    }
}