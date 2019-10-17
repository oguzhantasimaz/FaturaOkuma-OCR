package sample;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Connectivity {
    public static Connection myConn;
    public static Statement statement;

    public void ConnectDB(){
        String url = "jdbc:mysql://localhost:3307/Proje";
        String user = "root";
        String password = "";

        try{
            myConn = DriverManager.getConnection(url,user,password);
            statement= myConn.createStatement();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }
}