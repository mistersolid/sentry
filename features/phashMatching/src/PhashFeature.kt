// PACKAGE
package features.phashMatching

// IMPORT
import dev.kord.core.Kord
import dev.kord.core.behavior.ban
import dev.kord.core.entity.Member
import dev.kord.core.entity.Message
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import persistence.GuildConfigStore
import kotlin.time.Duration.Companion.minutes

// CLASS
class PhashFeature(private val store: GuildConfigStore) {
    fun build(message: Message, member: Member): Boolean {
        if (message.attachments.isNotEmpty()) {
            for (i in message.attachments) {
                val url = i.proxyUrl
                perceptualHash(url)
            }
        }

        if (message.embeds.isNotEmpty()) {
            for (i in message.embeds) {
                val url = i.thumbnail?.url ?: ""
                val hash = perceptualHash(url)
            }
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
            val evaluate = build(this.message, this.member ?: return@on)

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