// PACKAGE
package features.phashMatching

// IMPORT
import dev.kord.core.Kord
import dev.kord.core.behavior.ban
import dev.kord.core.behavior.edit
import dev.kord.core.behavior.interaction.response.respond
import dev.kord.core.entity.Message
import dev.kord.core.event.interaction.MessageCommandInteractionCreateEvent
import dev.kord.core.event.message.MessageCreateEvent
import dev.kord.core.on
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonNull.content
import persistence.GuildConfigStore
import persistence.GuildValuesStore
import kotlin.time.Clock
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

// CLASS
class PhashFeature(private val store: GuildConfigStore, private val values: GuildValuesStore) {

    /**
     * Analyzes a given message for image attachments and embedded thumbnails, computes their perceptual hashes,
     * and checks if they match any stored hashes within a predefined similarity threshold.
     *
     * @param message The message containing attachments and embeds to analyze.
     * @return `true` if any image's hash matches an existing stored hash within the similarity threshold; `false` otherwise.
     */
    suspend fun build(message: Message): Boolean {
        val urls = message.attachments.map { it.url } + message.embeds.mapNotNull { it.thumbnail?.url }

        if (urls.isEmpty()) return false

        val hashList = values.getValues()

        for (url in urls) {
            val hash = try {
                withContext(Dispatchers.IO) { perceptualHash(url) }
            } catch (e: Exception) {
                print(e)
                continue
            }

            if (hashList.any { hammingDistance(it, hash) <= HASH_THRESHOLD }) return true
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
            if (this.guildId == null) return@on

            if (!store.isPhashEnabled()) return@on
            val evaluate = build(this.message)
            if (evaluate) {
                val member = this.message.getAuthorAsMember()
                val dmChannel = member.getDmChannel()
                val guild = member.getGuild()

                this.message.delete()

                member.edit {
                    communicationDisabledUntil = Clock.System.now().plus(7.days)
                }

                dmChannel.createMessage("You've been muted for one week in ${guild.name}. Reason: uploading an image identified " +
                        "to be in violation of our community guidelines. If you disagree with this decision, " +
                        "you may contact a staff member to appeal. Further measures may be taken.")
            }
        }

        kord.on<MessageCommandInteractionCreateEvent> {
            val message = this.interaction.target.fetchMessage()
            val attachment = message.attachments
            val evaluate = build(message)

            // Hashing downloads the image, so defer to avoid the 3s interaction timeout
            val response = interaction.deferEphemeralResponse()

            for (i in attachment) {
                if (!i.isImage) {
                    response.respond { content = "No image was attached to the message." }
                }

                val newHash = try {
                    withContext(Dispatchers.IO) {
                        perceptualHash(i.url)
                    }
                } catch (_: Exception) {
                    response.respond { content = "Couldn't process selected image." }
                    return@on
                }

                val exists = values.getValues().any { hammingDistance(it, newHash as String) <= HASH_THRESHOLD }
                if (exists) {
                    response.respond { content = "This image already exists in the pHash matching database." }
                } else {
                    values.addValue(newHash as String)
                    response.respond { content = "The image has been added to the pHash matching database." }
                }

                if (evaluate) {
                    val member = message.getAuthorAsMember()
                    val dmChannel = member.getDmChannel()
                    val guild = member.getGuild()

                    message.delete()

                    member.edit {
                        communicationDisabledUntil = Clock.System.now().plus(7.days)
                    }

                    dmChannel.createMessage(
                        "You've been muted for one week in ${guild.name}. Reason: uploading an image identified " +
                                "to be in violation of our community guidelines. If you disagree with this decision, " +
                                "you may contact a staff member to appeal. Further measures may be taken."
                    )

                    response.respond { content = "User ${member.mention} has been muted for one week." }

                    return@on
                }
            }
        }
    }
}