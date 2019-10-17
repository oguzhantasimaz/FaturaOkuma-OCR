package sample;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;

import static org.opencv.core.CvType.CV_8UC1;
import static org.opencv.imgcodecs.Imgcodecs.IMREAD_GRAYSCALE;
import static org.opencv.imgcodecs.Imgcodecs.imwrite;
import static org.opencv.imgproc.Imgproc.*;

public class RecognizeText {

    // Source path content images
    static String TESS_DATA = "C:/Program Files (x86)/Tesseract-OCR/tessdata";

    // Create tess obj
    static Tesseract tesseract = new Tesseract();

    String extractTextFromImage(Mat inputMat) {

        int alpha =1;
        int beta = 50;

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        tesseract.setDatapath(TESS_DATA);
        tesseract.setLanguage("tur");

        String result = "";
        Mat gray = new Mat();

        cvtColor(inputMat, gray, COLOR_BGR2GRAY);
        //threshold(inputMat,gray , 125,130, THRESH_BINARY_INV);
        inputMat.convertTo(gray, -1, alpha, beta);

        imwrite("converted.jpg", gray);

        try {
            // Recognize text with OCR
            result = tesseract.doOCR(new File(  "converted.jpg"));
        } catch (TesseractException e) {
            e.printStackTrace();
        }

        return result;
    }
}