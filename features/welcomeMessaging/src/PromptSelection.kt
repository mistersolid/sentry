// PACKAGE
package features.welcomeMessaging

// IMPORT
import core.Profile
import kotlin.random.Random

// CONSTANT
private const val PREFIX = "    "
const val PROMPT_COUNT_DEFAULT = 5

// FUNCTION
// TODO: Write doc
/**
 *
 */
fun choosePrompt(
    profile: Profile,
    n: Int = PROMPT_COUNT_DEFAULT,
    random: Random = Random.Default,
): List<String> =
    promptCatalog
        .filter { it.applies(profile) }
        .distinctBy { it.text }
        .sampleWeighted(n, random as Random.Default) { it.weight }
        .map { PREFIX + it.text }