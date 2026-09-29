/**
 *  This file was made entirely by generative AI.
 */

// PACKAGE
package features.phashMatching

// IMPORT
import com.sun.net.httpserver.HttpServer
import dev.kord.core.entity.Attachment
import dev.kord.core.entity.Embed
import dev.kord.core.entity.Message
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.MatOfInt
import org.opencv.core.Size
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import persistence.GuildConfigStore
import persistence.GuildValuesStore
import java.net.InetSocketAddress
import java.nio.file.Files
import java.nio.file.Path
import java.util.Random
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

// HELPER
private val opencv by lazy { nu.pattern.OpenCV.loadLocally() }

/** Random 8x8 color blocks scaled up. Different seeds give unrelated low-frequency structure. */
private fun blockImage(seed: Long, blocks: Int = 8, size: Int = 256): Mat {
    val small = Mat(blocks, blocks, CvType.CV_8UC3)
    val data = ByteArray(blocks * blocks * 3).also { Random(seed).nextBytes(it) }
    small.put(0, 0, data)

    val big = Mat()
    Imgproc.resize(small, big, Size(size.toDouble(), size.toDouble()), 0.0, 0.0, Imgproc.INTER_NEAREST)
    return big
}

private fun Mat.save(dir: Path, name: String, vararg params: Int): String {
    val path = dir.resolve(name).toString()
    check(Imgcodecs.imwrite(path, this, MatOfInt(*params))) { "Could not write $path" }
    return path
}

// TEST
class PhashMatchingTest {
    companion object {
        @JvmStatic
        @BeforeAll
        fun loadNative() {
            opencv
        }
    }

    @TempDir
    lateinit var dir: Path

    @Test
    fun `hash is 16 lowercase hex chars`() {
        val hash = perceptualHash(blockImage(1).save(dir, "a.png"))

        assertEquals(16, hash.length)
        assertTrue(hash.all { it in '0'..'9' || it in 'a'..'f' })
    }

    @Test
    fun `hash is deterministic`() {
        val path = blockImage(1).save(dir, "a.png")

        assertEquals(perceptualHash(path), perceptualHash(path))
    }

    @Test
    fun `resized image stays within threshold`() {
        val original = blockImage(1)
        val half = Mat().also { Imgproc.resize(original, it, Size(128.0, 128.0), 0.0, 0.0, Imgproc.INTER_AREA) }

        val distance = hammingDistance(
            perceptualHash(original.save(dir, "orig.png")),
            perceptualHash(half.save(dir, "half.png")),
        )

        assertTrue(distance <= HASH_THRESHOLD, "distance was $distance")
    }

    @Test
    fun `brightness shift stays within threshold`() {
        val original = blockImage(1)
        val brighter = Mat().also { original.convertTo(it, -1, 1.0, 20.0) }

        val distance = hammingDistance(
            perceptualHash(original.save(dir, "orig.png")),
            perceptualHash(brighter.save(dir, "bright.png")),
        )

        assertTrue(distance <= HASH_THRESHOLD, "distance was $distance")
    }

    @Test
    fun `jpeg recompression stays within threshold`() {
        val original = blockImage(1)

        val distance = hammingDistance(
            perceptualHash(original.save(dir, "orig.png")),
            perceptualHash(original.save(dir, "copy.jpg", Imgcodecs.IMWRITE_JPEG_QUALITY, 70)),
        )

        assertTrue(distance <= HASH_THRESHOLD, "distance was $distance")
    }

    @Test
    fun `unrelated images exceed threshold`() {
        val distance = hammingDistance(
            perceptualHash(blockImage(1).save(dir, "a.png")),
            perceptualHash(blockImage(2).save(dir, "b.png")),
        )

        assertTrue(distance > HASH_THRESHOLD, "distance was $distance")
    }

    @Test
    fun `missing file throws`() {
        assertFailsWith<IllegalArgumentException> {
            perceptualHash(dir.resolve("nope.png").toString())
        }
    }

    @Test
    fun `non-image file throws`() {
        val path = dir.resolve("fake.png").also { Files.writeString(it, "not an image") }

        assertFailsWith<IllegalArgumentException> { perceptualHash(path.toString()) }
    }

    @Test
    fun `http source matches file source`() {
        val path = blockImage(1).save(dir, "a.png")
        val bytes = Files.readAllBytes(Path.of(path))

        val server = HttpServer.create(InetSocketAddress("127.0.0.1", 0), 0)
        server.createContext("/img.png") { ex ->
            ex.sendResponseHeaders(200, bytes.size.toLong())
            ex.responseBody.use { it.write(bytes) }
        }
        server.start()

        try {
            val remote = perceptualHash("http://127.0.0.1:${server.address.port}/img.png")
            assertEquals(perceptualHash(path), remote)
        } finally {
            server.stop(0)
        }
    }

    // hammingDistance

    @Test
    fun `identical hashes have distance 0`() {
        assertEquals(0, hammingDistance("ffeeddccbbaa9988", "ffeeddccbbaa9988"))
    }

    @Test
    fun `fully opposite hashes have distance 64`() {
        assertEquals(64, hammingDistance("0000000000000000", "ffffffffffffffff"))
    }

