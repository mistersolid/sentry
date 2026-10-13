// PACKAGE
package features.spamGuarding

// IMPORT
import dev.kord.core.Kord
import dev.kord.core.behavior.edit
import dev.kord.core.entity.Message
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import persistence.GuildConfigStore
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days

// CLASS
class GuardFeature(private val store: GuildConfigStore) {
    fun build(
        message: Message,
        messageMemory: MessageMemory,
        guildId: Long,
        maxAgeSeconds: Long = DEFAULT_MESSAGE_MAX_AGE_SECONDS,
    ): List<StoredMessage> {
        if (message.content.isBlank() || message.author?.isBot == true) return emptyList()
        return messageMemory.addAndFindDuplicates(message, guildId, maxAgeSeconds)
    }

    fun install(kord: Kord) {
        val messageMemory = MessageMemory()

        kord.on<MessageCreateEvent> {
            val guildId = guildId?.value?.toLong() ?: return@on
            if (!store.isGuardEnabled(guildId)) return@on

            val duplicates = build(message, messageMemory, guildId)

            if (duplicates.isNotEmpty()) {
                duplicates.forEach { duplicate ->
                    runCatching { duplicate.message.delete() }
                }

                val eventMember = member ?: return@on
                runCatching {
                    eventMember.edit {
                        communicationDisabledUntil = Clock.System.now().plus(7.days)
                    }
                }

                runCatching {
                    val dmChannel = eventMember.getDmChannel()
                    val guild = eventMember.getGuild()
                    dmChannel.createMessage(
                        "You've been muted for one week in ${guild.name}. Reason: spamming multiple channels " +
                                "with the same message in a short period of time. If you disagree with this decision, " +
                                "you may contact a staff member to appeal. Further measures may be taken."
                    )
                }
            }
        }
    }

    companion object {
        private const val DEFAULT_MESSAGE_MAX_AGE_SECONDS = 60L
    }
}