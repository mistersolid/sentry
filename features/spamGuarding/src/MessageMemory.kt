// PACKAGE
package features.spamGuarding

// IMPORT
import dev.kord.core.entity.Message

// DATA CLASS
data class StoredMessage(
    val userId: Long,
    val guildId: Long,
    val channelId: Long,
    val text: String,
    val timestamp: Long,
    val message: Message,
)

// CLASS
class MessageMemory {
    private data class AuthorKey(val guildId: Long, val userId: Long)

    private val messages = mutableMapOf<AuthorKey, MutableList<StoredMessage>>()

    @Synchronized
    fun addMessage(message: Message, guildId: Long): StoredMessage? {
        val userId = message.author?.id?.value?.toLong() ?: return null
        val storedMessage = StoredMessage(
            userId = userId,
            guildId = guildId,
            channelId = message.channelId.value.toLong(),
            text = message.content,
            timestamp = message.timestamp.toEpochMilliseconds(),
            message = message,
        )

        messages.getOrPut(AuthorKey(guildId, userId)) { mutableListOf() }.add(storedMessage)
        return storedMessage
    }

    @Synchronized
    fun getMessage(guildId: Long, userId: Long): List<StoredMessage> {
        return messages[AuthorKey(guildId, userId)]?.toList() ?: emptyList()
    }

    @Synchronized
    fun pruneMessages(maxAgeSeconds: Long) {
        val cutoff = System.currentTimeMillis() - maxAgeSeconds * 1000

        messages.entries.removeIf { (_, userMessages) ->
            userMessages.removeIf { it.timestamp < cutoff }
            userMessages.isEmpty()
        }
    }

    @Synchronized
    fun removeMessages(storedMessages: Collection<StoredMessage>) {
        val messagesToRemove = storedMessages.toSet()
        messages.values.forEach { it.removeAll(messagesToRemove) }
        messages.entries.removeIf { (_, userMessages) -> userMessages.isEmpty() }
    }

    @Synchronized
    fun addAndFindDuplicates(
        message: Message,
        guildId: Long,
        maxAgeSeconds: Long,
    ): List<StoredMessage> {
        val storedMessage = addMessage(message, guildId) ?: return emptyList()
        pruneMessages(maxAgeSeconds)

        val matching = getMessage(guildId, storedMessage.userId).filter { it.text == storedMessage.text }
        if (matching.size <= 3 || matching.map { it.channelId }.distinct().size < 2) return emptyList()

        removeMessages(matching)
        return matching
    }
}