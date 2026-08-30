// PACKAGE
package features.welcomeMessaging

// IMPORT
import kotlin.random.Random

// FUNCTION
// TODO: Write doc
/**
 * @throws IllegalArgumentException - if [n] is negative.
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