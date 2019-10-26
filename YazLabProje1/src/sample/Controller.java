package sample;

import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.ResourceBundle;
import org.opencv.core.Mat;
import java.util.StringTokenizer;
import static org.opencv.imgcodecs.Imgcodecs.*;


public class Controller implements Initializable {
    @FXML
    private JFXComboBox<String> paramCBox;

    @FXML
    private TableView Table;

    @FXML
    private TableView SearchTable;

    @FXML
    private VBox vbox_original;

    @FXML
    private GridPane conv_im_grid;

    @FXML
    private ImageView ImageV_original;

    @FXML
    private ImageView ImageV_converted;

    @FXML
    private Pane navPane;

    @FXML
    private Pane advancedSettings;

    @FXML
    private Pane searchPane;

    @FXML
    private JFXTextField search_value;

    @FXML
    private JFXSlider AlphaSlider;

    @FXML
    private JFXSlider Min_thresholdSlider;

    @FXML
    private JFXSlider Max_thresholdSlider;

    @FXML
    private JFXSlider BetaSlider;

    @FXML
    private JFXCheckBox disable_threshold;

    @FXML
    private JFXCheckBox disable_brightness;


    RecognizeText recog = new RecognizeText();
    private static File selected ;
    public static boolean disableThreshold ;
    public static int ALPHA = 1;
    public static int BETA = 50;
    public static int MIN_THRESHOLD = 100;
    public static int MAX_THRESHOLD =120;
    private ResultSet rs;
    private Connectivity connectivity = new Connectivity();
    private ObservableList<ObservableList> bill = FXCollections.observableArrayList();
    private ObservableList<ObservableList> searchBill = FXCollections.observableArrayList();
    private ObservableList <String> Columns = FXCollections.observableArrayList
            ("Tarih","FisNo","KDV","ToplamFiyat","Firma");

    /**
     * Fotoğrafı yükleyen metod.
     */
    @FXML
    void upload_image(ActionEvent event) {
        FileChooser fc = new FileChooser();
        selected = fc.showOpenDialog(Main.primaryStagex);
        if (selected != null) {open_original_Image();}
    }

    /**********************************************/

    /**
     * Seçilen fotoğrafı açan fonksiyon.
     * upload_image fonksiyonu ile
     * paralel çalışır.
     */
    private void open_original_Image() {
        Image image = new Image(selected.toURI().toString());
        vbox_original.setAlignment(Pos.CENTER);
        ImageV_original.setImage(image);
        vbox_original.getChildren().addAll(ImageV_original);
        ImageV_original.setFitHeight(400);
        ImageV_original.setFitWidth(400);
        ImageV_original.setPreserveRatio(true);
        ImageV_original.setSmooth(true);
        ImageV_original.setCache(true);
    }


    /***************** DATABASE *****************************/


    /**
     * Veritabanındaki tüm elemanları yazdıran metod.
     */

