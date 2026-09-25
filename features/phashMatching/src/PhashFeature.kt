// PACKAGE
package features.phashMatching

// IMPORT
import dev.kord.core.Kord
import dev.kord.core.behavior.ban
import dev.kord.core.entity.Message
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import persistence.GuildConfigStore
import persistence.GuildValuesStore
import kotlin.time.Duration.Companion.minutes

// CLASS
class PhashFeature(private val store: GuildConfigStore, private val values: GuildValuesStore) {
    private val threshold = 8

    suspend fun build(message: Message): Boolean {
        val urls = message.attachments.map { it.url } + message.embeds.mapNotNull { it.thumbnail?.url }

        if (urls.isEmpty()) return false

        val hashList = values.getValues()

        for (url in urls) {
            val hash = try {
                perceptualHash(url)
            } catch (e: Exception) {
                print(e)
                continue
            }

            if (hashList.any { hammingDistance(it, hash) <= threshold }) return true
        }

        return false
    }

    /**
     * Installs the pHash matching feature for the specified [Kord] instance, monitoring and moderating
     * uploaded images that violate community guidelines.
     *
     * This method listens to `MessageCreateEvent`. If any message is flagged as violating community
     * guidelines through the `build` method, the message is deleted, and the author is banned with
     * an appropriate reason and configured message deletion duration.
     *
     * @param kord The [Kord] client instance to which the pHash matching feature will be attached.
     */
    fun install(kord: Kord) {
        kord.on<MessageCreateEvent> {
            val evaluate = build(this.message)
            val guild = this.guildId?.value?.toLong() ?: return@on

            if (!store.isPhashEnabled(guild)) return@on
            if (evaluate) {
                val member = this.message.getAuthorAsMember()
                val guild = member.getGuild()

                this.message.delete()
                member.ban {
                    reason = "You've been banned from ${guild.name} by Sentry. Reason: uploading an image identified " +
                            "to be in violation of our community guidelines. If you disagree with this decision, " +
                            "you may contact a staff member to appeal."
                    deleteMessageDuration = 5.minutes
                }
            }
        }
    }
}