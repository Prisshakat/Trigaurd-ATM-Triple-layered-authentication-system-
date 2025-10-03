import org.opencv.core.*;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.MatOfRect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

public class FaceDetection {

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Load Haar Cascade XML file (keep it in resources/xml folder)
        CascadeClassifier faceDetector = new CascadeClassifier("xml/haarcascade_frontalface_default.xml");

        // Load an image
        Mat image = Imgcodecs.imread("input/test_face.jpg");

        // Detect faces
        MatOfRect faceDetections = new MatOfRect();
        faceDetector.detectMultiScale(image, faceDetections);

        System.out.println("✅ Faces detected: " + faceDetections.toArray().length);

        // Draw rectangles around detected faces
        for (Rect rect : faceDetections.toArray()) {
            Imgproc.rectangle(image, rect.tl(), rect.br(), new Scalar(0, 255, 0), 2);
        }

        // Save the output image with rectangles
        Imgcodecs.imwrite("output/detected_face.jpg", image);

        System.out.println("✅ Face detection completed. Check output/detected_face.jpg");
    }
}
