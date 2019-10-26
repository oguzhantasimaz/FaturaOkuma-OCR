package sample;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Connectivity {
    public static Connection myConn;
    public static Statement statement;

    public void ConnectDB(){
        String url = "jdbc:mysql://localhost:3306/faturalar";
        String user = "root";
        String password = "patates";

        try{
            myConn = DriverManager.getConnection(url,user,password);
            statement= myConn.createStatement();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }
    public void InsertBill(Connection connection, String Tarih, String Fis_no, String KDV,String Toplam_Fiyat , String Firma_Adı) throws SQLException {
        ConnectDB();
        Statement query = connection.createStatement();
        query.executeUpdate("INSERT INTO fatura VALUES (" + "'" + Tarih + "'" + ","+ "'" + Fis_no + "'" +"," + "'" + KDV + "'" +"," + "'" +Toplam_Fiyat + "'" + "," + "'" + Firma_Adı + "'" + ")");
    }

}