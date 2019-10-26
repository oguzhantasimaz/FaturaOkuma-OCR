package sample;

import javafx.scene.image.Image;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import java.io.File;
import static org.opencv.imgcodecs.Imgcodecs.imwrite;
import static org.opencv.imgproc.Imgproc.*;

public class RecognizeText {
    public static Image img;

    // Source path content images
    static String TESS_DATA = "C:/Program Files (x86)/Tesseract-OCR/tessdata";

    // Create tess obj
    static Tesseract tesseract = new Tesseract();

    String extractTextFromImage(Mat inputMat) {
        Controller cont = new Controller();

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        tesseract.setDatapath(TESS_DATA);
        tesseract.setLanguage("tur");

        String result = "";
        Mat gray = new Mat();

        cvtColor(inputMat, gray, COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(inputMat,gray,new Size(5, 5),0);
        gray.convertTo(gray,-1,cont.ALPHA,cont.BETA);

        System.out.println(cont.disableThreshold);
        if(cont.disableThreshold == false)
        {
            threshold(inputMat,gray , cont.MIN_THRESHOLD,cont.MAX_THRESHOLD, THRESH_BINARY_INV);
        }

        imwrite("src/sample/converted.jpg", gray);
        img = new Image("file:///converted.jpg");

        try {
            // Recognize text with OCR
            result = tesseract.doOCR(new File(  "src/sample/converted.jpg"));
        } catch (TesseractException e) {
            e.printStackTrace();
        }

        return result;
    }
}