    void FillAll(){
        try{
            connectivity.ConnectDB();
            Table.getColumns().clear();
            bill.clear();
            //SQL FOR SELECTING ALL OF CUSTOMER
            String SQL = "SELECT * from fatura";
            //ResultSet
            ResultSet rs = connectivity.myConn.createStatement().executeQuery(SQL);

            /************
             * TABLE COLUMN ADDED DYNAMICALLY *
             ************/
            for(int i=0 ; i<rs.getMetaData().getColumnCount(); i++){
                //We are using non property style for making dynamic table
                final int j = i;
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i+1));
                col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                        return new SimpleStringProperty(param.getValue().get(j).toString());
                    }
                });

                Table.getColumns().addAll(col);
            }

            /************
             * Data added to ObservableList *
             ************/
            while(rs.next()){
                //Iterate Row
                ObservableList<String> row = FXCollections.observableArrayList();
                for(int i=1 ; i<=rs.getMetaData().getColumnCount(); i++){
                    //Iterate Column
                    row.add(rs.getString(i));
                }
                bill.add(row);

            }

            //FINALLY ADDED TO TableView
            Table.setItems(bill);
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("Error on Building Data");
        }
    }

    /**********************************************/


    /**
     * FillAll ve SearchFill metodlarının içerisinde kullanılan listeyi dolduran
     * ortak metod.
     */

    /**********************************************/
    void fillList(){

        try {
            /************
             * TABLE COLUMN ADDED DYNAMICALLY *
             ************/
            for(int i=0 ; i<rs.getMetaData().getColumnCount(); i++){
                //We are using non property style for making dynamic table
                final int j = i;
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i+1));
                col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){
                    public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                        return new SimpleStringProperty(param.getValue().get(j).toString());
                    }
                });

                SearchTable.getColumns().addAll(col);
                System.out.println("Column ["+i+"] ");
            }

            for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                final int j = i;
                TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i + 1));
                col.setCellValueFactory((Callback<TableColumn.CellDataFeatures<ObservableList, String>, ObservableValue<String>>)
                        param -> new SimpleStringProperty(param.getValue().get(j).toString()));
                SearchTable.getColumns().addAll(col);
            }

            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    row.add(rs.getString(i));
                }
                bill.add(row);
            }
            SearchTable.setItems(bill);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Kullanıcı arama yaptıktan sonra aradığı şeyi listeleyen metod.
     * @param event
     */
    @FXML
    void SearchFill(ActionEvent event) {
        connectivity.ConnectDB();
        Table.setVisible(false);
        SearchTable.setVisible(true);
        searchBill.clear();
        SearchTable.getColumns().clear();
        String value = paramCBox.getValue();
        String value2 = search_value.getText();
        String query;
        bill.clear();
        try {
            switch (value) {
                case ("Tarih"):
                    searchBill.clear();
                    SearchTable.getColumns().clear();
                    query = "select * from fatura where  Tarih =" + "'"+value2+"'";
                    rs = connectivity.myConn.createStatement().executeQuery(query);
                    for(int i=0 ; i<rs.getMetaData().getColumnCount(); i++){
                        //We are using non property style for making dynamic table
                        final int j = i;
                        TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i+1));
                        col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){
                            public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                                return new SimpleStringProperty(param.getValue().get(j).toString());
                            }
                        });

                        SearchTable.getColumns().addAll(col);
                    }
                    while(rs.next()){
                        //Iterate Row
                        ObservableList<String> row = FXCollections.observableArrayList();
                        for(int i=1 ; i<=rs.getMetaData().getColumnCount(); i++){
                            //Iterate Column
                            row.add(rs.getString(i));
                        }
                        searchBill.add(row);

                    }

                    //FINALLY ADDED TO TableView
                    SearchTable.setItems(searchBill);
                    break;

                case ("FisNo"):
                    searchBill.clear();
                    SearchTable.getColumns().clear();
                    query = "select * from fatura where FisNo = " + "'"+value2+"'";
                    rs = connectivity.myConn.createStatement().executeQuery(query);
                    for(int i=0 ; i<rs.getMetaData().getColumnCount(); i++){
                        //We are using non property style for making dynamic table
                        final int j = i;
                        TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i+1));
                        col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){
                            public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                                return new SimpleStringProperty(param.getValue().get(j).toString());
                            }
                        });

                        SearchTable.getColumns().addAll(col);
                    }
                    while(rs.next()){
                        //Iterate Row
                        ObservableList<String> row = FXCollections.observableArrayList();
                        for(int i=1 ; i<=rs.getMetaData().getColumnCount(); i++){
                            //Iterate Column
                            row.add(rs.getString(i));
                        }
                        searchBill.add(row);

                    }

                    //FINALLY ADDED TO TableView
                    SearchTable.setItems(searchBill);
                    break;

                case ("KDV"):
                    searchBill.clear();
                    SearchTable.getColumns().clear();
                    query = "select * from fatura where KDV = " + "'"+value2+"'";
                    rs = connectivity.myConn.createStatement().executeQuery(query);
                    for(int i=0 ; i<rs.getMetaData().getColumnCount(); i++){
                        //We are using non property style for making dynamic table
                        final int j = i;
                        TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i+1));
                        col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){
                            public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                                return new SimpleStringProperty(param.getValue().get(j).toString());
                            }
                        });

                        SearchTable.getColumns().addAll(col);
                    }
                    while(rs.next()){
                        //Iterate Row
                        ObservableList<String> row = FXCollections.observableArrayList();
                        for(int i=1 ; i<=rs.getMetaData().getColumnCount(); i++){
                            //Iterate Column
                            row.add(rs.getString(i));
                        }
                        searchBill.add(row);

                    }

                    //FINALLY ADDED TO TableView
                    SearchTable.setItems(searchBill);
                    break;

                case ("ToplamFiyat"):
                    searchBill.clear();
                    SearchTable.getColumns().clear();
                    query = "select * from fatura where ToplamFiyat = " + "'"+value2+"'";
                    rs = connectivity.myConn.createStatement().executeQuery(query);
                    for(int i=0 ; i<rs.getMetaData().getColumnCount(); i++){
                        //We are using non property style for making dynamic table
                        final int j = i;
                        TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i+1));
                        col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){
                            public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                                return new SimpleStringProperty(param.getValue().get(j).toString());
                            }
                        });

                        SearchTable.getColumns().addAll(col);
                    }
                    while(rs.next()){
                        //Iterate Row
                        ObservableList<String> row = FXCollections.observableArrayList();
                        for(int i=1 ; i<=rs.getMetaData().getColumnCount(); i++){
                            //Iterate Column
                            row.add(rs.getString(i));
                        }
                        searchBill.add(row);
                    }
                    //FINALLY ADDED TO TableView
                    SearchTable.setItems(searchBill);
                    break;

                case ("Firma"):
                    searchBill.clear();
                    SearchTable.getColumns().clear();
                    query = "select * from fatura where Firma = " + "'"+value2+"'";
                    rs = connectivity.myConn.createStatement().executeQuery(query);
                    for(int i=0 ; i<rs.getMetaData().getColumnCount(); i++){
                        //We are using non property style for making dynamic table
                        final int j = i;
                        TableColumn col = new TableColumn(rs.getMetaData().getColumnName(i+1));
                        col.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ObservableList,String>,ObservableValue<String>>(){
                            public ObservableValue<String> call(TableColumn.CellDataFeatures<ObservableList, String> param) {
                                return new SimpleStringProperty(param.getValue().get(j).toString());
                            }
                        });

                        SearchTable.getColumns().addAll(col);
                    }
                    while(rs.next()){
                        //Iterate Row
                        ObservableList<String> row = FXCollections.observableArrayList();
                        for(int i=1 ; i<=rs.getMetaData().getColumnCount(); i++){
                            //Iterate Column
                            row.add(rs.getString(i));
                        }
                        bill.add(row);

                    }
                    //FINALLY ADDED TO TableView
                    Table.setItems(searchBill);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**********************************************/


    /**
     * Table javafx tablosuna fatura sql tablo verilerini yazdırır.
     */

    @FXML
    void read_image(ActionEvent event) throws SQLException {
        //FillAll();
        Mat img = new Mat();
        img = imread(selected.getAbsolutePath());
        String text = new String();
        text = recog.extractTextFromImage(img);
        System.out.println(text);
        Parse(text);
        FillAll();

        navPane.setVisible(true);
    }

    /**********************************************/


    /**
     * UI üzerinde bulunan navPane adındaki Pane yapısını açıp kapatan metodlar.
     * UI üzerinde bulunan AdvancedSettings Pane yapısını açıp kapatan metodlar.
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



    @FXML
    void openSettings(ActionEvent event) { advancedSettings.setVisible(true);}

    @FXML
    void toggle_threshold(ActionEvent event) {
        System.out.println(disableThreshold);
        if(disable_threshold.isSelected()){
            Min_thresholdSlider.setDisable(true);
            Max_thresholdSlider.setDisable(true);
            disableThreshold = true;
        }
        else{
            Min_thresholdSlider.setDisable(false);
            Max_thresholdSlider.setDisable(false);
            disableThreshold = false;
        }
        System.out.println(disableThreshold);
    }
    /**********************************************/

    /**
     * Fotoğraf okunduktan sonra yeni fotoğraf açmak için kullanılan metod.
     * @param event
     */
    @FXML
    void done(ActionEvent event){
        vbox_original.getChildren().clear();
        conv_im_grid.getChildren().clear();
        System.out.println(conv_im_grid.getChildren());

    }

    /**********************************************/

    @FXML
    void updateValues(ActionEvent event) {
        System.out.println("Updated");
        double alpha = AlphaSlider.getValue();
        ALPHA = (int) alpha;
        double beta = BetaSlider.getValue();
        BETA = (int) beta;
        double min_threshold = Min_thresholdSlider.getValue();
        MIN_THRESHOLD = (int) min_threshold;
        double max_threshold = Max_thresholdSlider.getValue();
        MAX_THRESHOLD = (int) max_threshold;

    }

    @FXML
    void openSearch(ActionEvent event) {
        searchPane.setVisible(true);
        advancedSettings.setVisible(false);
        SearchTable.setVisible(true);
        Table.setVisible(false);
    }

    @FXML
    void openAllData(ActionEvent event) {
        SearchTable.setVisible(false);
        Table.setVisible(true);
    }

    @FXML
    void toggle_brightness(ActionEvent event) {
        if(disable_brightness.isSelected()){
            AlphaSlider.setDisable(true);
            BetaSlider.setDisable(true);
        }
        else{
            AlphaSlider.setDisable(false);
            BetaSlider.setDisable(false);
        }
    }


    void Parse(String text) throws SQLException {
        connectivity.ConnectDB();
        //declaring ArrayList with initial size n
        ArrayList<String> kdvList = new ArrayList<String>();
        ArrayList<String> tarihList = new ArrayList<String>();
        ArrayList<String> fisNoList = new ArrayList<String>();
        ArrayList<String> toplamList = new ArrayList<String>();
        ArrayList<String> firmaList = new ArrayList<>();
        //kdvList
        kdvList.add("TOPKDU");
        kdvList.add("TOPKDU.");
        kdvList.add("TOPKDV");
        //toplam
        toplamList.add("TOPLAM");
        toplamList.add("TOPLAN");
        toplamList.add("ARATOPLAM");
        toplamList.add("ARATOPLAN");
        toplamList.add("TUTAR");
        toplamList.add("TUTARI");
        toplamList.add("TUTARI");
        //tarihList
        tarihList.add("TARİH");
        tarihList.add("TARIH");
        tarihList.add("TARiH");
        tarihList.add("TARH");
        tarihList.add("TARIh");
        tarihList.add("TARih");
        tarihList.add("TAREH");
        //fisNolist
        fisNoList.add("FİŞ");
        fisNoList.add("FIS");
        fisNoList.add("FİS");
        fisNoList.add("FIŞ");
        fisNoList.add("FiS");
        fisNoList.add("FiŞ");
        fisNoList.add("FiŞ NO");
        fisNoList.add("FİŞ NO");
        //firmaList
        firmaList.add("A.Ş");
        firmaList.add("A.S");
        firmaList.add("A.Ş.");
        firmaList.add("A.S.");
        ArrayList <String> junkies = new ArrayList<>();
        int i =0;
        StringTokenizer st1 = new StringTokenizer(text, " ");

        String kdv = new String();
        String toplam = new String();
        String tarih = new String();
        String fisNo = new String();
        String firma = new String();
        boolean found_firma = false;
        boolean found_kdv = false;
        boolean found_toplam = false;
        boolean found_tarih = false;
        boolean found_fisNo = false;

        while (st1.hasMoreTokens()){
            junkies.add(st1.nextToken());
        }

        for (int j = 0; j < junkies.size()&& !found_kdv; j++) {
            for (i=0;i<kdvList.size();i++)
            {
                if(junkies.get(j).contains(kdvList.get(i)))
                {
                    kdv = junkies.get(j+1);
                    kdv = kdv.replace("-","");
                    kdv = kdv.replace("\n","");
                    kdv = kdv.replace("*","");
                    kdv = kdv.replace(" ","");
                    kdv = kdv.replace("TOPLAM","");
                    System.out.println("KDV ="+kdv);
                    found_kdv = true;
                    break;
                }
            }
        }
        for (int j = 0; j < junkies.size()&& !found_toplam; j++) {
            for (i=0;i<toplamList.size();i++)
            {
                if(junkies.get(j).contains(toplamList.get(i)))
                {
                    toplam = junkies.get(j+1);
                    toplam = toplam.replace("x","");
                    toplam = toplam.replace("—","");
                    toplam = toplam.replace("*","");
                    toplam = toplam.replace("ı","");
                    toplam = toplam.replace(" ","");
                    toplam = toplam.replace("\n","");
                    //int fixedFiyat = Integer.parseInt(fixedToplam);
                    System.out.println("Toplam Fiyat ="+toplam);
                    found_toplam = true;
                }
            }
        }
        for (int j = 0; j < junkies.size()&& !found_tarih; j++) {
            for (i=0;i<tarihList.size();i++)
            {
                if(junkies.get(j).contains(tarihList.get(i)))
                {
                    tarih = junkies.get(j+2);
                    tarih = tarih.replace("OAT","");
                    tarih = tarih.replace("SAAT","");
                    tarih = tarih.replace("\n","");
                    tarih = tarih.replace(" ","");
                    tarih = tarih.replace("-","");
                    tarih = tarih.replace("*","");
                    System.out.println("Tarih ="+tarih);
                    found_tarih = true;
                }
            }
        }
        for (int j = 0; j < junkies.size()&& !found_fisNo; j++) {
            for (i=0;i<fisNoList.size();i++)
            {
                if(junkies.get(j).contains(fisNoList.get(i)))
                {

                    fisNo = junkies.get(j+3);
                    fisNo = fisNo.substring(0,4);
                    fisNo = fisNo.replace(" ","");
                    fisNo = fisNo.replace("-","");
                    fisNo = fisNo.replace("*","");
                    System.out.println("FisNo ="+fisNo);
                    found_fisNo = true;
                }
            }
        }
        for (int j = 0; j < junkies.size() && !found_firma;    j++) {
            for (i=0;i<firmaList.size();i++)
            {
                if(junkies.get(j).contains(firmaList.get(i)))
                {
                    for (int k = 0; k < j; k++) {
                        firma += " ";
                        firma += junkies.get(k);
                    }
                    firma = firma.replace("-","");
                    firma = firma.replace("*","");
                    found_firma = true;
                    System.out.println("Firma ="+firma);
                    break;
                }
            }
        }
        Connectivity conn = new Connectivity();
        conn.InsertBill(connectivity.myConn,tarih,fisNo,kdv,toplam,firma);
    }




    @Override
    public void initialize(URL location, ResourceBundle resources) { paramCBox.setItems(Columns); connectivity.ConnectDB(); }



}