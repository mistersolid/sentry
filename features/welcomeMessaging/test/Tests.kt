// PACKAGE
package features.welcomeMessaging

// IMPORT
import core.GuildRole.BIOLOGY
import core.GuildRole.COMPUTER_SCIENCE
import core.Profile
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import org.junit.jupiter.api.Test

// TEST
class WeightedSampleTest {
    private val random = Random.Default

    @Test
    fun `zero samples returns an empty list`() {
        assertEquals(emptyList(), listOf(1, 2, 3).sampleWeighted(0, random) { 1 })
    }

    @Test
    fun `negative sample count throws`() {
        assertFailsWith<IllegalArgumentException> {
            listOf(1).sampleWeighted(-1, random) { 1 }
        }
    }

    @Test
    fun `non-positive weights are excluded`() {
        val result = listOf("positive", "zero", "negative")
            .sampleWeighted(10, random) { if (it == "positive") 1 else 0 }

        assertEquals(listOf("positive"), result)
    }

    @Test
    fun `sampling does not repeat elements and is capped by the pool`() {
        val result = listOf(1, 2, 3).sampleWeighted(10, random) { 1 }

        assertEquals(3, result.size)
        assertEquals(3, result.toSet().size)
    }
}

class PromptSelectionTest {
    private val random = Random.Default

    @Test
    fun `default selection returns unique prefixed prompts`() {
        val prompts = choosePrompt(Profile(emptySet()), random = random)

        assertEquals(PROMPT_COUNT_DEFAULT, prompts.size)
        assertEquals(prompts.size, prompts.toSet().size)
        assertTrue(prompts.all { it.startsWith("    ") })
        assertTrue(prompts.all { it.removePrefix("    ") in promptCatalog.map { entry -> entry.text } })
    }

    @Test
    fun `selection only includes prompts applicable to the profile`() {
        val profile = Profile(setOf(COMPUTER_SCIENCE))
        val prompts = choosePrompt(profile, n = promptCatalog.size, random = random)
        val selected = prompts.map { it.removePrefix("    ") }
        val eligible = promptCatalog.filter { it.applies(profile) }.map { it.text }.toSet()

        assertTrue(selected.all { it in eligible })
        assertTrue("💻 What area of computer science do you think is most underexplored?" in selected)
        assertFalse("🧬 What biological discovery do you think changed the world the most?" in selected)
    }

    @Test
    fun `selection respects requested count`() {
        val prompts = choosePrompt(Profile(emptySet()), n = 1, random = random)

        assertEquals(1, prompts.size)
    }

    @Test
    fun `zero requested prompts is empty`() {
        val profile = Profile(setOf(BIOLOGY))
        val prompts = choosePrompt(profile, n = 0, random = random)

        assertTrue(prompts.isEmpty())
    }
}