    @Test
    fun `single bit difference is 1`() {
        assertEquals(1, hammingDistance("0000000000000000", "0000000000000001"))
    }

    @Test
    fun `distance counts bits across nibbles`() {
        // 0xf vs 0x0 = 4 bits, 0xa vs 0x5 = 4 bits
        assertEquals(8, hammingDistance("f0a0000000000000", "0050000000000000"))
    }

    @Test
    fun `hex case is ignored`() {
        assertEquals(0, hammingDistance("abcdefabcdefabcd", "ABCDEFABCDEFABCD"))
    }

    @Test
    fun `distance is symmetric`() {
        val a = "0123456789abcdef"
        val b = "fedcba9876543210"

        assertEquals(hammingDistance(a, b), hammingDistance(b, a))
    }

    @Test
    fun `length mismatch throws`() {
        assertFailsWith<IllegalArgumentException> { hammingDistance("00", "000") }
    }
}

class PhashFeatureTest {
    companion object {
        @JvmStatic
        @BeforeAll
        fun loadNative() {
            opencv
        }
    }

    @TempDir
    lateinit var dir: Path

    private val store = mockk<GuildConfigStore>(relaxed = true)
    private val values = mockk<GuildValuesStore>()
    private val feature = PhashFeature(store, values)

    private fun message(attachments: List<String> = emptyList(), thumbnails: List<String?> = emptyList()): Message {
        val attachmentMocks = attachments.map { u -> mockk<Attachment> { every { url } returns u } }.toSet()
        val embedMocks = thumbnails.map { t ->
            mockk<Embed> {
                every { thumbnail } returns t?.let { u -> mockk<Embed.Thumbnail> { every { url } returns u } }
            }
        }

        return mockk {
            every { this@mockk.attachments } returns attachmentMocks
            every { embeds } returns embedMocks
        }
    }

    private fun stored(seed: Long) = perceptualHash(blockImage(seed).save(dir, "stored$seed.png"))

    @Test
    fun `message without images returns false and skips the store`() = runTest {
        assertFalse(feature.build(message()))

        coVerify(exactly = 0) { values.getValues() }
    }

    @Test
    fun `attachment matching a stored hash is flagged`() = runTest {
        coEvery { values.getValues() } returns listOf(stored(1))
        val upload = blockImage(1).save(dir, "upload.png")

        assertTrue(feature.build(message(attachments = listOf(upload))))
    }

    @Test
    fun `slightly altered copy is flagged`() = runTest {
        coEvery { values.getValues() } returns listOf(stored(1))
        val altered = Mat().also { blockImage(1).convertTo(it, -1, 1.0, 20.0) }.save(dir, "altered.png")

        assertTrue(feature.build(message(attachments = listOf(altered))))
    }

    @Test
    fun `unrelated attachment is not flagged`() = runTest {
        coEvery { values.getValues() } returns listOf(stored(1))
        val upload = blockImage(2).save(dir, "upload.png")

        assertFalse(feature.build(message(attachments = listOf(upload))))
    }

    @Test
    fun `empty database never flags`() = runTest {
        coEvery { values.getValues() } returns emptyList()
        val upload = blockImage(1).save(dir, "upload.png")

        assertFalse(feature.build(message(attachments = listOf(upload))))
    }

    @Test
    fun `embed thumbnail is checked`() = runTest {
        coEvery { values.getValues() } returns listOf(stored(1))
        val thumb = blockImage(1).save(dir, "thumb.png")

        assertTrue(feature.build(message(thumbnails = listOf(thumb))))
    }

    @Test
    fun `embed without thumbnail is ignored`() = runTest {
        assertFalse(feature.build(message(thumbnails = listOf(null))))

        coVerify(exactly = 0) { values.getValues() }
    }

    @Test
    fun `undecodable image is skipped and later images still checked`() = runTest {
        coEvery { values.getValues() } returns listOf(stored(1))
        val bad = dir.resolve("bad.png").also { Files.writeString(it, "garbage") }.toString()
        val good = blockImage(1).save(dir, "good.png")

        assertTrue(feature.build(message(attachments = listOf(bad, good))))
    }

    @Test
    fun `only undecodable images returns false`() = runTest {
        coEvery { values.getValues() } returns listOf(stored(1))
        val bad = dir.resolve("bad.png").also { Files.writeString(it, "garbage") }.toString()

        assertFalse(feature.build(message(attachments = listOf(bad))))
    }

    @Test
    fun `any match among several attachments flags the message`() = runTest {
        coEvery { values.getValues() } returns listOf(stored(1))
        val clean = blockImage(2).save(dir, "clean.png")
        val bad = blockImage(1).save(dir, "bad.png")

        assertTrue(feature.build(message(attachments = listOf(clean, bad))))
    }

    @Test
    fun `stored hash set is fetched once per message`() = runTest {
        coEvery { values.getValues() } returns listOf(stored(1))
        val a = blockImage(2).save(dir, "a.png")
        val b = blockImage(3).save(dir, "b.png")

        feature.build(message(attachments = listOf(a, b)))

        coVerify(exactly = 1) { values.getValues() }
    }
}