// PACKAGE
package features.phashMatching

// IMPORT
// OpenCV is a Java package this projects uses with Kotlin/Java interoperability
import com.sun.tools.javac.comp.Todo
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc

// FUNCTION
// TODO: Write doc
/**
 *
 */
fun perceptualHash(imagePath: String): String {
    val image = Imgcodecs.imread(imagePath, Imgcodecs.IMREAD_COLOR)
    val resized = Mat()
    Imgproc.resize(image, resized, org.opencv.core.Size(8.0, 8.0))
    val gray = Mat()
    Imgproc.cvtColor(resized, gray, Imgproc.COLOR_BGR2GRAY)

    val hash = StringBuilder()
    for (i in 0 until gray.rows()) {
        for (j in 0 until gray.cols()) {
            hash.append(gray.get(i, j)[0])
        }
    }


    return ""
}