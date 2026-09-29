// PACKAGE
package features.welcomeMessaging

// IMPORT
import core.Profile
import kotlin.random.Random

// CONSTANT
private const val PREFIX = "    "   // Prompt indent
const val PROMPT_COUNT_DEFAULT = 3

// FUNCTION
/**
 * Selects a list of prompt strings based on the given profile and weighted sampling criteria.
 *
 * This method filters the available prompts by their applicability to the provided profile,
 * removes duplicates by their text content, performs weighted sampling to select up to [n] prompts,
 * and adds a prefix to each selected prompt before returning the result.
 *
 * @param profile the user profile containing roles used to filter applicable prompts.
 * @param n the number of prompts to select (default value is [PROMPT_COUNT_DEFAULT]).
 * @param random the random number generator used for weighted sampling (default value is [Random.Default]).
 * @return a list of selected and processed prompt strings.
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