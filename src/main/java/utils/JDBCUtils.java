/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 *
 * @author duyplus
 */
public class JDBCUtils {

    private static Connection conn;
    public static String SERVER = "localhost";
    public static String PORT = "1433";
    public static String DBNAME = "JAVA3_ASM";
    public static String USERNAME = "sa";
    public static String PASSWORD = "123456";

    public static Connection getConnection() {
        if (conn == null) {
            try {
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                String url = "jdbc:sqlserver://" + SERVER + ":" + PORT + ";databaseName=" + DBNAME;
                return DriverManager.getConnection(url, USERNAME, PASSWORD);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return conn;
    }
}
