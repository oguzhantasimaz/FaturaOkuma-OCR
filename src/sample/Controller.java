package sample;
import org.opencv.core.Core;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import com.jfoenix.controls.JFXComboBox;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import org.opencv.core.Mat;

import java.io.File;
import java.net.URL;
import java.sql.ResultSet;
import java.util.ResourceBundle;

import static org.opencv.imgcodecs.Imgcodecs.CV_LOAD_IMAGE_COLOR;
import static org.opencv.imgcodecs.Imgcodecs.imread;


public class Controller implements Initializable {
    @FXML
    private JFXComboBox<String> paramCBox;

    @FXML
    private TableView Table;

    @FXML
    private VBox vbox;

    @FXML
    private ImageView Image;

    @FXML
    private Pane navPane;

    private static File selectedPhoto;
    private ResultSet rs;
    private Connectivity connectivity = new Connectivity();
    private ObservableList<ObservableList> bill = FXCollections.observableArrayList();
    private ObservableList<String> Columns = FXCollections.observableArrayList
            ("Tarih","Fiş No","KDV","Toplam Fiyat","İşletme Adı");

    /**
     * Fotoğrafı yükleyen metod.
     * @param event
     */
    @FXML
    private void upload_image(ActionEvent event) {
        FileChooser fc = new FileChooser();
        selectedPhoto = fc.showOpenDialog(Main.primaryStagex);
        if (selectedPhoto != null) {
            openNewImageWindow(selectedPhoto);
        }
    }

    /****************************************************************************************************************************************/

    /**
     * Seçilen fotoğrafı açan fonksiyon.
     * upload_image fonksiyonu ile
     * paralel çalışır.
     * @param file
     */
    private void openNewImageWindow(File file) {

        Image image = new Image(file.toURI().toString());

        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(0, 10, 0, 10));
        vbox.getChildren().addAll(Image);

        Image.setFitHeight(400);
        Image.setPreserveRatio(true);
        Image.setImage(image);
        Image.setSmooth(true);
        Image.setCache(true);

    }

    /****************************************************************************************************************************************/


    /**
     * Veritabanındaki tüm elemanları yazdıran metod.
     */

    void FillAll(){
        Table.getColumns().clear();
        bill.clear();
        try{
            connectivity.ConnectDB();
            String SQL = "SELECT * from fatura";
            rs = connectivity.myConn.createStatement().executeQuery(SQL);
            fillList();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /****************************************************************************************************************************************/


    /**
     * FillAll ve SearchFill metodlarının içerisinde kullanılan listeyi dolduran
     * ortak metod.
     */

    void fillList(){
        try {
            for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                final int j = i;
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i + 1));
                col.setCellValueFactory((Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>)
                        param -> new SimpleStringProperty(param.getValue().get(j).toString()));
                Table.getColumns().addAll(col);
            }

            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    row.add(rs.getString(i));
                }
                bill.add(row);
            }
            Table.setItems(bill);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /****************************************************************************************************************************************/


    /**
     * Kullanıcı arama yaptıktan sonra aradığı şeyi listeleyen metod.
     * @param event
     */

    @FXML
    void SearchFill(ActionEvent event) {
        String value = paramCBox.getValue();
        String query;
        Table.getColumns().clear();
        bill.clear();
        try {
            switch (value) {
                case ("Tarih"):
                    query = "select * from fatura where Tarih = " + value ;
                    rs = connectivity.myConn.createStatement().executeQuery(query);
                    while (rs.next()){
                        fillList();
                    }
                    break;

                case ("Fiş No"):
                    query = "select * from fatura where FisNo = " + value ;
                    rs = connectivity.myConn.createStatement().executeQuery(query);
                    while (rs.next()) {
                        fillList();
                    }
                    break;

                case ("KDV"):
                    query = "select * from fatura where KDV = " + value;
                    rs = connectivity.myConn.createStatement().executeQuery(query);
                    while (rs.next()) {
                        fillList();
                    }
                    break;

                case ("Toplam Fiyat"):
                    query = "select * from fatura where ToplamFiyat = " + value;
                    rs = connectivity.myConn.createStatement().executeQuery(query);
                    while (rs.next()) {
                        fillList();
                    }
                    break;

                case ("İşletme Adı"):
                    query = "select * from fatura where IsletmeAdi = " + value;
                    rs = connectivity.myConn.createStatement().executeQuery(query);
                    while (rs.next()) {
                        fillList();
                    }
                    break;


            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /****************************************************************************************************************************************/


    /**
     * Table javafx tablosuna fatura sql tablo verilerini yazdırır.
     */

    @FXML
    void read_image(ActionEvent event) {
        //FillAll();
        System.out.println("Start recognize text from image");


        // Read image
        Mat origin = imread(selectedPhoto.getAbsolutePath(),CV_LOAD_IMAGE_COLOR);
        String result = new RecognizeText().extractTextFromImage(origin);
        System.out.println(result);
        System.out.println("Done");
        navPane.setVisible(true);
    }

    /****************************************************************************************************************************************/


    /**
     * UI üzerinde bulunan navPane adındaki Pane yapısını açıp kapatan metodlar.
     * @param event
     */
    @FXML
    void closeTable(MouseEvent event) {
        navPane.setVisible(false);
    }

    @FXML
    void openTable(ActionEvent event) {
        navPane.setVisible(true);
    }

    /****************************************************************************************************************************************/

    /**
     * Fotoğraf okunduktan sonra yeni fotoğraf açmak için kullanılan metod.
     * @param event
     */
    @FXML
    void done(ActionEvent event){
        vbox.getChildren().clear();
    }

    /****************************************************************************************************************************************/


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        paramCBox.setItems(Columns);
    }
}
