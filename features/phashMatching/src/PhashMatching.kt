// PACKAGE
package features.phashMatching

// IMPORT
// OpenCV is a Java package this projects uses with Kotlin/Java interoperability
import org.opencv.core.Core
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.MatOfByte
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import kotlin.math.sqrt

// CONSTANT
private const val HASH_SIZE = 8
private const val HIGHFREQ_FACTOR = 4

// FUNCTION
/**
 * Computes a 64-bit perceptual hash (pHash) for the image at [imgSource] and returns it as a 16-character hex string.
 *
 * The algorithm converts the image to grayscale, resizes it to 32x32, computes the Discrete Cosine Transform (DCT),
 * extracts the top-left 8x8 low-frequency components (with orthonormal correction for SciPy/imagehash compatibility),
 * thresholds them against their median value, and encodes the resulting 64 bits into hexadecimal format.
 *
 * @param imgSource the path to the image file.
 * @return a 16-character hexadecimal string representing the perceptual hash.
 * @throws IllegalArgumentException if the image cannot be loaded or is empty.
 */
fun perceptualHash(imgSource: String): String {
    val img = if (imgSource.startsWith("http://") || imgSource.startsWith("https://")) {
        val bytes = java.net.URL(imgSource).readBytes()
        Imgcodecs.imdecode(MatOfByte(*bytes), Imgcodecs.IMREAD_COLOR)
    } else {
        Imgcodecs.imread(imgSource, Imgcodecs.IMREAD_COLOR)
    }
    require(!img.empty()) { "Image is empty" }

    val gray = Mat()
    Imgproc.cvtColor(img, gray, Imgproc.COLOR_BGR2GRAY)

    val imgSize = HASH_SIZE * HIGHFREQ_FACTOR
    val resized = Mat()

    Imgproc.resize(gray, resized, org.opencv.core.Size(
        imgSize.toDouble(),
        imgSize.toDouble()),
        0.0,
        0.0,
        Imgproc.INTER_AREA
    )

    val f = Mat()
    resized.convertTo(f, CvType.CV_32F)   // Mutates object assigned to 'f' (convention due to OpenCV C++ inheritance)

    val dct = Mat()
    Core.dct(f, dct)        // Mutates object assigned to 'dct' (convention due to OpenCV C++ inheritance)

    // LLM authored snippet
    // OpenCV's DCT is orthonormal; scipy's dct(type=2, norm=None) is not.
    // They differ by a factor of sqrt(2) on the k=0 row and column, which
    // shifts the median. Correct for it before thresholding.
    val low = Array(HASH_SIZE) { DoubleArray(HASH_SIZE) }
    val flat = DoubleArray(HASH_SIZE * HASH_SIZE)
    var n = 0
    for (i in 0 until HASH_SIZE) {
        for (j in 0 until HASH_SIZE) {
            var v = dct.get(i, j)[0]
            if (i == 0) v *= sqrt(2.0)      // OpenCV/SciPy backwards compatibility
            if (j == 0) v *= sqrt(2.0)      // OpenCV/SciPy backwards compatibility
            low[i][j] = v
            flat[n++] = v
        }
    }

    // LLM authored snippet
    // numpy.median: average of the two middle values for an even-sized array
    flat.sort()
    val median = (flat[31] + flat[32]) / 2.0

    val bits = StringBuilder(64)
    for (i in 0 until HASH_SIZE) {
        for (j in 0 until HASH_SIZE) {
            bits.append(if (low[i][j] > median) '1' else '0')
        }
    }

    // LLM authored snippet
    // 4 bits per hex char, MSB first — matches imagehash._binary_array_to_hex
    return (0 until 64 step 4).joinToString("") {
        bits.substring(it, it + 4).toInt(2).toString(16)
    }
}

/** Hamming distance between two hex pHashes, equivalent to `hash1 - hash2`. */
fun hammingDistance(a: String, b: String): Int {
    require(a.length == b.length) { "Hash length mismatch" }
    return a.indices.sumOf { (a[it].digitToInt(16) xor b[it].digitToInt(16)).countOneBits() }
}