package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.opencv.core.Core;
import static sample.RecognizeText.*;

public class Main extends Application {

    public static Stage primaryStagex;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStagex = primaryStage;
        primaryStage.setTitle("YAZLAB-1");
        primaryStage.setScene(new Scene(root, 846,770 ));
        primaryStage.show();
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        tesseract.setDatapath(TESS_DATA);
        tesseract.setLanguage("tur");

    }


    public static void main(String[] args) {
        launch(args);
    }
}
