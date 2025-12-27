/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.petualanganbelajar.db;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.File;
/**
 *
 * @author DD
 */
public class DatabaseConnection {
    // Lokasi database relatif terhadap folder proyek
    private static final String DB_URL = "jdbc:sqlite:resources/db/game.db";
    private static Connection connection = null;

    public static Connection connect() {
        try {
            if (connection == null || connection.isClosed()) {
                // Pastikan folder resources/db ada
                File dbDir = new File("resources/db");
                if (!dbDir.exists()) {
                    dbDir.mkdirs(); 
                }

                // Load Driver & Buka Koneksi
                Class.forName("org.sqlite.JDBC"); 
                connection = DriverManager.getConnection(DB_URL);
                System.out.println("LOG: Terhubung ke database -> " + DB_URL);
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("ERROR: Gagal koneksi database!");
            e.printStackTrace();
        }
        return connection;
    }

    public static void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
