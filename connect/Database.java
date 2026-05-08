package connect;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static Database instance;
    private Connection connection;

    private Database() {
        try {
            
        	String url = "jdbc:sqlserver://localhost:1433;databaseName=QuanLyBanVePhim;encrypt=true;trustServerCertificate=true;";            String user = "sa"; // User SQL của bạn
            String password = "123456"; // Password SQL của bạn
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}