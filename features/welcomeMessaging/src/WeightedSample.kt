// PACKAGE
package features.welcomeMessaging

// IMPORT
import kotlin.random.Random

// FUNCTION
/**
 * Samples up to [n] unique elements from this list without replacement, weighted proportionally by [weight].
 *
 * Only elements with a positive weight (`weight(it) > 0`) are eligible for selection.
 * If [n] exceeds the number of eligible elements, all eligible elements are returned in randomly sampled order.
 *
 * @param n the number of elements to sample.
 * @param random the [Random.Default] instance used for random number generation.
 * @param weight a function returning the weight of an element; elements with non-positive weights are ignored.
 * @return a list containing up to [n] sampled elements.
 * @throws IllegalArgumentException if [n] is negative.
 */
fun <T> List<T>.sampleWeighted(
    n: Int,
    random: Random.Default,
    weight: (T) -> Int,
): List<T> {
    require(n >= 0) { "n must be non-negative" }

    val pool = filterTo(ArrayList()) { weight(it) > 0 }
    var total = pool.sumOf { weight(it) }
    val drawn = ArrayList<T>(minOf(n, pool.size))

    while(drawn.size < n && pool.isNotEmpty()) {
        var r = random.nextInt(total)
        var i = 0
        while (i < pool.lastIndex) {
            r -= weight(pool[i])
            if (r < 0) break
            i++
        }

        total -= weight(pool[i])
        drawn += pool.removeAt(i)
    }

    return drawn
}